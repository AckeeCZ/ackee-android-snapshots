package io.github.ackeecz.snapshots.framework.dsl

import io.github.ackeecz.snapshots.framework.DeviceConfig
import io.github.ackeecz.snapshots.framework.FontScale
import io.github.ackeecz.snapshots.framework.SnapshotVariant
import io.github.ackeecz.snapshots.framework.UiMode

/**
 * Declares the snapshot variant matrix. The enabled kinds ([components] / [screens]) are combined with
 * the [uiModes] and [fontScales] axes into a Cartesian product, minus any [exclude]d cells; [profile]s
 * define named per-preview overrides.
 */
@SnapshotDsl
interface VariantsScope {

    /** Enable snapshots of component-tagged previews (rendered device-independent, at minimal size). */
    fun components()

    /** Enable snapshots of screen-tagged previews, rendered on each of the given [devices]. */
    fun screens(vararg devices: DeviceConfig)

    /** The UI modes every enabled preview is rendered in. At least one value is required. */
    fun uiModes(vararg modes: UiMode)

    /** The font scales every enabled preview is rendered at. At least one value is required. */
    fun fontScales(vararg scales: FontScale)

    /** Drop every [SnapshotVariant] matching [predicate] from the matrix. May be called more than once. */
    fun exclude(predicate: (SnapshotVariant) -> Boolean)

    /**
     * Define a named override [key] that a preview references (via an `extraMetadata` token) to narrow
     * or widen its axes for that preview only; see [ProfileOverrideScope].
     */
    fun profile(key: String, block: ProfileOverrideScope.() -> Unit)
}
