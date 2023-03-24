package ca.team5032.commands.ArmPositions

import ca.team5032.commands.*
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup

class HighPostCone: ParallelCommandGroup(
    PivotCommand(-3600.0),
    ExtendBoomOneCommand(1),
    ExtendBoomTwoCommand(-545000.0),
    WristCommand(-20000.0)
)

