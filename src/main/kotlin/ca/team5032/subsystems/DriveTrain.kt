package ca.team5032.subsystems

import ca.team5032.*
import ca.team5032.utils.DoubleProperty
import ca.team5032.utils.Subsystem
import ca.team5032.utils.Tabbed
import com.ctre.phoenix.ErrorCode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import com.ctre.phoenix.sensors.PigeonIMU
import com.playingwithfusion.TimeOfFlight
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup
import kotlin.io.path.fileVisitor
import kotlin.math.abs

class DriveTrain : Subsystem<DriveTrain.State>("Drive", State.Idle), Tabbed {
    companion object {
        val DEADBAND_THRESHOLD = DoubleProperty("Deadband Threshold", 0.05)
        val PIVOT_SENSE = DoubleProperty("Rotation sensitivity", 0.3)
        val X_SENSE = DoubleProperty("X speed sensitivity",0.7)
        val Y_SENSE = DoubleProperty("Y Speed Sensitivity", 0.55)
        val MICRO_SPEED = DoubleProperty("Magnitude of Micro speed", 0.6)

    }

    // use data inputs in this class to apply to motors
    data class DriveInput(var ySpeed: Double, var xSpeed: Double, var pivot: Boolean, var brakes: Boolean)
    val autonomousInput = DriveInput(0.0,0.0,true,true)
    val gyroController = PIDController(0.07, 0.0, 0.0)
    sealed class State {
        object Autonomous : State()
        object Driving : State()
        object Pivot : State()
        object OverrideDrive : State()
        object Idle : State()
        object SlowPivotLeft : State()
        object SlowPivotRight : State()
    }

    private val leftFront = WPI_TalonFX(LEFT_FRONT_ID)
    private val leftRear = WPI_TalonFX(LEFT_REAR_ID)
    private val rightFront = WPI_TalonFX(RIGHT_FRONT_ID)
    private val rightRear = WPI_TalonFX(RIGHT_REAR_ID)

    private val TofF = TimeOfFlight(TIME_OF_FLIGHT_ID)
    val gyro = PigeonIMU(GYRO_ID)

    private val m_left = MotorControllerGroup(leftFront, leftRear)
    private val m_right = MotorControllerGroup(rightFront, rightRear)
    val m_drive = DifferentialDrive(m_left,m_right)


    private val kMaxSpeed = 3.0
    private val k_trackWidth = 1.0
    private val kIncoderResolution = 2048

    private val m_leftPIDController = PIDController(1.0,0.0,0.0)
    private val m_rightPIDController = PIDController(1.0,0.0,0.0)
    private val m_kinematics = DifferentialDriveKinematics(k_trackWidth)
    private val m_odometry: DifferentialDriveOdometry? = null

    private val controller: XboxController = Romance.driveController

    private val hasYInput: Boolean
        get() = abs(controller.leftY) > DEADBAND_THRESHOLD.value

    private val hasYMovement: Boolean
        get() = leftFront.selectedSensorVelocity > 0.1

    private val hasXInput: Boolean
        get() = abs(controller.rightX) > DEADBAND_THRESHOLD.value

    private val pivotOverride: Boolean
        //get() = controller.leftTriggerAxis >= 0.1
        get() = controller.leftBumper

    private val brakeOverride: Boolean
        //get() = controller.rightTriggerAxis >= 0.1
        get() = controller.rightBumper
    private val pivotLeftSlow: Boolean
        //get() = controller.leftBumper
        get() = controller.leftTriggerAxis >= 0.1
    private val pivotRightSlow: Boolean
        //get() = controller.rightBumper
        get() = controller.rightTriggerAxis >= 0.1


    init {
        leftFront.configClosedloopRamp(2.0)
        rightFront.configClosedloopRamp(2.0)
        leftRear.configClosedloopRamp(2.0)
        rightRear.configClosedloopRamp(2.0)
        rightFront.inverted = true
        rightRear.inverted = true

        leftFront.setNeutralMode(NeutralMode.Coast)
        leftRear.setNeutralMode(NeutralMode.Coast)
        rightFront.setNeutralMode(NeutralMode.Coast)
        rightRear.setNeutralMode(NeutralMode.Coast)

        tab.addNumber("Pitch: ",::getPitch)
        tab.addNumber("Yaw: ",::getYaw)
        tab.addNumber("Roll: ",::getRoll)

        tab.addNumber("Left Front Motor: ", ::getEncoder)
        tab.addNumber("ToF Distance: ", ::getDistToF)

        tab.addNumber("Velocities: ", ::getVelocity)

        gyroController.setTolerance(2.00)
    }

    private fun getInput(): DriveInput{
        return when (state) {
            is State.Autonomous -> DriveInput(0.0,0.0,pivot = true,brakes = true)
            is State.Driving -> DriveInput(
                -controller.leftY * Y_SENSE.value,
                -controller.rightX * PIVOT_SENSE.value,
                //pivot = false,
                pivot = true,
                brakes = false
            )
            is State.Pivot -> DriveInput(
                0.0,
                -controller.rightX * PIVOT_SENSE.value,
                //pivot = true,
                pivot = true,
                brakes = false
            )
            is State.OverrideDrive -> DriveInput(
                -controller.leftY * Y_SENSE.value,
                -controller.rightX * PIVOT_SENSE.value,
                pivot = true,
                brakes = brakeOverride
            )
            is State.SlowPivotLeft -> DriveInput(
                -controller.leftY * Y_SENSE.value,
                0.17,
                pivot = true,
                brakes = false
            )
            is State.SlowPivotRight -> DriveInput(
                -controller.leftY * Y_SENSE.value,
                -0.17,
                pivot = true,
                brakes = false
            )
//            is State.Idle -> DriveInput(
//                -controller.leftY * Y_SENSE.value,
//                -controller.rightX * PIVOT_SENSE.value,
//                pivot = true,
//                brakes = true
//            )
            else -> DriveInput(0.0, 0.0, pivot = true, brakes = false)
        }
    }
    override fun periodic() {
        // This method will be called once per scheduler run
        if (Romance.isDisabled) return
        if (state !is State.Autonomous) {
            if (pivotOverride || brakeOverride) {
                changeState(State.OverrideDrive)
            }
            else if (pivotLeftSlow) {
                changeState(State.SlowPivotLeft)
            }
            else if (pivotRightSlow) {
                changeState(State.SlowPivotRight)
            }
            else if (hasYInput || hasXInput) {
                changeState(State.Driving)
            }
//            else if (hasXInput && !hasYInput) {
//                changeState(State.Pivot)
//            }
            else if (state !is State.Idle) {
                changeState(State.Idle)
            }
        }

        var (xSpeed, ySpeed, pivot, brakes) = getInput()

//        if (pivotOverride || brakeOverride) {
//            xSpeed = -controller.leftY * Y_SENSE.value
//            ySpeed = controller.rightX * X_SENSE.value
//            pivot = pivotOverride
//            brakes = brakeOverride
//        }
        if (brakes) {
            applyBrakes()
        } else{
            disengageBrakes()
        }

        m_drive.curvatureDrive(xSpeed,ySpeed,pivot)

    }

    private fun determineCurvatureEngage(): Boolean {
        if (getVelocity()>15000.0) {
            return false
        }
        return true
    }

    fun getPitch(): Double {
        return gyro.pitch
    }
    fun getYaw(): Double {
        return gyro.yaw
    }
    fun getRoll(): Double{
        return gyro.roll
    }
    fun getEncoder(): Double {
        return leftFront.selectedSensorVelocity
    }

    fun getDistToF(): Double {
        return TofF.pidGet()
    }

    fun getVelocity(): Double {
        //get the linear velocity
        return ((abs(leftFront.selectedSensorVelocity) + abs(leftRear.selectedSensorVelocity) + abs(rightFront.selectedSensorVelocity) + abs(rightRear.selectedSensorVelocity))/4)
    }

    fun applyBrakes() {
        leftFront.setNeutralMode(NeutralMode.Brake)
        leftRear.setNeutralMode(NeutralMode.Brake)
        rightFront.setNeutralMode(NeutralMode.Brake)
        rightRear.setNeutralMode(NeutralMode.Brake)
    }
    fun disengageBrakes() {
        leftFront.setNeutralMode(NeutralMode.Coast)
        leftRear.setNeutralMode(NeutralMode.Coast)
        rightFront.setNeutralMode(NeutralMode.Coast)
        rightRear.setNeutralMode(NeutralMode.Coast)
    }

//    public fun resetDriveEncoders() {
//        leftFront.setSelectedSensorPosition(0.0)
//        rightFront.setSelectedSensorPosition(0.0)
//    }
//
//    fun getLeftEncoderPosition(): Double{
//        return leftFront.selectedSensorPosition
//    }
//
//    fun getRightEncoderPosition(): Double {
//        return -rightFront.selectedSensorPosition
//    }
//
//
//    fun getLeftEncoderVelocity(): Double{
//        return leftFront.selectedSensorVelocity
//    }
//
//    fun getRightEncoderVelocity(): Double {
//        return -rightFront.selectedSensorVelocity
//    }
//
//    fun getTurnRate(): ErrorCode? {
//        return gyro.getRawGyro()
//    }
//
//    fun getHeading(): Double {
//        return
//    }

    fun balanceOnSwitch() {
        // dharav part :)
    }

}