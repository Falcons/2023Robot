package ca.team5032.commands
import ca.team5032.Romance
import ca.team5032.subsystems.ClawC
import ca.team5032.subsystems.DriveTrain
import ca.team5032.subsystems.SelectItem
import edu.wpi.first.wpilibj2.command.CommandBase

class DriveForTimeCommand(val time: Double, val power: Double, val direction: Int) : CommandBase() {
    // item 1 for cone
    // item -1 for cube
    private var setPointTicks = 0.0
    private var TickThreshold = time / Romance.period

    /** Called when the command is initially scheduled.  */
    override fun initialize() {
        Romance.drive.applyBrakes()
        Romance.arm.mCancelCommand = false
        setPointTicks = 0.0
    }

    /** Called every time the scheduler runs while the command is scheduled.  */
    override fun execute() {
        Romance.drive.m_drive.curvatureDrive(power*direction,0.0,true)
        setPointTicks ++
    }

    /** Called once the command ends or is interrupted.  */
    override fun end(interrupted: Boolean) {
        Romance.drive.applyBrakes()
        Romance.drive.m_drive.curvatureDrive(0.0,0.0,true)
        Romance.drive.changeState(DriveTrain.State.Idle)
        Romance.arm.mCancelCommand = false
//        Romance.clawC.intakeMotor.set(0.0)
//        Romance.clawC.grabMotor.set(-0.1)
    }

    /** Returns true when the command should end.  */
    override fun isFinished(): Boolean {
        if (Romance.arm.mCancelCommand) {return true}
        if (setPointTicks >= TickThreshold) {
            return true
        }
        return false
    }
}