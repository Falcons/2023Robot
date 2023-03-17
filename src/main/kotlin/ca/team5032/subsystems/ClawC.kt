package ca.team5032.subsystems
import ca.team5032.GRAB_MOTOR_ID
import ca.team5032.INTAKE_MOTOR_ID
import ca.team5032.OBJECT_SENSOR_ID
import ca.team5032.Romance
import ca.team5032.commands.ClawIntakeCommand
import ca.team5032.utils.DoubleProperty
import ca.team5032.utils.Subsystem
import ca.team5032.utils.Tabbed
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.XboxController
import kotlin.math.abs

class ClawC : Subsystem<ClawC.State>("Claw", State.Idle), Tabbed {

    companion object {
        // The default power of the intake motor.
        val IntakeConePower = DoubleProperty("Intake Power", 0.20)
        val GrabPower = DoubleProperty("Grab Motor Power", 0.60)
        val IntakeCubePower = DoubleProperty("Intake Cube Power", 0.10)
        val OutTakePower = DoubleProperty("Eject Power", -0.1)
        val MaxCompressEncoderValue = DoubleProperty("Max Compress Encoder Value", -18000.0)
    }

    sealed class State {
        object  Intaking : State()
        object Ejecting : State()
        object Idle: State()
    }

    val intakeMotor = WPI_TalonFX(INTAKE_MOTOR_ID)
    val grabMotor = WPI_TalonFX(GRAB_MOTOR_ID)
    private val grab = grabMotor.isRevLimitSwitchClosed
    private val objectSensor = DigitalInput(3)
    private val backUpSense = DigitalInput(1)
    private var power = 0.0
    var hasObject = false

    init {
        resetGrabLimits()
        intakeMotor.setNeutralMode(NeutralMode.Brake)
        grabMotor.setNeutralMode(NeutralMode.Brake)
        grabMotor.selectedSensorPosition = 0.0
        tab.addNumber("Grab position",::getGrabPos)
//        tab.add("State",state)
        tab.addString("claw state") { state.javaClass.simpleName }
        tab.addNumber("Power", ::getPower)
        tab.addNumber("Sensor", ::showSensor)
        tab.addNumber("Intake Current",::showIntakeCurrent)
        intakeMotor.set(0.0)
        grabMotor.set(0.0)
    }

    override fun periodic() {
        state.let {
            when (it) {
                is State.Intaking -> {
                    if (Romance.selectItem.state is SelectItem.State.Cone) {}
                    else {}
                }
                is State.Ejecting -> intakeMotor.set(0.0)
                is State.Idle -> {
                    holdObject()
                    grabMotor.set(0.0)
                    intakeMotor.set(0.0)
                }
            }
        }
        if (grabMotor.isFwdLimitSwitchClosed == 1) {grabMotor.selectedSensorPosition = 0.0}
    }

    fun intake() {changeState(State.Intaking)}
    fun eject() {changeState(State.Ejecting)}
    fun stop() {changeState(State.Idle)}
    fun zeroGrab() {
        grabMotor.selectedSensorPosition=0.0
    }
    private fun getGrabPos(): Double {
        return grabMotor.selectedSensorPosition
    }

    private fun getPower(): Double {
        return power
    }

    fun hasObject() = objectSensor.get()
    fun hasBackUp() = backUpSense.get()

    private fun holdObject() {
//        if (Romance.selectItem.state is SelectItem.State.Cone) {
//            // constantly compress
//            intakeMotor.set(0.0)
//            intakeMotor.setNeutralMode(NeutralMode.Brake)
//            grabMotor.set(-0.15)
//        } else if (Romance.selectItem.state is SelectItem.State.Cube) {
//            // for cube constantly intake
//            intakeMotor.set(-0.1)
//            grabMotor.set(0.0)
//            grabMotor.setNeutralMode(NeutralMode.Brake)
//        }
//        else {
//            grabMotor.set(0.0)
//            intakeMotor.set(0.0)
//            grabMotor.setNeutralMode(NeutralMode.Brake)
//            intakeMotor.setNeutralMode(NeutralMode.Brake)
//        }
    }

    private fun showSensor(): Double {
        if (hasObject) return 1.0
        return 0.0
    }

    private fun showIntakeCurrent(): Double {
        return intakeMotor.statorCurrent
    }

    fun resetGrabLimits() {
        grabMotor.configForwardSoftLimitEnable(true)
        grabMotor.configReverseSoftLimitEnable(true)
        grabMotor.configForwardSoftLimitThreshold(0.0)
        grabMotor.configReverseSoftLimitThreshold(MaxCompressEncoderValue.value)
    }
}