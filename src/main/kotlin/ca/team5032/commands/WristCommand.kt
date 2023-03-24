package ca.team5032.commands
import ca.team5032.Romance
import ca.team5032.subsystems.Arm
import edu.wpi.first.wpilibj2.command.CommandBase
import java.lang.Math.abs

class WristCommand(private val position: Double) : CommandBase() {
    // direction 1 is rotate up, (forward motor)
    // direction -1 is rotate down, (reverse motor)
    // range from 0 - -300,000? up to down
    private var direction = 1

    /** Called when the command is initially scheduled.  */
    override fun initialize() {
        Romance.arm.mCancelCommand = false
        Romance.arm.wristMotor.configForwardSoftLimitEnable(true)
        Romance.arm.wristMotor.configReverseSoftLimitEnable(true)
        // determine direction
        // pivot up
        if (Romance.arm.wristMotor.selectedSensorPosition < position) {
            Romance.arm.wristMotor.configForwardSoftLimitThreshold(position)
            Romance.arm.wristMotor.configReverseSoftLimitThreshold(Arm.MaxEncoderExtensionWrist.value)
            direction = 1
        } else {
            //retract
            Romance.arm.wristMotor.configReverseSoftLimitThreshold(position)
            Romance.arm.wristMotor.configForwardSoftLimitThreshold(0.0)
            direction = -1
        }
    }

    /** Called every time the scheduler runs while the command is scheduled.  */
    override fun execute() {
        if (direction == 1) {
            Romance.arm.wristMotor.set(Arm.WristPower.value)
        } else {
            Romance.arm.wristMotor.set(-Arm.WristPower.value)
        }
    }

    /** Called once the command ends or is interrupted.  */
    override fun end(interrupted: Boolean) {
        Romance.arm.mCancelCommand = false
        Romance.arm.resetWristLimits()
        if (Romance.arm.wristMotor.isFwdLimitSwitchClosed == 1){Romance.arm.wristMotor.set(0.0)}
        else{Romance.arm.wristMotor.set(0.06)}
        // holding power
//        if (Romance.arm.state is Arm.State.HighPost || Romance.arm.state is Arm.State.HighIntake) {Romance.arm.pivotMotor.set(0.07)}
//        else if (Romance.arm.state is Arm.State.MidPost) {Romance.arm.pivotMotor.set(0.05)}
//        else {Romance.arm.pivotMotor.set(0.0)}
//        when (Romance.arm.state) {
//            is Arm.State.MidPost -> Romance.arm.wristMotor.set(0.05)
//            is Arm.State.HighPost -> Romance.arm.wristMotor.set(0.06)
//            is Arm.State.HighIntake -> Romance.arm.wristMotor.set(0.06)
//            is Arm.State.LowIntake -> Romance.arm.wristMotor.set(0.06)
//            is Arm.State.Stow -> Romance.arm.wristMotor.set(0.0)
//            is Arm.State.Idle -> Romance.arm.wristMotor.set(0.0)
//        }
    }

    /** Returns true when the command should end.  */
    override fun isFinished(): Boolean {
        if (Romance.arm.mCancelCommand) {return true}
        return abs(Romance.arm.wristMotor.selectedSensorPosition - position) <= 1000.0
    }
}