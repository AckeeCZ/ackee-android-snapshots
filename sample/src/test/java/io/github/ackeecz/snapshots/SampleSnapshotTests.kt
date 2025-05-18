package io.github.ackeecz.snapshots

import android.content.Context
import androidx.compose.runtime.Composable
import com.airbnb.android.showkase.models.Showkase
import io.github.ackeecz.snapshots.annotations.PreviewSnapshotStrategy
import io.github.ackeecz.snapshots.framework.Device
import io.github.ackeecz.snapshots.framework.DeviceConfig
import io.github.ackeecz.snapshots.framework.DeviceOrientation
import io.github.ackeecz.snapshots.framework.FontScale
import io.github.ackeecz.snapshots.framework.SnapshotStrategy
import io.github.ackeecz.snapshots.framework.UiTheme
import io.github.ackeecz.snapshots.paparazzi.PaparazziSnapshotTests
import io.github.ackeecz.snapshots.ui.theme.SnapshotsSampleTheme

val fontScales = listOf(FontScale.NORMAL, FontScale.LARGE)

val before = { _: Context ->
    // nothing
}

val theme: @Composable (UiTheme, @Composable () -> Unit) -> Unit = { uiTheme, content ->
    SnapshotsSampleTheme(
        darkTheme = uiTheme == UiTheme.DARK,
        dynamicColor = false
    ) {
        content()
    }
}

val screenPreviews = Showkase.getMetadata().componentList.filter {
    !it.extraMetadata.contains(PreviewSnapshotStrategy.Component)
}

val componentPreviews = Showkase.getMetadata().componentList.filter {
    it.extraMetadata.contains(PreviewSnapshotStrategy.Component)
}

class ComponentsSnapshotTests : PaparazziSnapshotTests(
    before = before,
    fontScales = fontScales,
    showkasePreviews = componentPreviews,
    theme = theme,
    uiTheme = UiTheme.LIGHT,
    strategy = SnapshotStrategy.Component
)

class DarkComponentsSnapshotTests : PaparazziSnapshotTests(
    before = before,
    fontScales = emptyList(),
    showkasePreviews = componentPreviews,
    theme = theme,
    uiTheme = UiTheme.DARK,
    strategy = SnapshotStrategy.Component
)

class PortraitPhoneSnapshotTests : PaparazziSnapshotTests(
    before = before,
    fontScales = fontScales,
    showkasePreviews = screenPreviews,
    theme = theme,
    uiTheme = UiTheme.LIGHT,
    strategy = SnapshotStrategy.Screen(
        DeviceConfig(
            device = Device.PIXEL_6,
            orientation = DeviceOrientation.PORTRAIT
        )
    ),
)

class DarkPortraitPhoneSnapshotTests : PaparazziSnapshotTests(
    before = before,
    fontScales = emptyList(),
    showkasePreviews = screenPreviews,
    theme = theme,
    uiTheme = UiTheme.DARK,
    strategy = SnapshotStrategy.Screen(
        DeviceConfig(
            device = Device.PIXEL_6,
            orientation = DeviceOrientation.PORTRAIT
        )
    ),
)

class LandscapePhoneSnapshotTests : PaparazziSnapshotTests(
    before = before,
    fontScales = fontScales,
    showkasePreviews = screenPreviews,
    theme = theme,
    uiTheme = UiTheme.LIGHT,
    strategy = SnapshotStrategy.Screen(
        DeviceConfig(
            device = Device.PIXEL_6,
            orientation = DeviceOrientation.LANDSCAPE
        )
    ),
)

class DarkLandscapePhoneSnapshotTests : PaparazziSnapshotTests(
    before = before,
    fontScales = emptyList(),
    showkasePreviews = screenPreviews,
    theme = theme,
    uiTheme = UiTheme.DARK,
    strategy = SnapshotStrategy.Screen(
        DeviceConfig(
            device = Device.PIXEL_6,
            orientation = DeviceOrientation.LANDSCAPE
        )
    ),
)

class TabletSnapshotTests : PaparazziSnapshotTests(
    before = before,
    fontScales = fontScales,
    showkasePreviews = screenPreviews,
    theme = theme,
    uiTheme = UiTheme.LIGHT,
    strategy = SnapshotStrategy.Screen(
        DeviceConfig(
            device = Device.NEXUS_10,
            orientation = DeviceOrientation.LANDSCAPE
        )
    ),
)

class DarkTabletSnapshotTests : PaparazziSnapshotTests(
    before = before,
    fontScales = emptyList(),
    showkasePreviews = screenPreviews,
    theme = theme,
    uiTheme = UiTheme.DARK,
    strategy = SnapshotStrategy.Screen(
        DeviceConfig(
            device = Device.NEXUS_10,
            orientation = DeviceOrientation.LANDSCAPE
        )
    ),
)
