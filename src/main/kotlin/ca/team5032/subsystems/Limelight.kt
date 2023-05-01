package ca.team5032.subsystems

import ca.team5032.utils.Subsystem
import ca.team5032.utils.Tabbed
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.DriverStation


class Limelight : Subsystem<Limelight.State>("Limelight", State.Idle), Tabbed {
    enum class CameraMode(val value: Int) {
        Processing(0),
        Drive(1);
        companion object {
            fun from(value: Int) = values().firstOrNull { it.value == value } ?: Drive
        }
    }

    enum class Pipeline(val value: Int) {
        Apriltag(1),
        ReflectiveTape(2);

        companion object {
            fun from(value: Int) = values().firstOrNull { it.value == value } ?: ReflectiveTape
        }
    }

    enum class LEDMode(val value: Int) {
        Current(0),
        Off(1),
        Blink(2),
        On(3);

        companion object {
            fun from(value: Int) = values().firstOrNull { it.value == value } ?: On
        }
    }

    sealed class State {
        data class Targeting(val pipeline: Limelight.Pipeline) : State()
        object Idle : State()
    }

    val enabled = NetworkTableInstance.getDefault().getTable("limelight").containsKey("tv")

    val controller = PIDController(0.01, 0.0, 0.0)

    data class LimelightTarget(
        val xOffset: Double,
        val yOffset: Double,
        val areaPercentage: Double,
        val skew: Double,
        val shortestSide: Double,
        val longestSide: Double,
        val horizontalSide: Double,
        val verticalSide: Double
    )

    private val networkTable = NetworkTableInstance.getDefault().getTable("limelight")

    val target: LimelightTarget
        get() = LimelightTarget(
            networkTable.getEntry("tx").getDouble(0.0),
            networkTable.getEntry("ty").getDouble(0.0),
            networkTable.getEntry("ta").getDouble(0.0),
            networkTable.getEntry("ts").getDouble(0.0),
            networkTable.getEntry("tshort").getDouble(0.0),
            networkTable.getEntry("tlong").getDouble(0.0),
            networkTable.getEntry("thor").getDouble(0.0),
            networkTable.getEntry("tvert").getDouble(0.0)
        )

    var pipeline: Pipeline
        get() = Pipeline.from(networkTable.getEntry("getpipe").getNumber(0).toInt())
        set(v) { networkTable.getEntry("pipeline").setNumber(v.value) }

    var cameraMode: CameraMode
        get() = CameraMode.from(networkTable.getEntry("camMode").getNumber(0).toInt())
        set(v) { networkTable.getEntry("camMode").setNumber(v.value) }

    var ledMode: LEDMode
        get() = LEDMode.from(networkTable.getEntry("ledMode").getNumber(0).toInt())
        set(v) { networkTable.getEntry("ledMode").setNumber(v.value) }

    init {
        tab.addString("LED Mode") { ledMode.name }
        tab.addString("Camera Mode") { cameraMode.name }
        tab.addString("Pipeline") { pipeline.name }
        tab.addString("State") { state.javaClass.simpleName }
        tab.addNumber("tx", ::returnTx)
        controller.setTolerance(2.00)
        changeState(State.Targeting(Pipeline.Apriltag))
        cameraMode = CameraMode.Processing
        ledMode = LEDMode.On

        tab.add(controller)
    }

    override fun onStateChange(oldState: State, newState: State) {
        if (newState is State.Targeting) {
            pipeline = newState.pipeline
        }
    }

    override fun periodic() {
        state.let {
            when (it) {
                is State.Targeting -> {}
                is State.Idle -> {}
            }
        }
    }

    fun hasTarget() = networkTable.getEntry("tv").getNumber(0) == 1.0

    private fun returnTx(): Double{
        return networkTable.getEntry("tx").getDouble(0.0)
    }
}