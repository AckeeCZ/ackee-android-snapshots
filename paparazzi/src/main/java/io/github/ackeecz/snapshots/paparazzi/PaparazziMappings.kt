package io.github.ackeecz.snapshots.paparazzi

import com.android.ide.common.rendering.api.SessionParams
import com.android.resources.NightMode
import com.android.resources.ScreenOrientation
import io.github.ackeecz.snapshots.framework.Device
import io.github.ackeecz.snapshots.framework.DeviceConfig
import io.github.ackeecz.snapshots.framework.DeviceOrientation
import io.github.ackeecz.snapshots.framework.SnapshotKind
import io.github.ackeecz.snapshots.framework.UiMode
import app.cash.paparazzi.DeviceConfig as PaparazziDeviceConfig

internal fun mapToNightMode(uiMode: UiMode): NightMode = when (uiMode) {
    UiMode.LIGHT -> NightMode.NOTNIGHT
    UiMode.DARK -> NightMode.NIGHT
}

internal fun renderConfigFor(kind: SnapshotKind, device: DeviceConfig?): PaparazziRenderConfig =
    when (kind) {
        SnapshotKind.Component -> PaparazziRenderConfig(
            deviceConfig = PaparazziDeviceConfig.NEXUS_10,
            renderingMode = SessionParams.RenderingMode.SHRINK,
        )
        SnapshotKind.Screen -> {
            val screenDevice = requireNotNull(device)
            PaparazziRenderConfig(
                deviceConfig = mapDevice(screenDevice.device).copy(orientation = mapOrientation(screenDevice.orientation)),
                renderingMode = SessionParams.RenderingMode.NORMAL,
            )
        }
    }

private fun mapDevice(device: Device): PaparazziDeviceConfig = when (device) {
    Device.PIXEL_6 -> PaparazziDeviceConfig.PIXEL_6
    Device.NEXUS_10 -> PaparazziDeviceConfig.NEXUS_10
}

private fun mapOrientation(orientation: DeviceOrientation): ScreenOrientation = when (orientation) {
    DeviceOrientation.PORTRAIT -> ScreenOrientation.PORTRAIT
    DeviceOrientation.LANDSCAPE -> ScreenOrientation.LANDSCAPE
}
