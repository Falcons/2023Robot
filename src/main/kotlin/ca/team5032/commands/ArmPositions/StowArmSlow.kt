package ca.team5032.commands.ArmPositions

import ca.team5032.commands.*
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup

//class StowArm: ParallelCommandGroup(
//    PivotCommand(-68000.0),
//    ExtendBoomOneCommand(-1),
//    ExtendBoomTwoCommand(0.0),
//    WristCommand(0.0)
//)

class StowArmSlow: SequentialCommandGroup(
    ParallelCommandGroup(
        ExtendBoomOneCommand(-1),
        ExtendBoomTwoCommand(0.0),
        WristCommand(0.0)
    ),
    PivotCommand(-68000.0)
)

