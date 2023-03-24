package ca.team5032.commands.ArmPositions

import ca.team5032.commands.*
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup

class MidPostCube: ParallelCommandGroup(
    PivotCommand(-45000.0),
    ExtendBoomOneCommand(-1),
    ExtendBoomTwoCommand(-400000.0),
    WristCommand(-32000.0)
)

