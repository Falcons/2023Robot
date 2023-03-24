package ca.team5032.commands.ClawPositions

import ca.team5032.commands.*
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup

class IntakeCube: SequentialCommandGroup (
    CompressCommand(-3200.0),
    ClawIntakeCommand()
)