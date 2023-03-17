package ca.team5032.commands
import ca.team5032.Romance
import ca.team5032.subsystems.Arm
import edu.wpi.first.wpilibj2.command.CommandBase
import java.lang.Math.abs

class PivotCommand(private val position: Double) : CommandBase() {
    // direction 1 is rotate up, (forward motor)
    // direction -1 is rotate down, (reverse motor)
    // range from 0 - -300,000? up to down
    private var direction = 1

    /** Called when the command is initially scheduled.  */
    override fun initialize() {
        Romance.arm.mCancelCommand = false
        Romance.arm.pivotMotor.configForwardSoftLimitEnable(true)
        Romance.arm.pivotMotor.configReverseSoftLimitEnable(true)
        // determine direction
        // pivot up
        if (Romance.arm.pivotMotor.selectedSensorPosition < position) {
            Romance.arm.pivotMotor.configForwardSoftLimitThreshold(position)
            Romance.arm.pivotMotor.configReverseSoftLimitThreshold(Arm.MaxEncoderExtensionPivot.value)
            direction = 1
        } else {
            //retract
            Romance.arm.pivotMotor.configReverseSoftLimitThreshold(position)
            Romance.arm.pivotMotor.configForwardSoftLimitThreshold(0.0)
            direction = -1
        }
    }

    /** Called every time the scheduler runs while the command is scheduled.  */
    override fun execute() {
        if (direction == 1) {
            Romance.arm.pivotMotor.set(Arm.PivotPower.value)
        } else {
            Romance.arm.pivotMotor.set(-Arm.PivotPower.value)
        }
    }

    /** Called once the command ends or is interrupted.  */
    override fun end(interrupted: Boolean) {
        Romance.arm.resetPivotLimits()
        Romance.arm.pivotMotor.set(0.0)
        // holding power
//        if (Romance.arm.state is Arm.State.HighPost || Romance.arm.state is Arm.State.HighIntake) {Romance.arm.pivotMotor.set(0.07)}
//        else if (Romance.arm.state is Arm.State.MidPost) {Romance.arm.pivotMotor.set(0.05)}
//        else {Romance.arm.pivotMotor.set(0.0)}

//        when (Romance.arm.state) {
//            is Arm.State.MidPost -> Romance.arm.pivotMotor.set(0.3)
//            is Arm.State.HighPost -> Romance.arm.pivotMotor.set(0.3)
//            is Arm.State.HighIntake -> Romance.arm.pivotMotor.set(0.3)
//            is Arm.State.LowIntake -> Romance.arm.pivotMotor.set(0.0)
//            is Arm.State.Stow -> Romance.arm.pivotMotor.set(0.0)
//            is Arm.State.Idle -> Romance.arm.pivotMotor.set(0.0)
//        }
    }

    /** Returns true when the command should end.  */
    override fun isFinished(): Boolean {
        if (Romance.arm.mCancelCommand) {return true}
        if (Romance.arm.pivotMotor.isRevLimitSwitchClosed == 1 && direction == -1) {return true}
        if (Romance.arm.pivotMotor.isFwdLimitSwitchClosed == 1 && direction == 1) {return true}
        return abs(Romance.arm.pivotMotor.selectedSensorPosition - position) <= 3000.0
    }
}