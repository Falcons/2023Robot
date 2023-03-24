package ca.team5032.subsystems
import ca.team5032.utils.Subsystem
import ca.team5032.utils.Tabbed

class SelectItem : Subsystem<SelectItem.State>("ItemSelect", State.Cone), Tabbed {
    sealed class State {
        object Cone : State()
        object Cube : State()
    }
    init {
        tab.addString("Item state") { state.javaClass.simpleName }
    }

    override fun periodic() {
        // change colour of led

        state.let {
            when (it) {
                is State.Cone -> {}
                is State.Cube -> {}
            }
        }
    }
    fun cone() {changeState(State.Cone)}
    fun cube() {changeState(State.Cube)}
}