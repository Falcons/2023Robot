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

class BalanceCommand() : CommandBase() {

    private var ticksAtSetpoint = 0
    private var ticksAtCritical = 0
    private var MINIMUM_Y_SPEED = 0.2
    private var xSpeed = 0.0
    private var criticalAngle = 0.0

    // Must be at the setpoint for 0.6 seconds.
    private var tickThreshold = 0.2 / Romance.period

    //private val setpoint = if (targetPipeline == Limelight.Pipeline.ReflectiveTape) 0.0 else 1.0

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

        // check if angle is greater than critical
        if (abs(angleOffset) >= criticalAngle) {
            criticalAngle = abs(angleOffset)
        }

        // moment when robot stops climbing
        if (abs(angleOffset) < criticalAngle) {
            ticksAtCritical ++
        } else {
            ticksAtCritical = 0
        }

//        val output = drive.gyroController.calculate(angleOffset, 0.0)
//        val absolute = Math.abs(output)
//        val sign = -sign(output)
//
//        val remapped = absolute * (1 - MINIMUM_Y_SPEED) + MINIMUM_Y_SPEED
//
//        if (remapped > MINIMUM_Y_SPEED) {
//            xSpeed = MINIMUM_Y_SPEED
//        } else {
//            xSpeed = sign * remapped
//        }
//
//        if (drive.gyroController.atSetpoint()) {
//            ticksAtSetpoint++
//        } else {
//            ticksAtSetpoint = 0
//        }

        if (ticksAtCritical < tickThreshold) {
//            if (angleOffset <= -2.5) {
//                // drive until not balanced
////                drive.m_drive.curvatureDrive(-0.25, 0.0, true)
//                xSpeed = -0.25
//            } else if (angleOffset >= 2.5) {
////                drive.m_drive.curvatureDrive(0.25, 0.0, true)
//                xSpeed = 0.25
//            }

            xSpeed = -0.23

            // check for critical
//            if (16.0 <= abs(angleOffset)){
//                ticksAtCritical ++
//            } else {
//                ticksAtCritical = 0
//            }

        } else if (ticksAtCritical >= tickThreshold && ticksAtSetpoint < tickThreshold){
            // slow balance
            if (angleOffset <= -2.0) {
//                drive.m_drive.curvatureDrive(-0.17, 0.0, true)
                xSpeed = -0.10
            } else if (angleOffset >= 2.0) {
//                drive.m_drive.curvatureDrive(0.17, 0.0, true)
                xSpeed = 0.10
            }

            //xSpeed = -0.10

            // check for balance
//            if (1.8 >= abs(angleOffset) && ticksAtCritical >= tickThreshold){
//                ticksAtSetpoint ++
//            } else {
//                ticksAtSetpoint = 0
//            }

        } else {
//            drive.m_drive.curvatureDrive(0.0,0.0,true)
            xSpeed = 0.0
        }

        // check to see if it is at critical angle
//        if (15.4 <= abs(angleOffset)){
//            ticksAtCritical ++
//        } else if (ticksAtCritical <= tickThreshold) {
//            ticksAtCritical = 0
//        }

        if (1.2 >= abs(angleOffset) && ticksAtCritical >= tickThreshold){
            ticksAtSetpoint ++
        } else {
            ticksAtSetpoint = 0
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
        return ticksAtSetpoint >= tickThreshold
        if (Romance.arm.mCancelCommand) {return true}
    }
}