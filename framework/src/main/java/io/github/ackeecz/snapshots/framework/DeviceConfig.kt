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
    NEXUS_10;

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
}

enum class DeviceOrientation {

    LANDSCAPE,
    PORTRAIT
}
