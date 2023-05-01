package ca.team5032.commands.ArmPositions

import ca.team5032.commands.*
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup

class LowIntakeCube: ParallelCommandGroup(
    PivotCommand(-230000.0),
    ExtendBoomOneCommand(-1),
    ExtendBoomTwoCommand(-320000.0),
    WristCommand(-19000.0)
)

