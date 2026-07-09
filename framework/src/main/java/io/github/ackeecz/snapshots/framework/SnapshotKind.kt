package io.github.ackeecz.snapshots.framework

/**
 * How a preview is rendered when snapshot. Chosen per preview by its snapshot-kind tag (see
 * `PreviewSnapshotKind` in the `:annotations` module).
 */
enum class SnapshotKind {

    /** Rendered device-independent, at the minimal size that fits the content. */
    Component,

    /** Rendered within a device's full frame ([DeviceConfig]), once per configured device. */
    Screen
}
