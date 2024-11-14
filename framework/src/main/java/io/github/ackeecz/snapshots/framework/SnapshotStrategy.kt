package io.github.ackeecz.snapshots.framework

sealed interface SnapshotStrategy {

    val name: String

    data object Component : SnapshotStrategy {

        override val name = "component"
    }

    data class Screen(val device: Device) : SnapshotStrategy {

        override val name = device.name
    }
}
