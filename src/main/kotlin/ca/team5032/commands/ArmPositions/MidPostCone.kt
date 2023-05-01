package ca.team5032.commands.ArmPositions

import ca.team5032.commands.*
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup

class MidPostCone: ParallelCommandGroup(
    PivotCommand(-24726.0),
    ExtendBoomOneCommand(-1),
    ExtendBoomTwoCommand(-458700.0),
    WristCommand(-40000.0)
)

