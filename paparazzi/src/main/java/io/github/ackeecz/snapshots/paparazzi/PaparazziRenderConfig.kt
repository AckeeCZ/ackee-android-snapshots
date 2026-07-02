package io.github.ackeecz.snapshots.paparazzi

import com.android.ide.common.rendering.api.SessionParams
import app.cash.paparazzi.DeviceConfig as PaparazziDeviceConfig

/** The Paparazzi device frame and rendering mode a snapshot group is rendered with. */
internal data class PaparazziRenderConfig(
    val deviceConfig: PaparazziDeviceConfig,
    val renderingMode: SessionParams.RenderingMode,
)
