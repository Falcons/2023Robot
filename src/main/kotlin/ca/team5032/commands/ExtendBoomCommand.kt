package ca.team5032.commands
import edu.wpi.first.wpilibj2.command.CommandBase

class ExtendBoomCommand(private val angle: Int) : CommandBase() {
    init {
        // Use addRequirements() here to declare subsystem dependencies.

    }

    /** Called when the command is initially scheduled.  */
    override fun initialize() {}

    /** Called every time the scheduler runs while the command is scheduled.  */
    override fun execute() {}

    /** Called once the command ends or is interrupted.  */
    override fun end(interrupted: Boolean) {}

    /** Returns true when the command should end.  */
    override fun isFinished(): Boolean {
        return false
    }
}