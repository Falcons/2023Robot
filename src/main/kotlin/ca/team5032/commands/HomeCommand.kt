package ca.team5032.commands
import ca.team5032.Romance
import ca.team5032.subsystems.Arm
import edu.wpi.first.wpilibj2.command.CommandBase
import java.lang.Math.abs

class HomeCommand() : CommandBase() {
    // direction 1 is extend, (reverse motor)
    // direction -1 is retract, (forward motor)

    /** Called when the command is initially scheduled.  */
    override fun initialize() {
        Romance.arm.mCancelCommand = false
        Romance.arm.boomTwoMotor.configForwardSoftLimitEnable(false)
        Romance.arm.boomTwoMotor.configReverseSoftLimitEnable(false)

        Romance.arm.boomOneMotor.configForwardSoftLimitEnable(false)
        Romance.arm.boomOneMotor.configReverseSoftLimitEnable(false)

        Romance.arm.pivotMotor.configForwardSoftLimitEnable(false)
        Romance.arm.pivotMotor.configReverseSoftLimitEnable(false)

        Romance.arm.wristMotor.configForwardSoftLimitEnable(false)
        Romance.arm.wristMotor.configReverseSoftLimitEnable(false)

        Romance.clawC.grabMotor.configForwardSoftLimitEnable(false)
        Romance.clawC.grabMotor.configReverseSoftLimitEnable(false)
    }

    /** Called every time the scheduler runs while the command is scheduled.  */
    override fun execute() {
        Romance.arm.boomOneMotor.set(0.9)
        Romance.arm.boomTwoMotor.set(0.9)
        Romance.arm.pivotMotor.set(0.45)
        Romance.arm.wristMotor.set(0.2)
        Romance.clawC.grabMotor.set(0.3)
    }

    /** Called once the command ends or is interrupted.  */
    override fun end(interrupted: Boolean) {
        Romance.arm.mCancelCommand = false
        Romance.arm.boomOneMotor.set(0.0)
        Romance.arm.boomTwoMotor.set(0.0)
        Romance.arm.pivotMotor.set(0.0)
        Romance.arm.wristMotor.set(0.0)
        Romance.clawC.grabMotor.set(0.0)

        Romance.arm.resetBoomTwoLimits()
        Romance.arm.resetBoomOneLimits()
        Romance.arm.resetPivotLimits()
        Romance.arm.resetWristLimits()
        Romance.clawC.resetGrabLimits()
    }

    /** Returns true when the command should end.  */
    override fun isFinished(): Boolean {
        if (Romance.arm.mCancelCommand) {return true}
        if (Romance.arm.boomOneMotor.isFwdLimitSwitchClosed == 1
            && Romance.arm.boomTwoMotor.isFwdLimitSwitchClosed == 1
            && Romance.arm.wristMotor.isFwdLimitSwitchClosed == 1
            && Romance.arm.pivotMotor.isFwdLimitSwitchClosed == 1
            && Romance.clawC.grabMotor.isFwdLimitSwitchClosed == 1
        ) {return true}
        return false
    }
}