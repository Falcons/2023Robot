package ca.team5032.commands
import ca.team5032.Romance
import ca.team5032.subsystems.Arm
import edu.wpi.first.wpilibj2.command.CommandBase
import java.lang.Math.abs

class ExtendBoomTwoCommand(private val position: Double) : CommandBase() {
    // direction 1 is extend, (reverse motor)
    // direction -1 is retract, (forward motor)
    private var direction = 1

    /** Called when the command is initially scheduled.  */
    override fun initialize() {
        Romance.arm.mCancelCommand = false
        Romance.arm.boomTwoMotor.configForwardSoftLimitEnable(true)
        Romance.arm.boomTwoMotor.configReverseSoftLimitEnable(true)
        // determine direction
        // if the motor position is "more" than the target, need to extend
        // extend
        if (Romance.arm.boomTwoMotor.selectedSensorPosition > position) {
            Romance.arm.boomTwoMotor.configForwardSoftLimitThreshold(0.0)
            Romance.arm.boomTwoMotor.configReverseSoftLimitThreshold(position)
            direction = 1
        } else{
            //retract
            Romance.arm.boomTwoMotor.configReverseSoftLimitThreshold(Arm.MaxEncoderExtensionBoomTwo.value)
            Romance.arm.boomTwoMotor.configForwardSoftLimitThreshold(position)
            direction = -1
        }
    }

    /** Called every time the scheduler runs while the command is scheduled.  */
    override fun execute() {
        if (direction == 1) {
            Romance.arm.boomTwoMotor.set(-Arm.MotorPowerBoomTwo.value)
        } else {
            Romance.arm.boomTwoMotor.set(Arm.MotorPowerBoomTwo.value)
        }
    }

    /** Called once the command ends or is interrupted.  */
    override fun end(interrupted: Boolean) {
        Romance.arm.mCancelCommand = false
        Romance.arm.boomTwoMotor.set(0.0)
        Romance.arm.resetBoomTwoLimits()
    }

    /** Returns true when the command should end.  */
    override fun isFinished(): Boolean {
        if (Romance.arm.mCancelCommand) {return true}
        if (Romance.arm.boomTwoMotor.isFwdLimitSwitchClosed == 1 && direction == -1) {return true}
        if (Romance.arm.boomTwoMotor.isRevLimitSwitchClosed == 1 && direction == 1) {return true}
        return abs(Romance.arm.boomTwoMotor.selectedSensorPosition - position) <= 800.0
    }
}