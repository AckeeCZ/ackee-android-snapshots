package io.github.ackeecz.snapshots.framework

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain

internal class DeviceShortcutTest : FunSpec({

    test("Device.Pixel6.portrait is DeviceConfig(PIXEL_6, PORTRAIT)") {
        Device.Pixel6.portrait shouldBe expectedConfig(Device.PIXEL_6, DeviceOrientation.PORTRAIT)
    }

    test("Device.Pixel6.landscape is DeviceConfig(PIXEL_6, LANDSCAPE)") {
        Device.Pixel6.landscape shouldBe expectedConfig(Device.PIXEL_6, DeviceOrientation.LANDSCAPE)
    }

    test("Device.Nexus10.portrait / landscape map to NEXUS_10") {
        Device.Nexus10.portrait shouldBe expectedConfig(Device.NEXUS_10, DeviceOrientation.PORTRAIT)
        Device.Nexus10.landscape shouldBe expectedConfig(Device.NEXUS_10, DeviceOrientation.LANDSCAPE)
    }

    test("Device.Pixel9ProFold.portrait / landscape map to PIXEL_9_PRO_FOLD") {
        Device.Pixel9ProFold.portrait shouldBe expectedConfig(Device.PIXEL_9_PRO_FOLD, DeviceOrientation.PORTRAIT)
        Device.Pixel9ProFold.landscape shouldBe expectedConfig(Device.PIXEL_9_PRO_FOLD, DeviceOrientation.LANDSCAPE)
    }

    test("every device has a snapshot-name label that is unique and free of underscores") {
        val labels = Device.entries.map { DeviceConfig(it, DeviceOrientation.PORTRAIT).name }
        labels.forEach { it shouldNotContain "_" }
        labels shouldBe labels.distinct()
    }
})

private fun expectedConfig(device: Device, orientation: DeviceOrientation) =
    DeviceConfig(device = device, orientation = orientation)
