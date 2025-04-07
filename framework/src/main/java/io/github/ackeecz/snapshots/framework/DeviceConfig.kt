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
    NEXUS_10
}

enum class DeviceOrientation {

    LANDSCAPE,
    PORTRAIT
}
