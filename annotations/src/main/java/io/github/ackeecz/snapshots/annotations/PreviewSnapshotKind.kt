package io.github.ackeecz.snapshots.annotations

/**
 * Snapshot-kind tags for a preview's `@ShowkaseComposable` `extraMetadata`. Tag each snapshot preview
 * with exactly one of these to declare how it is rendered.
 */
object PreviewSnapshotKind {

    /**
     * Components are rendered in a smallest possible size and are not
     * snapshot for different devices.
     */
    const val Component = "component"

    /**
     * Screens are rendered within the full frame of the selected device
     * and snapshots are taken for the configured devices.
     */
    const val Screen = "screen"
}
