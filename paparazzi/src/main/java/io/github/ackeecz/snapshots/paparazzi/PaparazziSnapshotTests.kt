package io.github.ackeecz.snapshots.paparazzi

import android.content.Context
import androidx.compose.runtime.Composable
import com.airbnb.android.showkase.models.ShowkaseBrowserComponent
import io.github.ackeecz.snapshots.framework.AckeeSnapshotTests
import io.github.ackeecz.snapshots.framework.FontScale
import io.github.ackeecz.snapshots.framework.SnapshotStrategy
import io.github.ackeecz.snapshots.framework.UiTheme

abstract class PaparazziSnapshotTests(
    before: (context: Context) -> Unit,
    fontScales: List<FontScale>,
    showkasePreviews: List<ShowkaseBrowserComponent>,
    uiTheme: UiTheme,
    strategy: SnapshotStrategy,
    theme: @Composable (UiTheme, @Composable () -> Unit) -> Unit
) : AckeeSnapshotTests(
    engine = PaparazziEngine(),
    before = before,
    fontScales = fontScales,
    showkasePreviews = showkasePreviews,
    uiTheme = uiTheme,
    strategy = strategy,
    theme = theme
)
