package ca.team5032.commands.ClawPositions

import ca.team5032.Romance
import ca.team5032.commands.*
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import edu.wpi.first.wpilibj2.command.WaitCommand
import edu.wpi.first.wpilibj2.command.WaitUntilCommand

class EjectObject: ParallelCommandGroup (
    CompressCommand(0.0),
    InstantCommand({Romance.clawC.outTake()}),
    SequentialCommandGroup(
        InstantCommand({Romance.clawC.outTake()}),
        WaitCommand(0.5),
        InstantCommand({Romance.arm.cancelCommand()})
    )
)