//package ca.team5032.subsystems
//import ca.team5032.*
//import ca.team5032.commands.RotateArm
//import ca.team5032.utils.DoubleProperty
//import ca.team5032.utils.Subsystem
//import ca.team5032.utils.Tabbed
//import com.ctre.phoenix.motorcontrol.NeutralMode
//import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
//import edu.wpi.first.math.controller.PIDController
//import edu.wpi.first.wpilibj.DigitalInput
//import edu.wpi.first.wpilibj.XboxController
//import edu.wpi.first.wpilibj2.command.PIDSubsystem
//import kotlin.math.abs
//import kotlin.math.sin
//
//
//class Arm : Subsystem<Arm.State>("Arm", State.Idle), Tabbed {
//
//
//    companion object {
//        // The default power of the intake motor.
////        val IntakePower = DoubleProperty("Intake Power", 0.20)
////        val GrabPower = DoubleProperty("Grab Motor Power", 0.60)
//        //val guessedPVal = DoubleProperty("Pivot Motor Power P values", 1.3)
//    }
//
//    sealed class State {
//        object Pivoting : State()
//        object BoomOne : State()
//        object BoomTwo : State()
//        object Idle : State()
//        object Auto : State()
//    }
//
//    val pivotMotor = WPI_TalonFX(PIVOT_MOTOR)
//    val boomOneMotor = WPI_TalonFX(BOOM_ONE_ID)
//    val boomTwoMotor = WPI_TalonFX(BOOM_TWO_ID)
//
//    private val controller: XboxController = Romance.peripheralController
//    private val second_controller: XboxController = Romance.driveController
//
//    private val pivotLimitReverse = DigitalInput(0)
//    private val pivotLimitForward = DigitalInput(1)
//
//
//    private var setPoint = 0.0
//    private var tickToDegreeConst = -2330.4
//    // -176512 -> -2313.39 t/d
//    // -177149 -> -2321.74 t/d
//    // -177417 -> -2325.255 t/d
//    // -180194 -> -2361.65 t/d
//    // avg = -2330 t/d
//    val arm_controller = PIDController(0.1,0.0,0.0)
//    private var power = 0.0
//    private var boomOneExtensionPower = 0.0
//    private var boomTwoExtensionPower = 0.0
//
//
//    init {
//        pivotMotor.setNeutralMode(NeutralMode.Brake)
//        boomOneMotor.setNeutralMode(NeutralMode.Brake)
//        boomTwoMotor.setNeutralMode(NeutralMode.Brake)
//
//        pivotMotor.selectedSensorPosition = 0.0
////        tab.addNumber("Grab position",::getGrabPos)
////        tab.add("State",state)
////        tab.addString("claw state") { state.javaClass.simpleName }
//        tab.addNumber("Pivot Position", ::getCurrentPosition)
//        tab.addNumber("Pivot angle", ::showDegrees)
//        tab.addInteger("Pivot Switch Reverse", ::LimitSwitch)
//        tab.addInteger("Pivot Switch Front", ::hasForwardLimitSwitch)
//        tab.addInteger("Boom One Forward",::hasForwardLimitSwitchBoomOne)
//        tab.addInteger("Boom One Rev",::hasRevLimitSwitchBoomOne)
//        tab.addInteger("Boom Two Forward",::hasForwardLimitSwitchBoomTwo)
//        tab.addInteger("Boom Two Rev",::hasRevLimitSwitchBoomTwo)
//        tab.addNumber("Pivot Current", ::displayCurrent)
//        tab.addNumber("Pivot Power", ::getPower)
//
//    }
//
//    data class rotationalInput(var power: Double)
////    data class boomOneExtensionInput(var power: Double)
////    data class boomTwoExtensionInput(var power: Double)
//    val commandRotationalInput = rotationalInput(0.0)
//    private fun motorOutputs(): rotationalInput {
//        return when (state) {
//            is State.Idle -> rotationalInput(0.0)
//            is State.Pivoting -> rotationalInput(controller.leftY*0.8)
//            is State.BoomOne -> rotationalInput(second_controller.leftY*0.7)
//            is State.BoomTwo -> rotationalInput(second_controller.rightY*0.7)
//            is State.Auto -> commandRotationalInput
//        }
//    }
//
//    private val hasPivotInput: Boolean
//        get() = abs(controller.leftY) > 0.07
//    private val hasBoomOneInput: Boolean
//        get() = abs(second_controller.leftY) > 0.07
//    private val hasBoomTwoInput: Boolean
//        get() = abs(second_controller.rightY) > 0.07
//
//    override fun periodic() {
////        if (!pivotLimit.get()){
////            rotationalInput(0.0)
////            zeroEncoder()
////        }
//        var (rotationSpeed) = motorOutputs()
//        power = rotationSpeed
//        if (state !is State.Auto) {
//            if (hasPivotInput){
//                changeState(State.Pivoting)
//                pivotMotor.set(power)
//            }
//            else if (hasBoomOneInput){
//                changeState(State.BoomOne)
//                boomOneMotor.set(power)
//            }
//            else if (hasBoomTwoInput){
//                changeState(State.BoomTwo)
//                boomTwoMotor.set(power)
//            }
//            else if (state !is State.Idle) {
//                changeState(State.Idle)
//                pivotMotor.set(power)
//                boomOneMotor.set(power)
//                boomTwoMotor.set(power)
//            }
//        }
//
//    }
//
//    fun setSetPoint(encoderSetVal: Double) {
//        setPoint = encoderSetVal
//    }
//
//    fun getSetPoint(): Double {
//        return setPoint
//    }
//
//    fun getCurrentPosition(): Double {
//        return pivotMotor.selectedSensorPosition
//    }
//
//    fun getError(): Double {
//        return convertToDegrees(getSetPoint())-convertToDegrees(getCurrentPosition())
//    }
//
//    fun convertToDegrees(ticks: Double): Double {
//        return abs(ticks/tickToDegreeConst)
//    }
//
//    fun showDegrees(): Double {
//        return abs(pivotMotor.selectedSensorPosition/tickToDegreeConst)
//    }
//
//    fun zeroEncoder() {
//        pivotMotor.selectedSensorPosition = 0.0
//    }
//
//    private fun calculateTorque(): Double{
//        val armAngle = convertToDegrees(getCurrentPosition())
//        val radius = 0.7
//        val force = 90
//        return force*radius*sin(armAngle)
//    }
//
//    fun showPIDOutput(): Double {
//        val rotateArm = RotateArm(30.0)
//        return rotateArm.remapped
//    }
//
//    private fun LimitSwitch(): Long {
//        if (pivotMotor.isRevLimitSwitchClosed == 0) {return 0}
//        else{return 1}
//    }
//
//    private fun hasForwardLimitSwitch(): Long {
//        if (pivotMotor.isFwdLimitSwitchClosed == 0) {return 0}
//        else{return 1}
//    }
//
//    private fun hasForwardLimitSwitchBoomOne(): Long {
//        if (boomOneMotor.isFwdLimitSwitchClosed == 0) {return 0}
//        else{return 1}
//    }
//
//    private fun hasRevLimitSwitchBoomOne(): Long {
//        if (boomOneMotor.isRevLimitSwitchClosed == 0) {return 0}
//        else{return 1}
//    }
//
//    private fun hasForwardLimitSwitchBoomTwo(): Long {
//        if (boomTwoMotor.isFwdLimitSwitchClosed == 0) {return 0}
//        else {return 1}
//    }
//    private fun hasRevLimitSwitchBoomTwo(): Long {
//        if (boomTwoMotor.isRevLimitSwitchClosed == 0) {return 0}
//        else{return 1}
//    }
//
//    private fun displayCurrent(): Double {
//        return pivotMotor.statorCurrent
//    }
//
//    private fun getPower(): Double {
//        return power
//    }
//    // 0.075 power
//    // initialize function
//}