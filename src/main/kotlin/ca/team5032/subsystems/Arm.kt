package ca.team5032.subsystems
import ca.team5032.*
import ca.team5032.commands.ArmPositions.*
import ca.team5032.commands.HomeCommand
import ca.team5032.commands.StowHomeCommand
import ca.team5032.utils.DoubleProperty
import ca.team5032.utils.Subsystem
import ca.team5032.utils.Tabbed
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import edu.wpi.first.wpilibj.XboxController
import kotlin.math.abs

class Arm : Subsystem<Arm.State>("Arm", State.Idle), Tabbed {

    companion object {
        // The default power of the intake motor.
        val MaxEncoderExtensionBoomOne = DoubleProperty("Boom One Max Extension", -535000.0)
        val MotorPowerBoomOne = DoubleProperty("Boom One Power", 0.99)
        val MaxEncoderExtensionBoomTwo = DoubleProperty("Boom Two Max Extension", -437300.0)
        val MotorPowerBoomTwo = DoubleProperty("Boom Two Power", 0.99)
        val PivotPower = DoubleProperty("Pivot Power", 0.65)
        val MaxEncoderExtensionPivot = DoubleProperty("Pivot Max Extension", -242000.0)
        val WristPower = DoubleProperty("Wrist Power", 0.25)
        val MaxEncoderExtensionWrist = DoubleProperty("Max Wrist Extension", -70000.0)
        val DEADBAND_VALUE = 0.20
    }

    sealed class State {
        object LowIntake : State()
        object HighIntake : State()
        object HighPost : State()
        object MidPost : State()
        object Idle: State()
        object FallenIntake: State()
        object Stow: State()
        object Moving : State()
    }

    val pivotMotor = WPI_TalonFX(PIVOT_MOTOR)
    val boomOneMotor = WPI_TalonFX(BOOM_ONE_ID)
    val boomTwoMotor = WPI_TalonFX(BOOM_TWO_ID)
    val wristMotor = WPI_TalonFX(WRIST_MOTOR_ID)

    var mCancelCommand = false

    private val controller: XboxController = Romance.peripheralController
    private var power = 0.0

    private var testCompress = false
    private var testCompressPos = 0.0

    private var lowIntakeTestFinish = false

    init {
        mCancelCommand = false
        resetPivotLimits()
        resetWristLimits()
        resetBoomTwoLimits()
        resetBoomOneLimits()
        pivotMotor.setNeutralMode(NeutralMode.Brake)
        boomOneMotor.setNeutralMode(NeutralMode.Brake)
        boomTwoMotor.setNeutralMode(NeutralMode.Brake)
        wristMotor.setNeutralMode(NeutralMode.Brake)

//        pivotMotor.setNeutralMode(NeutralMode.Coast)
//        boomOneMotor.setNeutralMode(NeutralMode.Coast)
//        boomTwoMotor.setNeutralMode(NeutralMode.Coast)
//        wristMotor.setNeutralMode(NeutralMode.Coast)

        boomOneMotor.selectedSensorPosition = 0.0
        boomTwoMotor.selectedSensorPosition = 0.0
//        tab.add("State",state)
        tab.addString("Arm state") { state.javaClass.simpleName }
        tab.addNumber("Pivot Power",::showPivotPower)
        tab.addBoolean("Low intake finish",::showFinishIntake)
        tab.addNumber("Pivot Pos", ::showPivotPos)
        tab.addNumber("Boom two Pos", ::showBoomTwoPos)
        tab.addNumber("Boom one Pos", ::showBoomOnePos)
        tab.addNumber("Wrist Pos", ::showWristPos)
    }

    private val hasLeftYInput: Boolean
        get() = abs(controller.leftY) > DEADBAND_VALUE

    private val hasRightYInput: Boolean
        get() = abs(controller.rightY) > DEADBAND_VALUE

    private val hasRightTrigger: Boolean
        get() = abs(controller.rightTriggerAxis) > DEADBAND_VALUE

    private val hasLeftTrigger: Boolean
        get() = abs(controller.leftTriggerAxis) > DEADBAND_VALUE

    override fun periodic() {
        if (hasLeftYInput){
            cancelCommand()
            wristMotor.set(controller.leftY*0.25)
        }
        if (hasRightYInput){
            cancelCommand()
            pivotMotor.set(controller.rightY*0.35)
        }
        if (hasLeftTrigger){
            cancelCommand()
            boomTwoMotor.set(controller.leftTriggerAxis*0.75)
        }
        if (hasRightTrigger){
            cancelCommand()
            boomTwoMotor.set(-controller.rightTriggerAxis*0.75)
        }
        // cone commands
        //CANCEL PREVIOUS COMMAND BEFORE RUNNING
        if (Romance.selectItem.state is SelectItem.State.Cone) {
            state.let {
                when (it) {
                    is State.Moving -> idleMotors()
                    is State.LowIntake -> idleMotors()
                    is State.HighIntake -> idleMotors()
                    is State.HighPost -> idleMotors()
                    is State.MidPost -> idleMotors()
                    is State.Stow -> idleMotors()
                    is State.FallenIntake -> idleMotors()
                    is State.Idle -> {
//                        LowIntakeCone().cancel()
//                        MidPostCone().cancel()
//                        HighPostCone().cancel()
//                        StowArm().cancel()
//                        pivotMotor.set(0.0)
//                        boomOneMotor.set(0.0)
//                        boomTwoMotor.set(0.0)
//                        wristMotor.set(0.0)
                        idleMotors()
                    }
                }
            }
        } else {
            state.let {
                when (it) {
                    is State.Moving -> idleMotors()
                    is State.LowIntake -> idleMotors()
                    is State.HighIntake -> idleMotors()
                    is State.HighPost -> idleMotors()
                    is State.MidPost -> idleMotors()
                    is State.Stow -> idleMotors()
                    is State.FallenIntake -> idleMotors()
                    is State.Idle -> {
                        idleMotors()
                    }
                }
            }
        }
        //reset encoder positions
        if (boomTwoMotor.isFwdLimitSwitchClosed == 1) {boomTwoMotor.selectedSensorPosition = 0.0}
        if (boomOneMotor.isFwdLimitSwitchClosed == 1) {boomOneMotor.selectedSensorPosition = 0.0}
        if (pivotMotor.isFwdLimitSwitchClosed == 1) {pivotMotor.selectedSensorPosition = 0.0}
        if (wristMotor.isFwdLimitSwitchClosed == 1) {wristMotor.selectedSensorPosition = 0.0}
    }

    fun lowIntake() {
        changeState(State.LowIntake)
        cancelCommand()
        Romance.selectItem.state.let{
            when (it){
                is SelectItem.State.Cone -> {
//                    when (state){
//                        is State.LowIntake -> {LowIntakeCone().schedule()}
//                    }
                    LowIntakeCone().schedule()
                }
                is SelectItem.State.Cube -> {
                    LowIntakeCube().schedule()
                }
            }
        }
    }

    fun highIntake() {
        changeState(State.HighIntake)
        cancelCommand()
        Romance.selectItem.state.let{
            when (it){
                is SelectItem.State.Cone -> {
                    HighIntakeCone().schedule()
                }
                is SelectItem.State.Cube -> {
                    HighIntakeCube().schedule()
                }
            }
        }
    }

    fun highPost() {
        changeState(State.HighPost)
        cancelCommand()
        Romance.selectItem.state.let{
            when (it) {
                is SelectItem.State.Cone -> {
                    HighPostCone().schedule()
                }
                is SelectItem.State.Cube -> {
                    HighPostCube().schedule()
                }
            }
        }
    }
    fun midPost() {
        changeState(State.MidPost)
        cancelCommand()
        Romance.selectItem.state.let{
            when (it){
                is SelectItem.State.Cone -> {
                    MidPostCone().schedule()
                }
                is SelectItem.State.Cube -> {
                    MidPostCube().schedule()
                }
            }
        }
    }
    fun idle() {changeState(State.Idle)}
    fun stow() {
        cancelCommand()
        if (state is State.HighPost || state is State.MidPost || state is State.HighIntake){
            StowArmSlow().schedule()
        } else{StowArmFast().schedule()}
        changeState(State.Stow)
//        Romance.selectItem.state.let{
//            when (it){
//                is SelectItem.State.Cone -> {
//                    StowArm().schedule()
//                }
//                is SelectItem.State.Cube -> {
//                    StowArm().schedule()
//                }
//            }
//        }
    }

    fun fallenIntake() {
        cancelCommand()
        changeState(State.FallenIntake)
        FallenIntakeCone().schedule()
    }

    fun homeArm() {
        cancelCommand()
        changeState(State.Idle)
        HomeCommand().schedule()
    }

    fun stowHome() {
        changeState(State.Stow)
        StowHomeCommand().schedule()
    }

    private fun idleMotors() {
//        LowIntakeCone().cancel()
//        MidPostCone().cancel()
//        HighPostCone().cancel()
//        StowArm().cancel()
//        PivotCommand(0.0).cancel()
//        ExtendBoomOneCommand(-1).cancel()
//        ExtendBoomTwoCommand(0.0).cancel()
//        WristCommand(0.0).cancel()
//
//        pivotMotor.set(0.0)
//        boomOneMotor.set(0.0)
//        boomTwoMotor.set(0.0)
//        wristMotor.set(0.0)
    }
    private fun showFinishIntake():Boolean {return lowIntakeTestFinish}

    fun resetBoomOneLimits(){
        boomOneMotor.configForwardSoftLimitEnable(true)
        boomOneMotor.configReverseSoftLimitEnable(true)
        boomOneMotor.configReverseSoftLimitThreshold(MaxEncoderExtensionBoomOne.value)
        boomOneMotor.configForwardSoftLimitThreshold(0.0)
    }
    fun resetBoomTwoLimits(){
        boomTwoMotor.configForwardSoftLimitEnable(true)
        boomTwoMotor.configReverseSoftLimitEnable(true)
        boomTwoMotor.configReverseSoftLimitThreshold(MaxEncoderExtensionBoomTwo.value)
        boomTwoMotor.configForwardSoftLimitThreshold(0.0)
    }
    fun resetPivotLimits(){
        pivotMotor.configForwardSoftLimitEnable(true)
        pivotMotor.configReverseSoftLimitEnable(true)
        pivotMotor.configReverseSoftLimitThreshold(MaxEncoderExtensionPivot.value)
        pivotMotor.configForwardSoftLimitThreshold(0.0)
    }
    fun resetWristLimits(){
        wristMotor.configForwardSoftLimitEnable(true)
        wristMotor.configReverseSoftLimitEnable(true)
        wristMotor.configReverseSoftLimitThreshold(MaxEncoderExtensionWrist.value)
        wristMotor.configForwardSoftLimitThreshold(0.0)
    }
    fun showPivotPower(): Double{
        return pivotMotor.motorOutputVoltage
    }

    fun showPivotPos(): Double {
        return pivotMotor.selectedSensorPosition
    }

    fun showBoomTwoPos(): Double {
        return boomTwoMotor.selectedSensorPosition
    }

    fun showBoomOnePos(): Double {
        return boomOneMotor.selectedSensorPosition
    }

    fun showWristPos(): Double {
        return wristMotor.selectedSensorPosition
    }

    fun cancelCommand() {
        wristMotor.set(0.0)
        pivotMotor.set(0.0)
        boomTwoMotor.set(0.0)
        boomOneMotor.set(0.0)
        //Romance.clawC.grabMotor.set(0.0)
        Romance.clawC.intakeMotor.set(0.0)
        mCancelCommand = true
    }
}