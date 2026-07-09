package io.github.ackeecz.snapshots.framework

import android.content.Context
import androidx.compose.runtime.Composable
import com.airbnb.android.showkase.models.ShowkaseBrowserComponent

/**
 * Resolved, validated configuration consumed by [SnapshotResolver].
 *
 * [componentsEnabled] toggles rendering of component-tagged previews; [devices] are the screen
 * targets (empty ⇒ screens disabled). [uiModes] and [fontScales] are shared by both kinds.
 */
internal data class SnapshotConfig(
    val previews: List<ShowkaseBrowserComponent>,
    val componentsEnabled: Boolean,
    val devices: List<DeviceConfig>,
    val uiModes: List<UiMode>,
    val fontScales: List<FontScale>,
    val excludes: List<(SnapshotVariant) -> Boolean> = emptyList(),
    val profiles: Map<String, ProfileOverride> = emptyMap(),
    val before: (Context) -> Unit = NO_OP_BEFORE,
    val decorate: @Composable (UiMode, @Composable () -> Unit) -> Unit = PASSTHROUGH_DECORATE,
)

/**
 * A per-preview override. Any subset of axes may be overridden; a `null` axis means "no override on
 * this axis". [devices] only affects screen-tagged previews.
 */
internal data class ProfileOverride(
    val devices: List<DeviceConfig>? = null,
    val uiModes: List<UiMode>? = null,
    val fontScales: List<FontScale>? = null,
)
