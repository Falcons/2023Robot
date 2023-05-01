package ca.team5032.commands.AutoCommands

import ca.team5032.commands.*
import ca.team5032.commands.ArmPositions.StowArmFast
import edu.wpi.first.wpilibj2.command.*

class Taxi: SequentialCommandGroup (
    DriveForTimeCommand(1.0,0.25,-1)
)