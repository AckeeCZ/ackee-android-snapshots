package io.github.ackeecz.snapshots

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.airbnb.android.showkase.models.Showkase
import com.airbnb.android.showkase.models.ShowkaseElementsMetadata
import io.github.ackeecz.snapshots.framework.UiMode
import io.github.ackeecz.snapshots.ui.theme.SnapshotsSampleTheme

/**
 * The Showkase previews scoped to a single [group]. Each per-concern test class snapshots only its own
 * group, keeping its golden set focused so a regression points at a specific framework feature.
 */
internal fun previewsInGroup(group: String): ShowkaseElementsMetadata =
    ShowkaseElementsMetadata(
        componentList = Showkase.getMetadata().componentList.filter { it.group == group },
    )

/**
 * Shared decoration for every sample snapshot: the sample app theme keyed off the [UiMode], painted over
 * a themed surface so the light/dark difference is visible even for otherwise transparent previews.
 */
internal val sampleDecorate: @Composable (UiMode, @Composable () -> Unit) -> Unit = { uiMode, content ->
    SnapshotsSampleTheme(darkTheme = uiMode == UiMode.DARK, dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            content()
        }
    }
}
