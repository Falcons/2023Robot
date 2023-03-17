//package ca.team5032.subsystems
//import ca.team5032.*
//import ca.team5032.utils.DoubleProperty
//import ca.team5032.utils.Subsystem
//import ca.team5032.utils.Tabbed
//import com.ctre.phoenix.motorcontrol.LimitSwitchNormal
//import com.ctre.phoenix.motorcontrol.LimitSwitchSource
//import com.ctre.phoenix.motorcontrol.NeutralMode
//import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
//import edu.wpi.first.wpilibj.XboxController
//import kotlin.math.abs
//
//class BoomOneC : Subsystem<BoomOneC.State>("Boom One", State.Idle), Tabbed {
//
//    companion object {
//        // The default power of the intake motor.
//        val MaxEncoderExtension = DoubleProperty("Boom One Max Extension", 10000.0)
//        val MotorPower = DoubleProperty("Boom One Power", 1.0)
//    }
//
//    sealed class State {
//        object Idle : State()
//        object Moving : State()
//    }
//
//    val boomOneMotor = WPI_TalonFX(BOOM_ONE_ID)
//
//    private val controller: XboxController = Romance.driveController
//    private var power = 0.0
//
//    init {
//        boomOneMotor.setNeutralMode(NeutralMode.Brake)
//        boomOneMotor.selectedSensorPosition = 0.0
//        boomOneMotor.configForwardSoftLimitThreshold(0.0)
//        boomOneMotor.configReverseSoftLimitThreshold(MaxEncoderExtension.value)
//        //boomOneMotor.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector,LimitSwitchNormal.NormallyOpen)
//        tab.addString("Boom one state") { state.javaClass.simpleName }
//        tab.addNumber("Power", ::getPower)
//        tab.addNumber("Current", ::getCurrent)
//        tab.addInteger("Forward", ::hasForwardLimitSwitch)
//        tab.addInteger("Reverse",::hasRevLimitSwitch)
//        tab.addNumber("Encoder Pos", ::getPosition)
//    }
//
//    data class rotationalInput(var power: Double)
//
//    private fun motorOutputs(): rotationalInput {
//        return when (state) {
//            is State.Idle -> rotationalInput(0.0)
//            is State.Moving -> rotationalInput(controller.leftY*1.0)
//        }
//    }
//
//    private val hasInput: Boolean
//        get() = abs(controller.leftY) > 0.07
//
//    override fun periodic() {
//        state.let {
//            if (hasInput){
//                changeState(State.Moving)
//            } else if (state !is State.Idle) {
//                changeState(State.Idle)
//            }
//        }
//        if (boomOneMotor.isFwdLimitSwitchClosed == 1) {boomOneMotor.selectedSensorPosition = 0.0}
//        //if (boomOneMotor.isRevLimitSwitchClosed == 1) {boomOneMotor.selectedSensorPosition = MaxEncoderExtension.value}
//        var (boomOneInput) = motorOutputs()
//        power = boomOneInput
//        boomOneMotor.set(power)
//    }
//
//    private fun getPower(): Double {
//        return power
//    }
//    private fun getCurrent(): Double {
//        return boomOneMotor.statorCurrent
//    }
//
//    private fun getPosition(): Double {
//        return boomOneMotor.selectedSensorPosition
//    }
//
//    private fun hasForwardLimitSwitch(): Long {
//        if (boomOneMotor.isFwdLimitSwitchClosed == 0) {return 0}
//        else{return 1}
//    }
//
//    private fun hasRevLimitSwitch(): Long {
//        if (boomOneMotor.isRevLimitSwitchClosed == 0) {return 0}
//        else{return 1}
//    }
//
//
//}