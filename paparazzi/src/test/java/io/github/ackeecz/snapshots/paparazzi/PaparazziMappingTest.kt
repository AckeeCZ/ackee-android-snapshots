package io.github.ackeecz.snapshots.paparazzi

import com.android.ide.common.rendering.api.SessionParams
import com.android.resources.NightMode
import com.android.resources.ScreenOrientation
import io.github.ackeecz.snapshots.framework.Device
import io.github.ackeecz.snapshots.framework.DeviceConfig
import io.github.ackeecz.snapshots.framework.DeviceOrientation
import io.github.ackeecz.snapshots.framework.SnapshotKind
import io.github.ackeecz.snapshots.framework.UiMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import app.cash.paparazzi.DeviceConfig as PaparazziDeviceConfig

class PaparazziMappingTest : FunSpec({

    context("uiMode maps to NightMode") {
        test("LIGHT maps to NOTNIGHT") {
            mapToNightMode(UiMode.LIGHT) shouldBe NightMode.NOTNIGHT
        }
        test("DARK maps to NIGHT") {
            mapToNightMode(UiMode.DARK) shouldBe NightMode.NIGHT
        }
    }

    context("strategy maps to device + rendering mode") {
        test("Component maps to NEXUS_10 + SHRINK") {
            renderConfigFor(SnapshotKind.Component, device = null) shouldBe PaparazziRenderConfig(
                deviceConfig = PaparazziDeviceConfig.NEXUS_10,
                renderingMode = SessionParams.RenderingMode.SHRINK,
            )
        }
        test("Screen maps to its device with NORMAL rendering") {
            val device = DeviceConfig(Device.PIXEL_6, DeviceOrientation.PORTRAIT)
            renderConfigFor(SnapshotKind.Screen, device) shouldBe PaparazziRenderConfig(
                deviceConfig = PaparazziDeviceConfig.PIXEL_6.copy(orientation = ScreenOrientation.PORTRAIT),
                renderingMode = SessionParams.RenderingMode.NORMAL,
            )
        }
        test("a Nexus 10 screen maps to the Nexus 10 device") {
            val device = DeviceConfig(Device.NEXUS_10, DeviceOrientation.PORTRAIT)
            renderConfigFor(SnapshotKind.Screen, device).deviceConfig shouldBe
                PaparazziDeviceConfig.NEXUS_10.copy(orientation = ScreenOrientation.PORTRAIT)
        }
        test("Screen orientation is applied to the device config") {
            val device = DeviceConfig(Device.PIXEL_6, DeviceOrientation.LANDSCAPE)
            renderConfigFor(SnapshotKind.Screen, device).deviceConfig.orientation shouldBe ScreenOrientation.LANDSCAPE
        }
    }
})
