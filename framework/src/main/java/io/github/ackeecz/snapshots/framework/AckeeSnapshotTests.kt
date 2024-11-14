package io.github.ackeecz.snapshots.framework

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.airbnb.android.showkase.models.ShowkaseBrowserComponent
import io.kotest.core.spec.style.FunSpec

abstract class AckeeSnapshotTests(
    engine: SnapshotEngine,
    before: (context: Context) -> Unit,
    fontScales: List<FontScale>,
    showkasePreviews: List<ShowkaseBrowserComponent>,
    uiThemes: List<UiTheme>,
    strategy: SnapshotStrategy,
    theme: @Composable (UiTheme, @Composable () -> Unit) -> Unit
) : FunSpec({
    engine.init(strategy, this)

    beforeTest {
        before(engine.context)
    }

    preparePreviews(showkasePreviews).forEach { componentPreview ->
        if (uiThemes.size > 1) {
            uiThemes.forEach { uiTheme ->
                test("${componentPreview}_${strategy.name}_theme=${uiTheme.name}") {
                    takeSnapshot(
                        engine = engine,
                        theme = theme,
                        componentPreview = componentPreview,
                        uiTheme = uiTheme
                    )
                }
            }
        }
        fontScales.forEach { fontScale ->
            test("${componentPreview}_${strategy.name}_fs=$fontScale") {
                takeSnapshot(
                    engine = engine,
                    theme = theme,
                    componentPreview = componentPreview,
                    fontScale = fontScale
                )
            }
        }
    }
})

private fun takeSnapshot(
    engine: SnapshotEngine,
    theme: @Composable (UiTheme, @Composable () -> Unit) -> Unit,
    componentPreview: ComponentPreview,
    fontScale: FontScale = FontScale.NORMAL,
    uiTheme: UiTheme = UiTheme.LIGHT,
) {
    engine.snapshot {
        CompositionLocalProvider(
            LocalConfiguration provides LocalConfiguration.current.apply {
                uiMode = when (uiTheme) {
                    UiTheme.LIGHT -> Configuration.UI_MODE_NIGHT_NO
                    UiTheme.DARK -> Configuration.UI_MODE_NIGHT_YES
                }
            }
        ) {
            theme(uiTheme) {
                val currentDensity = LocalDensity.current
                CompositionLocalProvider(
                    LocalDensity provides Density(
                        density = currentDensity.density,
                        fontScale = fontScale.scale
                    )
                ) {
                    componentPreview.content()
                }
            }
        }
    }
}

private fun preparePreviews(showkaseComponents: List<ShowkaseBrowserComponent>): List<ComponentPreview> {
    val groupedComponents = showkaseComponents.groupBy { "${it.group}_${it.componentName}" }
    return groupedComponents.flatMap { (key, components) ->
        components.mapIndexed { index, showkaseBrowserComponent ->
            val id = if (components.size == 1) key else "${key}_$index"
            ComponentPreview(id, showkaseBrowserComponent)
        }
    }.sortedBy { it.toString() }
}
