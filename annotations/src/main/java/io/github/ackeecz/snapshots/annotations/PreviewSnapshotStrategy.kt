package io.github.ackeecz.snapshots.annotations

object PreviewSnapshotStrategy {

    /**
     * Components are rendered in a smallest possible size and are not
     * snapshot for different devices.
     */
    const val Component = "component"

    /**
     * Screens are rendered within the full frame of the selected device
     * and snapshots are taken for all devices.
     */
    const val Screen = "screen"
}


