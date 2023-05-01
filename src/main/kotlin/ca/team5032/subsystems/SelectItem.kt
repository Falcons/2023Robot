package ca.team5032.subsystems
import ca.team5032.*
import ca.team5032.utils.Subsystem
import ca.team5032.utils.Tabbed
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.DigitalOutput

class SelectItem : Subsystem<SelectItem.State>("ItemSelect", State.Cone), Tabbed {
    sealed class State {
        object Cone : State()
        object Cube : State()
    }

    private val SOLID_PURPLE = DigitalOutput(PURPLE_ID)
    private val SOLID_ORANGE = DigitalOutput(ORANGE_ID)
    private val BLINK_PURPLE = DigitalOutput(PURPLE_BLINK_ID)
    private val BLINK_ORANGE = DigitalOutput(ORANGE_BLINK_ID)

    init {
        tab.addString("Item state") { state.javaClass.simpleName }
    }

    override fun periodic() {
        // change colour of led

        // DIO HIGH 1 IS CONE

        state.let {
            when (it) {
                is State.Cone -> {
                    if (Romance.clawC.state is ClawC.State.Intaking){
                        BLINK_ORANGE.set(true)
                        BLINK_PURPLE.set(false)
                        SOLID_ORANGE.set(false)
                        SOLID_PURPLE.set(false)
                    } else{
                        BLINK_ORANGE.set(false)
                        BLINK_PURPLE.set(false)
                        SOLID_ORANGE.set(true)
                        SOLID_PURPLE.set(false)
                    }
                }
                is State.Cube -> {
                    if (Romance.clawC.state is ClawC.State.Intaking){
                        BLINK_ORANGE.set(false)
                        BLINK_PURPLE.set(true)
                        SOLID_ORANGE.set(false)
                        SOLID_PURPLE.set(false)
                    } else{
                        BLINK_ORANGE.set(false)
                        BLINK_PURPLE.set(false)
                        SOLID_ORANGE.set(false)
                        SOLID_PURPLE.set(true)
                    }
                }
            }
        }
    }
    fun cone() {changeState(State.Cone)}
    fun cube() {changeState(State.Cube)}
}