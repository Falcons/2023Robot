package ca.team5032.commands.AutoCommands

import ca.team5032.commands.*
import ca.team5032.commands.ArmPositions.StowArmFast
import edu.wpi.first.wpilibj2.command.*

class ConeAndTaxi: SequentialCommandGroup (
//    InstantCommand({Romance.selectItem.changeState(SelectItem.State.Cone)}),
//    ClawIntakeCommand(),
//    ParallelRaceGroup(
////        ParallelCommandGroup(
////            PivotCommand(-4300.0),
////            ExtendBoomOneCommand(1),
////            ExtendBoomTwoCommand(-545000.0),
////            WristCommand(-22000.0)
////        ),
//        HighPostCone(),
//        WaitCommand(4.0)
//    )
    //DriveUntilNotBalanced(-1)
    //HighPostCube(),
//    EjectObject(),
//    StowArmSlow(),
//    ParallelRaceGroup(
//        WaitCommand(1.0),
//        InstantCommand({Romance.drive.changeState(DriveTrain.State.Autonomous)}),
//        InstantCommand({Romance.drive.autonomousInput.ySpeed = 0.3})
//    ),
//    InstantCommand({Romance.drive.changeState(DriveTrain.State.Driving)})
    ConeBrainDead(),
    StowArmFast(),
    DriveForTimeCommand(1.0)
    )