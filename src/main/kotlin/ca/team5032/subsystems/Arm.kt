package ca.team5032.subsystems
import ca.team5032.*
import ca.team5032.commands.ArmPositions.*
import ca.team5032.commands.ExtendBoomOneCommand
import ca.team5032.commands.ExtendBoomTwoCommand
import ca.team5032.commands.PivotCommand
import ca.team5032.commands.WristCommand
import ca.team5032.utils.DoubleProperty
import ca.team5032.utils.Subsystem
import ca.team5032.utils.Tabbed
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.XboxController
import kotlin.math.abs

class Arm : Subsystem<Arm.State>("Arm", State.Idle), Tabbed {

    companion object {
        // The default power of the intake motor.
        val MaxEncoderExtensionBoomOne = DoubleProperty("Boom One Max Extension", -680000.0)
        val MotorPowerBoomOne = DoubleProperty("Boom One Power", 0.9)
        val MaxEncoderExtensionBoomTwo = DoubleProperty("Boom Two Max Extension", -560000.0)
        val MotorPowerBoomTwo = DoubleProperty("Boom Two Power", 0.9)
        val PivotPower = DoubleProperty("Pivot Power", 0.45)
        val MaxEncoderExtensionPivot = DoubleProperty("Pivot Max Extension", -190000.0)
        val WristPower = DoubleProperty("Wrist Power", 0.25)
        val MaxEncoderExtensionWrist = DoubleProperty("Max Wrist Extension", -70000.0)
    }

    sealed class State {
        object LowIntake : State()
        object HighIntake : State()
        object HighPost : State()
        object MidPost : State()
        object Idle: State()
        object Stow: State()
        object Moving : State()
    }

    val pivotMotor = WPI_TalonFX(PIVOT_MOTOR)
    val boomOneMotor = WPI_TalonFX(BOOM_ONE_ID)
    val boomTwoMotor = WPI_TalonFX(BOOM_TWO_ID)
    val wristMotor = WPI_TalonFX(WRIST_MOTOR_ID)

    var mCancelCommand = false

    private val controller: XboxController = Romance.peripheralController
    private var power = 0.0

    private var testCompress = false
    private var testCompressPos = 0.0

    private var lowIntakeTestFinish = false

    init {
        mCancelCommand = false
        resetPivotLimits()
        resetWristLimits()
        resetBoomTwoLimits()
        resetBoomOneLimits()
        pivotMotor.setNeutralMode(NeutralMode.Brake)
        boomOneMotor.setNeutralMode(NeutralMode.Brake)
        boomTwoMotor.setNeutralMode(NeutralMode.Brake)
        wristMotor.setNeutralMode(NeutralMode.Brake)
        boomOneMotor.selectedSensorPosition = 0.0
        boomTwoMotor.selectedSensorPosition = 0.0
//        tab.add("State",state)
        tab.addString("Arm state") { state.javaClass.simpleName }
        tab.addNumber("Pivot Power",::showPivotPower)
        tab.addBoolean("Low intake finish",::showFinishIntake)
    }

    override fun periodic() {
        // cone commands
        //CANCEL PREVIOUS COMMAND BEFORE RUNNING
        if (Romance.selectItem.state is SelectItem.State.Cone) {
            state.let {
                when (it) {
                    is State.Moving -> idleMotors()
                    is State.LowIntake -> idleMotors()
                    is State.HighIntake -> idleMotors()
                    is State.HighPost -> idleMotors()
                    is State.MidPost -> idleMotors()
                    is State.Stow -> idleMotors()
                    is State.Idle -> {
//                        LowIntakeCone().cancel()
//                        MidPostCone().cancel()
//                        HighPostCone().cancel()
//                        StowArm().cancel()
//                        pivotMotor.set(0.0)
//                        boomOneMotor.set(0.0)
//                        boomTwoMotor.set(0.0)
//                        wristMotor.set(0.0)
                        idleMotors()
                    }
                }
            }
        } else {
            state.let {
                when (it) {
                    is State.Moving -> idleMotors()
                    is State.LowIntake -> idleMotors()
                    is State.HighIntake -> idleMotors()
                    is State.HighPost -> idleMotors()
                    is State.MidPost -> idleMotors()
                    is State.Stow -> idleMotors()
                    is State.Idle -> {
                        idleMotors()
                    }
                }
            }
        }
        //reset encoder positions
        if (boomTwoMotor.isFwdLimitSwitchClosed == 1) {boomTwoMotor.selectedSensorPosition = 0.0}
        if (boomOneMotor.isFwdLimitSwitchClosed == 1) {boomOneMotor.selectedSensorPosition = 0.0}
        if (pivotMotor.isFwdLimitSwitchClosed == 1) {pivotMotor.selectedSensorPosition = 0.0}
        if (wristMotor.isFwdLimitSwitchClosed == 1) {wristMotor.selectedSensorPosition = 0.0}
    }

    fun lowIntake() {
        changeState(State.Moving)
        Romance.selectItem.state.let{
            when (it){
                is SelectItem.State.Cone -> {
                    LowIntakeCone().schedule()
                }
                is SelectItem.State.Cube -> {
                    LowIntakeCube().schedule()
                }
            }
        }
    }


    fun highIntake() {changeState(State.HighIntake)
        Romance.selectItem.state.let{
            when (it){
                is SelectItem.State.Cone -> {
                    HighPostCone().schedule()
                }
                is SelectItem.State.Cube -> {
                    HighPostCube().schedule()
                }
            }
        }
    }
    fun highPost() {
        changeState(State.HighPost)
        Romance.selectItem.state.let{
            when (it){
                is SelectItem.State.Cone -> {
                    HighPostCone().schedule()
                }
                is SelectItem.State.Cube -> {
                    HighPostCube().schedule()
                }
            }
        }
    }
    fun midPost() {
        changeState(State.MidPost)
        Romance.selectItem.state.let{
            when (it){
                is SelectItem.State.Cone -> {
                    MidPostCone().schedule()
                }
                is SelectItem.State.Cube -> {
                    MidPostCube().schedule()
                }
            }
        }
    }
    fun idle() {changeState(State.Idle)}
    fun stow() {
        changeState(State.Stow)
        StowArm().schedule()
//        Romance.selectItem.state.let{
//            when (it){
//                is SelectItem.State.Cone -> {
//                    StowArm().schedule()
//                }
//                is SelectItem.State.Cube -> {
//                    StowArm().schedule()
//                }
//            }
//        }
    }

    private fun idleMotors() {
//        LowIntakeCone().cancel()
//        MidPostCone().cancel()
//        HighPostCone().cancel()
//        StowArm().cancel()
//        PivotCommand(0.0).cancel()
//        ExtendBoomOneCommand(-1).cancel()
//        ExtendBoomTwoCommand(0.0).cancel()
//        WristCommand(0.0).cancel()
//
//        pivotMotor.set(0.0)
//        boomOneMotor.set(0.0)
//        boomTwoMotor.set(0.0)
//        wristMotor.set(0.0)
    }
    private fun showFinishIntake():Boolean {return lowIntakeTestFinish}

    fun resetBoomOneLimits(){
        boomOneMotor.configForwardSoftLimitEnable(true)
        boomOneMotor.configReverseSoftLimitEnable(true)
        boomOneMotor.configReverseSoftLimitThreshold(MaxEncoderExtensionBoomOne.value)
        boomOneMotor.configForwardSoftLimitThreshold(0.0)
    }
    fun resetBoomTwoLimits(){
        boomTwoMotor.configForwardSoftLimitEnable(true)
        boomTwoMotor.configReverseSoftLimitEnable(true)
        boomTwoMotor.configReverseSoftLimitThreshold(MaxEncoderExtensionBoomTwo.value)
        boomTwoMotor.configForwardSoftLimitThreshold(0.0)
    }
    fun resetPivotLimits(){
        pivotMotor.configForwardSoftLimitEnable(true)
        pivotMotor.configReverseSoftLimitEnable(true)
        pivotMotor.configReverseSoftLimitThreshold(MaxEncoderExtensionPivot.value)
        pivotMotor.configForwardSoftLimitThreshold(0.0)
    }
    fun resetWristLimits(){
        wristMotor.configForwardSoftLimitEnable(true)
        wristMotor.configReverseSoftLimitEnable(true)
        wristMotor.configReverseSoftLimitThreshold(MaxEncoderExtensionWrist.value)
        wristMotor.configForwardSoftLimitThreshold(0.0)
    }
    fun showPivotPower(): Double{
        return pivotMotor.motorOutputVoltage
    }

    fun cancelCommand() {
        mCancelCommand = true
        changeState(State.Idle)
    }
}