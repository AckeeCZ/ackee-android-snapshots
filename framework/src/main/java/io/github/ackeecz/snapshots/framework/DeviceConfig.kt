package io.github.ackeecz.snapshots.framework

data class DeviceConfig(
    val device: Device,
    val orientation: DeviceOrientation,
) {

    /**
     * Source-mirroring label, e.g. `Pixel6-portrait` — the `Device.Pixel6.portrait` shortcut with `-`
     * joining the segments. Unlike the raw enum names it contains no `_`, so it reads as a single segment
     * in a snapshot name; `-` (not `.`) is used because a `.` is rewritten to `_` by the Kotest test
     * name → Paparazzi golden-file pipeline.
     */
    val name: String = "${device.label}-${orientation.label}"
}

internal val Device.label: String
    get() = when (this) {
        Device.PIXEL_6 -> "Pixel6"
        Device.NEXUS_10 -> "Nexus10"
        Device.PIXEL_9_PRO_FOLD -> "Pixel9ProFold"
    }

internal val DeviceOrientation.label: String
    get() = when (this) {
        DeviceOrientation.LANDSCAPE -> "landscape"
        DeviceOrientation.PORTRAIT -> "portrait"
    }

enum class Device {
    // phones
    PIXEL_6,

    // tablets
    NEXUS_10,

    // foldables — unfolded inner display
    PIXEL_9_PRO_FOLD;

    /** Ready-made [DeviceConfig] shortcuts for the Pixel 6 phone. */
    object Pixel6 {

        /** Pixel 6 in portrait orientation. */
        val portrait: DeviceConfig = DeviceConfig(device = PIXEL_6, orientation = DeviceOrientation.PORTRAIT)

        /** Pixel 6 in landscape orientation. */
        val landscape: DeviceConfig = DeviceConfig(device = PIXEL_6, orientation = DeviceOrientation.LANDSCAPE)
    }

    /** Ready-made [DeviceConfig] shortcuts for the Nexus 10 tablet. */
    object Nexus10 {

        /** Nexus 10 in portrait orientation. */
        val portrait: DeviceConfig = DeviceConfig(device = NEXUS_10, orientation = DeviceOrientation.PORTRAIT)

        /** Nexus 10 in landscape orientation. */
        val landscape: DeviceConfig = DeviceConfig(device = NEXUS_10, orientation = DeviceOrientation.LANDSCAPE)
    }

    /**
     * Ready-made [DeviceConfig] shortcuts for the Pixel 9 Pro Fold's unfolded inner display
     * (852x883dp). Both orientations sit between the tablet and phone widths, which is the band a
     * layout driven by width breakpoints is most likely to get wrong.
     */
    object Pixel9ProFold {

        /** Pixel 9 Pro Fold, unfolded, in portrait orientation. */
        val portrait: DeviceConfig = DeviceConfig(device = PIXEL_9_PRO_FOLD, orientation = DeviceOrientation.PORTRAIT)

        /** Pixel 9 Pro Fold, unfolded, in landscape orientation. */
        val landscape: DeviceConfig = DeviceConfig(device = PIXEL_9_PRO_FOLD, orientation = DeviceOrientation.LANDSCAPE)
    }
}

enum class DeviceOrientation {

    LANDSCAPE,
    PORTRAIT
}
