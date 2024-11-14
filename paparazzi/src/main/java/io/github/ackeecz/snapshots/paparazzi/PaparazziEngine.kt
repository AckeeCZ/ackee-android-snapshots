package io.github.ackeecz.snapshots.paparazzi

import android.content.Context
import androidx.compose.runtime.Composable
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import io.github.ackeecz.snapshots.framework.Device
import io.github.ackeecz.snapshots.framework.SnapshotEngine
import io.github.ackeecz.snapshots.framework.SnapshotStrategy
import io.kotest.core.spec.style.FunSpec

class PaparazziEngine : SnapshotEngine {

    private lateinit var paparazzi: Paparazzi

    override val context: Context
        get() = paparazzi.context

    override fun init(strategy: SnapshotStrategy, funSpec: FunSpec) {
        paparazzi = when (strategy) {
            is SnapshotStrategy.Screen -> createPaparazzi(
                device = mapDevice(strategy.device),
                renderingMode = SessionParams.RenderingMode.NORMAL
            )

            is SnapshotStrategy.Component -> createPaparazzi(
                device = DeviceConfig.NEXUS_10,
                renderingMode = SessionParams.RenderingMode.SHRINK
            )
        }
        PaparazziExtension(paparazzi).also {
            funSpec.extension(it)
        }
    }

    override fun snapshot(content: @Composable () -> Unit) {
        paparazzi.snapshot {
            content()
        }
    }

    private fun createPaparazzi(
        device: DeviceConfig,
        renderingMode: SessionParams.RenderingMode
    ): Paparazzi = Paparazzi(
        deviceConfig = device,
        renderingMode = renderingMode,
        theme = "android:Theme.Material.Light.NoActionBar",
    )

    private fun mapDevice(device: Device): DeviceConfig {
        return when (device) {
            Device.PIXEL_6 -> DeviceConfig.PIXEL_6
            Device.NEXUS_10 -> DeviceConfig.NEXUS_10
        }
    }
}
