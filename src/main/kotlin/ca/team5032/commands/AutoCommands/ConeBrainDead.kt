package ca.team5032.commands.AutoCommands

import ca.team5032.Romance
import ca.team5032.commands.*
import ca.team5032.commands.ArmPositions.HighPostCone
import ca.team5032.commands.ClawPositions.EjectObject
import ca.team5032.subsystems.SelectItem
import edu.wpi.first.wpilibj2.command.*

class ConeBrainDead: SequentialCommandGroup (
    InstantCommand({Romance.selectItem.changeState(SelectItem.State.Cone)}),
    ClawIntakeCommand(),
    ParallelRaceGroup(
//        ParallelCommandGroup(
//            PivotCommand(-4300.0),
//            ExtendBoomOneCommand(1),
//            ExtendBoomTwoCommand(-545000.0),
//            WristCommand(-22000.0)
//        ),
        HighPostCone(),
        WaitCommand(4.0)
    ),
    //DriveUntilNotBalanced(-1)
    //HighPostCube(),
    EjectObject(),
//    StowArmSlow()
    HomeCommand()
    //DriveForTimeCommand()
//    ParallelRaceGroup(
//        WaitCommand(1.0),
//        InstantCommand({Romance.drive.changeState(DriveTrain.State.Autonomous)}),
//        InstantCommand({Romance.drive.autonomousInput.ySpeed = 0.3})
//    ),
//    InstantCommand({Romance.drive.autonomousInput.ySpeed = 0.0}),
//    InstantCommand({Romance.drive.changeState(DriveTrain.State.Driving)})
    )