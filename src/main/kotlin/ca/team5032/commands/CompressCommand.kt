package ca.team5032.commands
import ca.team5032.Romance
import ca.team5032.subsystems.Arm
import ca.team5032.subsystems.ClawC
import edu.wpi.first.wpilibj2.command.CommandBase
import java.lang.Math.abs

class CompressCommand(private val position: Double) : CommandBase() {
    // direction 1 is release (forward motor)
    // direction -1 is compress (reverse motor)
    private var direction = -1

    /** Called when the command is initially scheduled.  */
    override fun initialize() {
        Romance.arm.mCancelCommand = false
        Romance.clawC.grabMotor.configForwardSoftLimitEnable(true)
        Romance.clawC.grabMotor.configReverseSoftLimitEnable(true)
        // determine direction
        // release
        if (Romance.clawC.grabMotor.selectedSensorPosition < position) {
            Romance.clawC.grabMotor.configForwardSoftLimitThreshold(position)
            Romance.clawC.grabMotor.configReverseSoftLimitThreshold(ClawC.MaxCompressEncoderValue.value)
            direction = 1
        } else {
            //retract
            Romance.clawC.grabMotor.configReverseSoftLimitThreshold(position)
            Romance.clawC.grabMotor.configForwardSoftLimitThreshold(0.0)
            direction = -1
        }
    }

    /** Called every time the scheduler runs while the command is scheduled.  */
    override fun execute() {
        if (direction == 1) {
            Romance.clawC.grabMotor.set(ClawC.GrabPower.value)
        } else {
            Romance.clawC.grabMotor.set(-ClawC.GrabPower.value)
        }
    }

    /** Called once the command ends or is interrupted.  */
    override fun end(interrupted: Boolean) {
        Romance.arm.mCancelCommand = false
        Romance.clawC.resetGrabLimits()
        Romance.clawC.grabMotor.set(0.0)
    }
    /** Returns true when the command should end.  */
    override fun isFinished(): Boolean {
        if (Romance.arm.mCancelCommand) {return true}
        if (Romance.clawC.grabMotor.isFwdLimitSwitchClosed == 1 && direction == 1) {return true}
        return abs(Romance.clawC.grabMotor.selectedSensorPosition - position) <= 1000.0
    }
}