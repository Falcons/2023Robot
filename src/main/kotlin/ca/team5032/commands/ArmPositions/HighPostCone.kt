package ca.team5032.commands.ArmPositions

import ca.team5032.commands.*
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup

class HighPostCone: ParallelCommandGroup(
    PivotCommand(-7000.0),
    ExtendBoomOneCommand(1),
    ExtendBoomTwoCommand(-444800.0),
    WristCommand(-30000.0)
)

