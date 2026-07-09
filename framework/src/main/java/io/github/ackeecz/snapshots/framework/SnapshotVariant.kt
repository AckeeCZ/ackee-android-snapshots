package io.github.ackeecz.snapshots.framework

import io.github.ackeecz.snapshots.annotations.ExperimentalSnapshotsApi

/**
 * A single resolved snapshot coordinate — one cell of the (kind × device × UI mode × font scale)
 * matrix the resolver produces, and the value passed to a `variants { exclude { … } }` predicate.
 *
 * @property kind how the preview is rendered — [SnapshotKind.Component] or [SnapshotKind.Screen].
 * @property device the screen device for a [SnapshotKind.Screen] variant, or `null` for a
 *   [SnapshotKind.Component] variant (which is device-independent).
 * @property uiMode the UI mode (light or dark) the variant is rendered in.
 * @property fontScale the font scale the variant is rendered at.
 */
@ExperimentalSnapshotsApi
data class SnapshotVariant(
    val kind: SnapshotKind,
    val device: DeviceConfig?,
    val uiMode: UiMode,
    val fontScale: FontScale,
)
