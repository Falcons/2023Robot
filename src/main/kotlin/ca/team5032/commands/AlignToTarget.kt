package ca.team5032.commands

import ca.team5032.Romance
import ca.team5032.Romance.limelight
import ca.team5032.subsystems.DriveTrain
import ca.team5032.subsystems.Limelight
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.sign

class AlignToTargetCommand(
    private val targetPipeline: () -> Limelight.Pipeline,
    private val direction: Int,
    private val seek: Boolean = true
) : CommandBase() {

    private var ticksAtSetpoint = 0

    // Must be at the setpoint for 0.2 seconds.
    private var tickThreshold = 0.2 / Romance.period

    //private val setpoint = if (targetPipeline == Limelight.Pipeline.ReflectiveTape) 0.0 else 1.0

    override fun initialize() {
        Romance.drive.changeState(DriveTrain.State.Autonomous)

        limelight.changeState(Limelight.State.Targeting(targetPipeline.invoke()))

        ticksAtSetpoint = 0
    }

    override fun execute() {
        Romance.drive.changeState(DriveTrain.State.Autonomous)
        limelight.changeState(Limelight.State.Targeting(targetPipeline.invoke()))

        if (limelight.hasTarget()) {
            // Turn towards target.
            val distance = limelight.target.xOffset

            val output = limelight.controller.calculate(distance, 0.0)
            val absolute = Math.abs(output)
            val sign = -sign(output)

            val remapped = absolute * (1 - 0.5) + 0.2

            Romance.drive.autonomousInput.xSpeed = sign * remapped

            // TODO: Remove, keep aligning when going to shoot?
            if (limelight.controller.atSetpoint()) {
                ticksAtSetpoint++
            } else {
                ticksAtSetpoint = 0
            }
        } else if (seek) {
            // Turn until hasTarget.
            Romance.drive.autonomousInput.xSpeed = 0.5 * direction

            ticksAtSetpoint = 0
        }
    }

    override fun end(interrupted: Boolean) {
        Romance.drive.autonomousInput.xSpeed = 0.0

        Romance.drive.changeState(DriveTrain.State.Idle)
        limelight.changeState(Limelight.State.Idle)
    }

    override fun isFinished(): Boolean {
        return ticksAtSetpoint >= tickThreshold
    }

}