package ca.team5032.commands

import ca.team5032.Romance
import ca.team5032.Romance.drive
import ca.team5032.subsystems.DriveTrain
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.abs

class BalanceCommand(var direction: Int) : CommandBase() {

    private var ticksAtSetpoint = 0
    private var ticksAtCritical = 0.0
    private var MINIMUM_Y_SPEED = 0.2
    private var xSpeed = 0.0
    //private var criticalAngle = 16.0
    private var criticalAngle = 18.0
    private var tippingAngle = 0.0
    private var slowStageEnables = false

    private val time = 0.6
    private var tickThreshold = time / Romance.period

    private var previous = 0.0
    private var current = 0.0

    override fun initialize() {
        Romance.arm.mCancelCommand = false
        drive.changeState(DriveTrain.State.Autonomous)
        drive.engageBrake()
        if (direction == -1){
            criticalAngle = drive.gyro.roll+16.4

        } else {
            criticalAngle = drive.gyro.roll+10.5
        }
        tippingAngle = criticalAngle-0.7
        drive.autonomousInput.ySpeed = 0.0
        ticksAtSetpoint = 0
        current = drive.gyro.roll
    }

    override fun execute() {
        // Turn towards target.
        // get error
        val angleOffset = drive.gyro.roll
        previous = current
        current = angleOffset
        //drive.applyBrakes()

        // keep updating tipping angle
//        if (abs(angleOffset)>tippingAngle) {
//            tippingAngle = abs(angleOffset)
//        }

        // keep driving until it has reached the critical angle
        if (abs(angleOffset) < criticalAngle) {
            xSpeed = 0.26*direction
        }

        if (abs(angleOffset) > criticalAngle && abs(previous-current) <=0.7) {
            ticksAtCritical++
        }

        // once the angle offset hits the critical angle threshold start driving slow until it tips
//        if (abs(angleOffset) in criticalAngle..tippingAngle) {
//            ticksAtCritical++
//        }

        if (ticksAtCritical >= tickThreshold) {
            slowStageEnables = true
            xSpeed = if (direction == -1){
                -0.15
            } else{
                0.13
            }
            //xSpeed = 0.18*direction
            if (angleOffset in -tippingAngle..tippingAngle) {
                xSpeed = 0.0
                ticksAtSetpoint++
            }
        }
        drive.autonomousInput.ySpeed = xSpeed
//        drive.m_drive.curvatureDrive(xSpeed, 0.0, true)
    }

    override fun end(interrupted: Boolean) {
        xSpeed = 0.0
        drive.autonomousInput.ySpeed = 0.0
        Romance.arm.mCancelCommand = false
        drive.engageBrake()
    }

    override fun isFinished(): Boolean {
        if (Romance.arm.mCancelCommand) {return true}
        return ticksAtSetpoint >= tickThreshold
    }
}