package ca.team5032.subsystems
import ca.team5032.GRAB_MOTOR_ID
import ca.team5032.INTAKE_MOTOR_ID
import ca.team5032.OBJECT_SENSOR_ID
import ca.team5032.Romance
import ca.team5032.commands.ClawPositions.EjectObject
import ca.team5032.commands.ClawPositions.FallenIntakeCone
import ca.team5032.commands.ClawPositions.IntakeCone
import ca.team5032.commands.ClawPositions.IntakeCube
import ca.team5032.utils.DoubleProperty
import ca.team5032.utils.Subsystem
import ca.team5032.utils.Tabbed
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import edu.wpi.first.wpilibj.DigitalInput

class ClawC : Subsystem<ClawC.State>("Claw", State.Idle), Tabbed {

    companion object {
        // The default power of the intake motor.
        val IntakeConePower = DoubleProperty("Intake Power", 0.40)
        val GrabPower = DoubleProperty("Grab Motor Power", 0.60)
        val IntakeCubePower = DoubleProperty("Intake Cube Power", 0.40)
        val OutTakePower = DoubleProperty("Eject Power", -0.1)
        val MaxCompressEncoderValue = DoubleProperty("Max Compress Encoder Value", -16000.0)
    }

    sealed class State {
        object  Intaking : State()
        object Ejecting : State()
        object Idle: State()
    }

    var mCancelIntake = false
    val intakeMotor = WPI_TalonFX(INTAKE_MOTOR_ID)
    val grabMotor = WPI_TalonFX(GRAB_MOTOR_ID)
    private val grab = grabMotor.isRevLimitSwitchClosed
    private val objectSensor = DigitalInput(OBJECT_SENSOR_ID)
//    private val backUpSense = DigitalInput(1)
    private var power = 0.0

    //var hasObject = false

    init {
        mCancelIntake = false
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
//                    if (Romance.selectItem.state is SelectItem.State.Cone) {}
//                    else {}
                }

                is State.Ejecting -> {
                }

                is State.Idle -> {}
                    //grabMotor.set(-0.2)
//                {
//                    if (showSensor() == 1.0) {
//                        grabMotor.set(-0.15)
//                    } else {
//                        grabMotor.set(0.0)
//                        intakeMotor.set(0.0)
//                    }
//                    //holdObject()
////                    grabMotor.set(0.0)
////                    intakeMotor.set(0.0)
//                }

            }
            if (grabMotor.isFwdLimitSwitchClosed == 1) {
                grabMotor.selectedSensorPosition = 0.0
            }

        }
    }

    fun intake() {
        changeState(State.Intaking)
        Romance.selectItem.state.let{
            when (it){
                is SelectItem.State.Cone -> {
                    if (Romance.arm.state is Arm.State.FallenIntake){
                        FallenIntakeCone().schedule()
                    } else {
                        IntakeCone().schedule()
                    }
                }
                is SelectItem.State.Cube -> {
                    IntakeCube().schedule()
                }
            }
        }
    }
    fun eject() {
        changeState(State.Ejecting)
        EjectObject().schedule()
    }

    fun outTake(){
        intakeMotor.set(0.1)
    }

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
//    fun hasBackUp() = backUpSense.get()

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

        if (showSensor() == 1.0) {
            if (Romance.selectItem.state is SelectItem.State.Cone) {
                grabMotor.set(-0.25)
                intakeMotor.set(0.0)
            } else {
                intakeMotor.set(-0.07)
                grabMotor.set(0.0)
            }
        } else {
            intakeMotor.set(0.0)
            grabMotor.set(0.0)
        }
    }

    fun showSensor(): Double {
        if (hasObject()) return 1.0
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

    fun cancelIntake() {
        mCancelIntake = true
    }
}
