package io.github.ackeecz.snapshots.framework.dsl

import io.github.ackeecz.snapshots.annotations.ExperimentalSnapshotsApi
import io.github.ackeecz.snapshots.framework.DeviceConfig
import io.github.ackeecz.snapshots.framework.FontScale
import io.github.ackeecz.snapshots.framework.UiMode

/**
 * Body of a named [VariantsScope.profile]. Each axis set here replaces the class-level axis for any
 * preview that references the profile; axes left unset fall through to the class config.
 */
@ExperimentalSnapshotsApi
@SnapshotDsl
interface ProfileOverrideScope {

    /** Override the devices for the profiled screen preview (ignored for component-tagged previews). */
    fun devices(vararg devices: DeviceConfig)

    /** Override the UI modes for the profiled preview. */
    fun uiModes(vararg modes: UiMode)

    /** Override the font scales for the profiled preview. */
    fun fontScales(vararg scales: FontScale)
}
