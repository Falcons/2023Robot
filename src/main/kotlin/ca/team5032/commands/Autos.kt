package ca.team5032.commands

import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.Commands
import ca.team5032.subsystems.Example

class Autos private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class!")
    }

    companion object {
        /** Example static factory for an autonomous command.  */
        fun exampleAuto(subsystem: Example): CommandBase {
            return Commands.sequence(subsystem.exampleMethodCommand(), ExampleCommand(subsystem))
        }
    }
}
