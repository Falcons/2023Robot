package ca.team5032

//import ca.team5032.commands.RotateArm
import ca.team5032.commands.ArmPositions.HighPostCone
import ca.team5032.commands.ArmPositions.LowIntakeCone
import ca.team5032.commands.ArmPositions.MidPostCone
import ca.team5032.commands.ArmPositions.StowArm
import ca.team5032.commands.*
import ca.team5032.subsystems.*
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandScheduler
import edu.wpi.first.wpilibj2.command.button.JoystickButton
import edu.wpi.first.wpilibj2.command.button.POVButton
/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
object Romance : TimedRobot() {

//    private val leftFront = WPI_TalonFX(1)
//    private val leftRear = WPI_TalonFX(2)
//    private val rightFront = WPI_TalonFX(3)
//    private val rightRear = WPI_TalonFX(4)
//
//    private val m_left = MotorControllerGroup(leftFront, leftRear)
//    private val m_right = MotorControllerGroup(rightFront, rightRear)
//    private val m_drive = DifferentialDrive(m_left,m_right)

    val driveController = XboxController(0)
    val peripheralController = XboxController(1)

    //val claw = Claw()
    val pivot = Pivot()
//    val wrist = Wrist()
    val selectItem = SelectItem()
    val clawC = ClawC()
//    val boomTwo = BoomTwoC()
//    val boomOne = BoomOneC()
    val arm = Arm()
    val drive = DriveTrain()
    private var autonomousCommand: Command? = null
    private var robotContainer: RobotContainer? = null

    /**
     * This function is run when the robot is first started up and should be used for any
     * initialization code.
     */


    private fun registerCommands() {
//        JoystickButton(driveController, XboxController.Button.kX.value)
//            .onTrue(claw::intake, claw).onFalse(claw::stopIntake, claw)
//        JoystickButton(driveController, XboxController.Button.kB.value)
//            .onTrue(claw::intake, claw).onFalse(claw::stopIntake, claw)

//        JoystickButton(peripheralController,XboxController.Button.kX.value)
//            .whenPressed(claw::intake, claw).whenReleased(claw::stopIntake, claw)

//        JoystickButton(peripheralController,XboxController.Button.kB.value)
//            .whenPressed(claw::eject, claw).whenReleased(claw::stopIntake, claw)
//
//        JoystickButton(peripheralController,XboxController.Button.kY.value)
//            .whenPressed(claw::grab, claw).whenReleased(claw::stopGrab, claw)
//
//        JoystickButton(peripheralController,XboxController.Button.kA.value)
//            .whenPressed(claw::release, claw).whenReleased(claw::stopGrab, claw)



//        POVButton(peripheralController, 0).whenPressed(claw::zeroGrab, claw)
//        POVButton(peripheralController, 180).whenPressed(claw::cancelGrab, claw)

        //POVButton(peripheralController, 90).whenPressed(arm::zeroEncoder, arm)

//        POVButton(peripheralController, 270).whenPressed(claw::compressGrab, claw).whenReleased(claw::stopGrab, claw)
//        POVButton(peripheralController, 0).whenPressed({HighPostCone().schedule()}, arm).whenReleased({HighPostCone().cancel()}, arm)
//        POVButton(peripheralController, 90).whenPressed({MidPostCone().schedule()}, arm).whenReleased({MidPostCone().cancel()}, arm)
//        POVButton(peripheralController, 180).whenPressed({LowIntakeCone().schedule()}, arm).whenReleased({LowIntakeCone().cancel()}, arm)
//        POVButton(peripheralController, 270).whenPressed({StowArm().schedule()}, arm).whenReleased({ cancelCommands() }, arm)

//        POVButton(peripheralController, 0).whenPressed({HighPostCone().schedule()}, arm)
//        POVButton(peripheralController, 90).whenPressed({MidPostCone().schedule()}, arm)
//        POVButton(peripheralController, 180).whenPressed({LowIntakeCone().schedule()}, arm)
//        POVButton(peripheralController, 270).whenPressed({StowArm().schedule()}, arm)

        POVButton(peripheralController, 0).whenPressed(arm::highPost, arm)
        POVButton(peripheralController, 90).whenPressed(arm::midPost, arm)
        POVButton(peripheralController, 180).whenPressed(arm::lowIntake, arm)
        POVButton(peripheralController, 270).whenPressed(arm::stow, arm)

        JoystickButton(peripheralController,XboxController.Button.kX.value)
            .whenPressed({ClawIntakeCommand().schedule()}, clawC)

//        JoystickButton(peripheralController,XboxController.Button.kB.value)
//            .whenPressed({CompressCommand(-10000.0).schedule()}, clawC)

        JoystickButton(peripheralController,XboxController.Button.kRightBumper.value)
            .whenPressed(arm::cancelCommand, arm)

        JoystickButton(driveController,XboxController.Button.kB.value)
            .whenPressed({HomeCommand().schedule()}, arm)
    }

    override fun robotInit() {
        // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
        // autonomous chooser on the dashboard.
        robotContainer = RobotContainer()
        registerCommands()
    }

    /**
     * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
     * that you want ran during disabled, autonomous, teleoperated and test.
     *
     *
     * This runs after the mode specific periodic functions, but before LiveWindow and
     * SmartDashboard integrated updating.
     */
    override fun robotPeriodic() {
        // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
        // commands, running already-scheduled commands, removing finished or interrupted commands,
        // and running subsystem periodic() methods.  This must be called from the robot's periodic
        // block in order for anything in the Command-based framework to work.
        CommandScheduler.getInstance().run()
    }

    /** This function is called once each time the robot enters Disabled mode.  */
    override fun disabledInit() {}

    /** This function is called periodically when disabled.  */
    override fun disabledPeriodic() {}

    /** This autonomous runs the autonomous command selected by your [RobotContainer] class.  */
    override fun autonomousInit() {
        autonomousCommand = robotContainer?.autonomousCommand

        // Schedule the autonomous command (example)
        // Note the Kotlin safe-call(?.), this ensures autonomousCommand is not null before scheduling it
        autonomousCommand?.schedule()
    }

    /** This function is called periodically during autonomous.  */
    override fun autonomousPeriodic() {}

    /** This function is called once when teleop is enabled.  */
    override fun teleopInit() {
        // This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to
        // continue until interrupted by another command, remove
        // this line or comment it out.
        // Note the Kotlin safe-call(?.), this ensures autonomousCommand is not null before cancelling it
        autonomousCommand?.cancel()
    }

    /** This function is called periodically during operator control.  */
    override fun teleopPeriodic() {
        //m_drive.curvatureDrive(-driveController.leftY, driveController.rightX, driveController.leftBumper)
    }

    /** This function is called once when test mode is enabled.  */
    override fun testInit() {
        // Cancels all running commands at the start of test mode.
        CommandScheduler.getInstance().cancelAll()
    }

    /** This function is called periodically during test mode.  */
    override fun testPeriodic() {}

    /** This function is called once when the robot is first started up.  */
    override fun simulationInit() {}

    /** This function is called periodically whilst in simulation.  */
    override fun simulationPeriodic() {}
}
