package io.github.ackeecz.snapshots.framework

import android.content.Context
import androidx.compose.runtime.Composable
import io.kotest.core.spec.style.scopes.FunSpecContainerScope

/**
 * Backend that renders a composable and records or verifies its golden image. The framework resolves
 * the snapshot matrix, groups it by (kind, device, UI mode) and drives one engine per group.
 *
 * Implement this to add a new snapshot backend; `PaparazziEngine` in the `:paparazzi` module is the
 * reference implementation.
 */
interface SnapshotEngine {

    /**
     * Prepare the engine for a group of snapshots sharing the same [kind], [device] and [uiMode].
     * Called once per group inside the group's Kotest container [scope], before any [snapshot] call.
     * [device] is `null` for [SnapshotKind.Component], which is device-independent.
     */
    fun init(kind: SnapshotKind, device: DeviceConfig?, uiMode: UiMode, scope: FunSpecContainerScope)

    /** The Android [Context] of the prepared rendering environment. */
    val context: Context

    /** Render [content] and record/verify it against the golden identified by [goldenName]. */
    fun snapshot(goldenName: String, content: @Composable () -> Unit)
}
