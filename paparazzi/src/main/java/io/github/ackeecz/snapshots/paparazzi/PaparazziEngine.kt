package io.github.ackeecz.snapshots.paparazzi

import android.content.Context
import androidx.compose.runtime.Composable
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.android.resources.ScreenOrientation
import io.github.ackeecz.snapshots.framework.Device
import io.github.ackeecz.snapshots.framework.DeviceConfig
import io.github.ackeecz.snapshots.framework.DeviceOrientation
import io.github.ackeecz.snapshots.framework.SnapshotEngine
import io.github.ackeecz.snapshots.framework.SnapshotStrategy
import io.kotest.core.spec.style.FunSpec
import app.cash.paparazzi.DeviceConfig as PaparazziDeviceConfig

class PaparazziEngine : SnapshotEngine {

    private lateinit var paparazzi: Paparazzi

    override val context: Context
        get() = paparazzi.context

    override fun init(strategy: SnapshotStrategy, funSpec: FunSpec) {
        paparazzi = when (strategy) {
            is SnapshotStrategy.Screen -> createPaparazzi(
                device = mapDevice(strategy.deviceConfig),
                renderingMode = SessionParams.RenderingMode.NORMAL
            )

            is SnapshotStrategy.Component -> createPaparazzi(
                device = PaparazziDeviceConfig.NEXUS_10,
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
        device: PaparazziDeviceConfig,
        renderingMode: SessionParams.RenderingMode
    ): Paparazzi = Paparazzi(
        deviceConfig = device,
        renderingMode = renderingMode,
        theme = "android:Theme.Material.Light.NoActionBar",
    )

    private fun mapDevice(deviceConfig: DeviceConfig): PaparazziDeviceConfig {
        return when (deviceConfig.device) {
            Device.PIXEL_6 -> PaparazziDeviceConfig.PIXEL_6
            Device.NEXUS_10 -> PaparazziDeviceConfig.NEXUS_10
        }.copy(
            orientation = when (deviceConfig.orientation) {
                DeviceOrientation.LANDSCAPE -> ScreenOrientation.LANDSCAPE
                DeviceOrientation.PORTRAIT -> ScreenOrientation.PORTRAIT
            }
        )
    }
}
