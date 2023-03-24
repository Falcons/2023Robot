package ca.team5032.commands.ClawPositions

import ca.team5032.commands.*
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup

class IntakeCone: SequentialCommandGroup (
    CompressCommand(-10000.0),
    ClawIntakeCommand()
)