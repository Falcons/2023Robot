package ca.team5032.commands.ArmPositions

import ca.team5032.commands.*
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup

class FallenIntakeCone: ParallelCommandGroup(
    PivotCommand(-101000.0),
    ExtendBoomOneCommand(-1),
    ExtendBoomTwoCommand(-305000.0),
    WristCommand(-59000.0)
)

