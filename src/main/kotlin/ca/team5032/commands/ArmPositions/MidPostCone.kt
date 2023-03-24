package ca.team5032.commands.ArmPositions

import ca.team5032.commands.*
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup

class MidPostCone: ParallelCommandGroup(
    PivotCommand(-10000.0),
    ExtendBoomOneCommand(-1),
    ExtendBoomTwoCommand(-530000.0),
    WristCommand(-34000.0)
)

