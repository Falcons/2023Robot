package ca.team5032.commands
import ca.team5032.Romance
import ca.team5032.subsystems.Arm
import edu.wpi.first.wpilibj2.command.CommandBase
import java.lang.Math.abs

class ExtendBoomOneCommand(private val direction: Int) : CommandBase() {
    // direction 1 is extend, (reverse motor)
    // direction -1 is retract, (forward motor)

    private var moveMotor = true
    /** Called when the command is initially scheduled.  */
    override fun initialize() {
        Romance.arm.mCancelCommand = false
        // check if already in extended position and trying to extend more
        if (Romance.arm.boomOneMotor.isRevLimitSwitchClosed == 1 && direction == 1){
            moveMotor = false
        } else if (Romance.arm.boomOneMotor.isFwdLimitSwitchClosed == 1 && direction == -1) {
            moveMotor = false
        }
    }

    /** Called every time the scheduler runs while the command is scheduled.  */
    override fun execute() {
        if (moveMotor) {
            if (direction == 1) {
                Romance.arm.boomOneMotor.set(-Arm.MotorPowerBoomOne.value)
            } else {
                Romance.arm.boomOneMotor.set(Arm.MotorPowerBoomOne.value)
            }
        }
    }

    /** Called once the command ends or is interrupted.  */
    override fun end(interrupted: Boolean) {
        Romance.arm.mCancelCommand = false
        Romance.arm.boomOneMotor.set(0.0)
        Romance.arm.resetBoomOneLimits()
    }

    /** Returns true when the command should end.  */
    override fun isFinished(): Boolean {
        if (Romance.arm.mCancelCommand) {return true}
        if (direction == 1) {
            //return (Romance.arm.boomOneMotor.isRevLimitSwitchClosed == 1 || Math.abs(Romance.arm.boomOneMotor.selectedSensorPosition - Arm.MaxEncoderExtensionBoomOne.value) <= 5000.0)
            return (Romance.arm.boomOneMotor.isRevLimitSwitchClosed == 1 && direction == 1 || Math.abs(Romance.arm.boomOneMotor.selectedSensorPosition - Arm.MaxEncoderExtensionBoomOne.value) <= 5000.0)
        } else if (direction == -1){
//            return (Romance.arm.boomOneMotor.isFwdLimitSwitchClosed == 1 || Math.abs(Romance.arm.boomOneMotor.selectedSensorPosition) <= 1000.0)
            return (Romance.arm.boomOneMotor.isFwdLimitSwitchClosed == 1 && direction == -1 || Math.abs(Romance.arm.boomOneMotor.selectedSensorPosition) <= 1000.0)
        }
        return false
    }
}