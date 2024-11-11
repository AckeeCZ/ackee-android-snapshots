package io.github.ackeecz.snapshots.paparazzi

import android.content.Context
import androidx.compose.runtime.Composable
import com.airbnb.android.showkase.models.ShowkaseBrowserComponent
import io.github.ackeecz.snapshots.core.AckeeSnapshotTests
import io.github.ackeecz.snapshots.core.FontScale
import io.github.ackeecz.snapshots.core.SnapshotStrategy
import io.github.ackeecz.snapshots.core.UiTheme

abstract class PaparazziSnapshotTests(
    before: (context: Context) -> Unit,
    fontScales: List<FontScale>,
    showkasePreviews: List<ShowkaseBrowserComponent>,
    uiThemes: List<UiTheme>,
    strategy: SnapshotStrategy,
    theme: @Composable (UiTheme, @Composable () -> Unit) -> Unit
) : AckeeSnapshotTests(
    engine = PaparazziEngine(),
    before = before,
    fontScales = fontScales,
    showkasePreviews = showkasePreviews,
    uiThemes = uiThemes,
    strategy = strategy,
    theme = theme
)
