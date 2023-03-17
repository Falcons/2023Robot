//package ca.team5032.subsystems
//import ca.team5032.*
//import ca.team5032.utils.DoubleProperty
//import ca.team5032.utils.Subsystem
//import ca.team5032.utils.Tabbed
//import com.ctre.phoenix.motorcontrol.NeutralMode
//import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
//import edu.wpi.first.wpilibj.XboxController
//import kotlin.math.abs
//
//class BoomTwoC : Subsystem<BoomTwoC.State>("Boom Two", State.Idle), Tabbed {
//
//    companion object {
//        // The default power of the intake motor.
//        val MaxEncoderExtension = DoubleProperty("Boom Two Max Extension", -20000.0)
//        val MotorPower = DoubleProperty("Boom Two Power", 1.0)
//    }
//
//    sealed class State {
//        object Idle : State()
//        object Moving : State()
//    }
//
//    val boomTwoMotor = WPI_TalonFX(BOOM_TWO_ID)
//
//    private val controller: XboxController = Romance.driveController
//    private var power = 0.0
//
//    init {
//        boomTwoMotor.setNeutralMode(NeutralMode.Brake)
//        boomTwoMotor.selectedSensorPosition = 0.0
//        boomTwoMotor.configReverseSoftLimitEnable(false)
//        boomTwoMotor.configForwardSoftLimitEnable(false)
//        boomTwoMotor.configForwardSoftLimitThreshold(1000.0)
//        boomTwoMotor.configReverseSoftLimitThreshold(BoomTwoC.MaxEncoderExtension.value)
//        tab.addString("Boom Two state") { state.javaClass.simpleName }
//        tab.addNumber("Power", ::getPower)
//        tab.addNumber("Current", ::getCurrent)
//        tab.addInteger("Forward", ::hasForwardLimitSwitch)
//        tab.addInteger("Reverse",::hasRevLimitSwitch)
//        tab.addNumber("Sensor position",::getPosition)
//    }
//
//
//    data class rotationalInput(var power: Double)
//
//    private fun motorOutputs(): rotationalInput {
//        return when (state) {
//            is State.Idle -> rotationalInput(0.0)
//            is State.Moving -> rotationalInput(controller.rightY*1.0)
//        }
//    }
//
//    private val hasInput: Boolean
//        get() = abs(controller.rightY) > 0.07
//
//    override fun periodic() {
//        state.let {
//            if (hasInput){
//                changeState(State.Moving)
//            } else if (state !is State.Idle) {
//                changeState(State.Idle)
//            }
//        }
//        if (boomTwoMotor.isFwdLimitSwitchClosed == 1) {boomTwoMotor.selectedSensorPosition = 0.0}
//        //if (boomTwoMotor.isRevLimitSwitchClosed == 1) {boomTwoMotor.selectedSensorPosition = BoomOneC.MaxEncoderExtension.value}
//        var (boomTwoInput) = motorOutputs()
//        power = boomTwoInput
//        boomTwoMotor.set(power)
//    }
//
//    private fun getPower(): Double {
//        return power
//    }
//    private fun getCurrent(): Double {
//        return boomTwoMotor.statorCurrent
//    }
//
//    private fun hasForwardLimitSwitch(): Long {
//        if (boomTwoMotor.isFwdLimitSwitchClosed == 0) {return 0}
//        else{return 1}
//    }
//
//    private fun hasRevLimitSwitch(): Long {
//        if (boomTwoMotor.isRevLimitSwitchClosed == 0) {return 0}
//        else{return 1}
//    }
//
//    private fun getPosition(): Double {
//        return boomTwoMotor.selectedSensorPosition
//    }
//
//    fun disableSoftLimits(){
//        boomTwoMotor.configForwardSoftLimitEnable(false)
//        boomTwoMotor.configReverseSoftLimitEnable(false)
//    }
//    fun enableSoftLimits(){
//        boomTwoMotor.configForwardSoftLimitEnable(true)
//        boomTwoMotor.configReverseSoftLimitEnable(true)
//    }
//
//}