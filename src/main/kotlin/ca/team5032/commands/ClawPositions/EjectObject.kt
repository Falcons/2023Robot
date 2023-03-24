package ca.team5032.commands.ClawPositions

import ca.team5032.commands.*
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup

class EjectObject: ParallelCommandGroup (
    CompressCommand(0.0)
)