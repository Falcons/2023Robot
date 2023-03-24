package ca.team5032.commands
import ca.team5032.Romance
import ca.team5032.subsystems.Arm
import ca.team5032.subsystems.DriveTrain
import edu.wpi.first.wpilibj2.command.CommandBase
import java.lang.Math.abs

class DriveUntilNotBalanced(private val direction: Int) : CommandBase() {
    // direction 1 is forward
    // direction -1 is backwards

    private val tickThreshold = 0.4 / Romance.period
    private  var ticksAtSetPoint = 0.0

    /** Called when the command is initially scheduled.  */
    override fun initialize() {
        Romance.arm.mCancelCommand = false
        ticksAtSetPoint = 0.0
    }

    /** Called every time the scheduler runs while the command is scheduled.  */
    override fun execute() {
        // drive forwards
        if (direction == 1) {
            Romance.drive.m_drive.arcadeDrive(0.25,0.0)
        }
        // drive backwards
        else {
            Romance.drive.m_drive.arcadeDrive(-0.25,0.0)
        }
        if (Math.abs(Romance.drive.gyro.roll) >= 11.0) {
            ticksAtSetPoint ++
        } else {
            ticksAtSetPoint = 0.0
        }
    }

    /** Called once the command ends or is interrupted.  */
    override fun end(interrupted: Boolean) {
        Romance.drive.m_drive.arcadeDrive(0.0,0.0)
        Romance.drive.changeState(DriveTrain.State.Idle)
    }

    /** Returns true when the command should end.  */
    override fun isFinished(): Boolean {
        return ticksAtSetPoint >= tickThreshold
    }
}