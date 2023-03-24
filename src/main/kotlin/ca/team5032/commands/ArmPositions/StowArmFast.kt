package ca.team5032.commands.ArmPositions

import ca.team5032.commands.*
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup

class StowArmFast: ParallelCommandGroup(
    PivotCommand(-68000.0),
    ExtendBoomOneCommand(-1),
    ExtendBoomTwoCommand(0.0),
    WristCommand(0.0)
)



