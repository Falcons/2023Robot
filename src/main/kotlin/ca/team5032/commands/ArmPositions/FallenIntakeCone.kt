package ca.team5032.commands.ArmPositions

import ca.team5032.commands.*
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup

class FallenIntakeCone: ParallelCommandGroup(
    PivotCommand(-110000.0),
    ExtendBoomOneCommand(-1),
    ExtendBoomTwoCommand(-295000.0),
    WristCommand(-65000.0)
)

