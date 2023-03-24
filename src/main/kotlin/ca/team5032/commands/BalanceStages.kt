package ca.team5032.commands

import ca.team5032.Romance
import ca.team5032.Romance.drive
import ca.team5032.Romance.limelight
import ca.team5032.subsystems.DriveTrain
import ca.team5032.subsystems.Limelight
import com.ctre.phoenix.motorcontrol.NeutralMode
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.abs
import kotlin.math.sign

class BalanceStages(val direction: Int) : CommandBase() {

    private var ticksAtSetpoint = 0
    private var ticksAtCritical = 0.0
    private var MINIMUM_Y_SPEED = 0.2
    private var xSpeed = 0.0
    private var criticalAngle = 16.0
    private var tippingAngle = 0.0
    private var slowStageEnables = false

    private var tickThreshold = 0.6 / Romance.period


    override fun initialize() {
        Romance.arm.mCancelCommand = false
        drive.applyBrakes()
        ticksAtSetpoint = 0
    }

    override fun execute() {
        // Turn towards target.
        // get error
        val angleOffset = drive.gyro.roll
        drive.applyBrakes()

        // keep updating tipping angle
        if (abs(angleOffset)>tippingAngle) {
            tippingAngle = abs(angleOffset)
        }

        // keep driving until it has reached the critical angle
        if (abs(angleOffset) < criticalAngle) {
            xSpeed = 0.23*direction
        }

        // once the angle offset hits the critical angle threshold start driving slow until it tips
        if (abs(angleOffset) in criticalAngle..tippingAngle) {
            ticksAtCritical++
        }

        if (ticksAtCritical >= tickThreshold) {
            slowStageEnables = true
            xSpeed = 0.14*direction
        }

        if (abs(angleOffset) < tippingAngle && slowStageEnables) {
            ticksAtSetpoint++
            xSpeed = 0.08*direction
        }

        drive.m_drive.curvatureDrive(xSpeed, 0.0, true)
    }

    override fun end(interrupted: Boolean) {
       xSpeed = 0.0
        Romance.arm.mCancelCommand = false
       drive.m_drive.curvatureDrive(0.0,0.0,true)
        drive.changeState(DriveTrain.State.Idle)
        drive.applyBrakes()
    }

    override fun isFinished(): Boolean {
        if (Romance.arm.mCancelCommand) {return true}
        return ticksAtSetpoint >= tickThreshold
    }
}