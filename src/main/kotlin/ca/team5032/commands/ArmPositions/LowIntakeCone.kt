package ca.team5032.commands.ArmPositions

import ca.team5032.commands.*
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup

class LowIntakeCone: ParallelCommandGroup(
    PivotCommand(-175000.0),
    ExtendBoomOneCommand(-1),
    ExtendBoomTwoCommand(-490000.0),
    WristCommand(-8000.0)
)

