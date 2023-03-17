//package ca.team5032.subsystems
//import ca.team5032.*
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
//class Wrist : Subsystem<Wrist.State>("Wrist", State.Idle), Tabbed {
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
//        object Idle : State()
//        object Moving : State()
//    }
//
//    val wristMotor = WPI_TalonFX(WRIST_MOTOR_ID)
//
//    private val controller: XboxController = Romance.peripheralController
//    private var power = 0.0
//
//    init {
//        wristMotor.setNeutralMode(NeutralMode.Brake)
//        tab.addString("claw state") { state.javaClass.simpleName }
//        tab.addNumber("Power", ::getPower)
//        tab.addNumber("Current", ::getCurrent)
//        tab.addNumber("Position",::getPosition)
//    }
//
//    data class rotationalInput(var power: Double)
//
//    private fun motorOutputs(): rotationalInput {
//        return when (state) {
//            is State.Idle -> rotationalInput(0.0)
//            is State.Moving -> rotationalInput(controller.rightY*0.5)
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
//
//        var (wristInput) = motorOutputs()
//        power = wristInput
//        wristMotor.set(power)
//    }
//
//    private fun getPower(): Double {
//        return power
//    }
//    private fun getCurrent(): Double {
//        return wristMotor.statorCurrent
//    }
//
//    private fun getPosition(): Double {
//        return wristMotor.selectedSensorPosition
//    }
//
//}