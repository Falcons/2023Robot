package ca.team5032.commands
import ca.team5032.Romance
import ca.team5032.subsystems.ClawC
import ca.team5032.subsystems.SelectItem
import edu.wpi.first.wpilibj2.command.CommandBase

class ClawIntakeCommand() : CommandBase() {
    // item 1 for cone
    // item -1 for cube
    private var setPointTicks = 0.0
    private var TickThreshold = 0.8 / Romance.period

    /** Called when the command is initially scheduled.  */
    override fun initialize() {
        Romance.arm.mCancelCommand = false
        Romance.clawC.hasObject = false
        //Romance.clawC.changeState(ClawC.State.Intaking)
        setPointTicks = 0.0
    }

    /** Called every time the scheduler runs while the command is scheduled.  */
    override fun execute() {
        // cone needs no intake compression
        if (Romance.selectItem.state is SelectItem.State.Cone) {
            Romance.clawC.intakeMotor.set(-ClawC.IntakeConePower.value)
        } else {
            Romance.clawC.intakeMotor.set(-ClawC.IntakeCubePower.value)
        }
        // keep intaking for a little bit
        if (Romance.clawC.intakeMotor.statorCurrent >= 28.0) {
            setPointTicks ++
        } else {
            setPointTicks = 0.0
        }
    }

    /** Called once the command ends or is interrupted.  */
    override fun end(interrupted: Boolean) {
        Romance.arm.mCancelCommand = false
        Romance.clawC.intakeMotor.set(0.0)
        Romance.clawC.grabMotor.set(-0.1)
        Romance.clawC.changeState(ClawC.State.Idle)
    }

    /** Returns true when the command should end.  */
    override fun isFinished(): Boolean {
        if (Romance.arm.mCancelCommand) {return true}
        if (setPointTicks >= TickThreshold) {
            Romance.clawC.hasObject = true
            return true
        }
        return false
    }
}