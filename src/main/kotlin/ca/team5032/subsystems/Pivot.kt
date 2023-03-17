package ca.team5032.subsystems
import ca.team5032.*
import ca.team5032.utils.DoubleProperty
import ca.team5032.utils.Subsystem
import ca.team5032.utils.Tabbed
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import edu.wpi.first.wpilibj.AnalogInput
import edu.wpi.first.wpilibj.XboxController
import kotlin.math.abs

class Pivot : Subsystem<Pivot.State>("Pivot", State.Idle), Tabbed {

    companion object {
        // The default power of the intake motor.
        val MaxPivotPositon = DoubleProperty("Max Pivot Position", 50000.0)

    }

    sealed class State {
        object Idle : State()
        object Moving : State()
        object Auto : State()
    }

    val pivotMotor = WPI_TalonFX(PIVOT_MOTOR)
    private val pivotSensor = AnalogInput(0)

    private val controller: XboxController = Romance.peripheralController
    private var power = 0.0

    init {
        pivotMotor.setNeutralMode(NeutralMode.Brake)
        pivotMotor.selectedSensorPosition = 0.0
        pivotMotor.configForwardSoftLimitThreshold(0.0)
        pivotMotor.configReverseSoftLimitThreshold(MaxPivotPositon.value)
        tab.addString("pivot state") { state.javaClass.simpleName }
        tab.addNumber("Power", ::getPower)
        tab.addNumber("Current", ::getCurrent)
        tab.addNumber("Pivot Pos", ::getEncoderPos)
        tab.addNumber("Pivot Pot", ::getPotInput)
    }

    data class rotationalInput(var power: Double)
    val commandRotationalInput = rotationalInput(0.0)

    private fun motorOutputs(): rotationalInput {
        return when (state) {
            is State.Idle -> rotationalInput(0.0)
            is State.Moving -> rotationalInput(controller.leftY*0.5)
            is State.Auto -> commandRotationalInput
        }
    }

    private val hasInput: Boolean
        get() = abs(controller.leftY) > 0.15

    override fun periodic() {
        state.let {
            if (state !is State.Auto){
                if (hasInput) {
                    changeState(State.Moving)
                } else if (state !is State.Idle) {
                    changeState(State.Idle)
                }
            }
        }

        var (pivotInput) = motorOutputs()
        power = pivotInput
        pivotMotor.set(power)
    }

    private fun getPower(): Double {
        return power
    }
    private fun getCurrent(): Double {
        return pivotMotor.statorCurrent
    }
    private fun getEncoderPos(): Double {
        return pivotMotor.selectedSensorPosition
    }
    private fun getPotInput(): Double {
        return pivotSensor.averageVoltage
    }

}