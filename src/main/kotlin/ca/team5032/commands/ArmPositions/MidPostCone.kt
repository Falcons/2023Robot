package ca.team5032.commands.ArmPositions

import ca.team5032.commands.*
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup

class MidPostCone: ParallelCommandGroup(
    PivotCommand(-25000.0),
    ExtendBoomOneCommand(-1),
    ExtendBoomTwoCommand(-450000.0),
    WristCommand(-32000.0)
)

