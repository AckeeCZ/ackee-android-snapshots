package io.github.ackeecz.snapshots.framework

data class DeviceConfig(
    val device: Device,
    val orientation: DeviceOrientation,
) {

    val name: String = "${device.name}_${orientation.name}"
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
