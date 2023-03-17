//package ca.team5032.subsystems
//import ca.team5032.GRAB_MOTOR_ID
//import ca.team5032.INTAKE_MOTOR_ID
//import ca.team5032.OBJECT_SENSOR_ID
//import ca.team5032.Romance
//import ca.team5032.utils.DoubleProperty
//import ca.team5032.utils.Subsystem
//import ca.team5032.utils.Tabbed
//import com.ctre.phoenix.motorcontrol.ControlMode
//import com.ctre.phoenix.motorcontrol.NeutralMode
//import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
//import edu.wpi.first.wpilibj.DigitalInput
//import edu.wpi.first.wpilibj.XboxController
//import kotlin.math.abs
//
//class Claw : Subsystem<Claw.State>("Claw", State.IdleIntake), Tabbed {
//
//    companion object {
//        // The default power of the intake motor.
//        val IntakePower = DoubleProperty("Intake Power", 0.20)
//        val GrabPower = DoubleProperty("Grab Motor Power", 0.60)
//        val IntakeCubePower = DoubleProperty("Intake Cube Power", 0.10)
//        val OutTakePower = DoubleProperty("Eject Power", -0.1)
//    }
//
//    sealed class State {
//        object  Intaking : State()
//        object Ejecting : State()
//        object Release : State()
//        object Grab : State()
//        object IdleIntake : State()
//        object IdleGrab: State()
//        object Compress: State()
//        object CompressIntake: State()
//    }
//
//    val intakeMotor = WPI_TalonFX(INTAKE_MOTOR_ID)
//    val grabMotor = WPI_TalonFX(GRAB_MOTOR_ID)
//
//    //private val objectSensor = DigitalInput(OBJECT_SENSOR_ID)
//    private val controller: XboxController = Romance.peripheralController
//    private var power = 0.0
//
//    private var feedForwardPower = -0.31
//
//
//    private val has_Y_input: Boolean
//        get() = abs(controller.leftY) > 0.05
//
//
//    private var testCompress = false
//    private var testCompressPos = 0.0
//
//    init {
//        intakeMotor.setNeutralMode(NeutralMode.Brake)
//        grabMotor.setNeutralMode(NeutralMode.Brake)
//        grabMotor.selectedSensorPosition = 0.0
//        tab.addNumber("Grab position",::getGrabPos)
////        tab.add("State",state)
//        tab.addString("claw state") { state.javaClass.simpleName }
//        tab.addNumber("Power", ::getPower)
//    }
//
//    override fun periodic() {
//
////        power = controller.leftY*0.35
////        grabMotor.set(power)
//
//        state.let {
////            if (it is State.IdleGrab && testCompress) {
////                grabMotor.set(-0.25)
////                //val pos = grabMotor.selectedSensorPosition
//////                grabMotor.set(ControlMode.Position, 5000.0)
//////                grabMotor.setNeutralMode(NeutralMode.Brake)
////            }
////
//            when (it) {
//                is State.Intaking -> intakeMotor.set(-IntakePower.value)
//                //is State.Intaking ->
//                is State.Ejecting -> intakeMotor.set(IntakePower.value)
//                is State.Grab -> grabMotor.set(GrabPower.value)
//                is State.Release -> grabMotor.set(-GrabPower.value)
//
//                is State.IdleIntake -> {
//                    intakeMotor.set(0.0)
//                    intakeMotor.setNeutralMode(NeutralMode.Brake)
//                }
//                is State.IdleGrab -> {
//                    grabMotor.set(0.0)
//                    grabMotor.setNeutralMode(NeutralMode.Brake)
//                }
//                is State.Compress -> {
//                    grabMotor.set(-0.25)
//                }
//                is State.CompressIntake -> intakeMotor.set(-0.1)
//            }
//        }
//    }
//
//    fun intake() {changeState(State.Intaking)}
//    fun eject() {changeState(State.Ejecting)}
//    fun grab() {changeState(State.Grab)}
//    fun release() {changeState(State.Release)}
//    fun stopIntake() {changeState(State.IdleIntake)}
//    fun stopGrab() {changeState(State.IdleGrab)}
//    fun compressGrab() {changeState(State.Compress)}
//    fun compressIntake() {changeState(State.CompressIntake)}
//    fun zeroGrab() {
//        // zero out every motor
//        grabMotor.selectedSensorPosition=0.0
////        Romance.boomOne.boomOneMotor.selectedSensorPosition = 0.0
////        Romance.boomTwo.boomTwoMotor.selectedSensorPosition = 0.0
////        Romance.pivot.pivotMotor.selectedSensorPosition = 0.0
////        Romance.wrist.wristMotor.selectedSensorPosition = 0.0
//        testCompress = true
//    }
//    fun cancelGrab() {testCompress = false}
//
//    private fun getGrabPos(): Double {
//        return grabMotor.selectedSensorPosition
//    }
//
//    private fun getPower(): Double {
//        return power
//    }
//
//    //private fun setSetPoint()
//
//    //fun hasObject() = !objectSensor.get()
//
//}