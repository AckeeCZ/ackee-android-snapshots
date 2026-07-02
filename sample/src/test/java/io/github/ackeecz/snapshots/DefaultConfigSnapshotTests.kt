package io.github.ackeecz.snapshots

import io.github.ackeecz.snapshots.framework.Device
import io.github.ackeecz.snapshots.framework.FontScale
import io.github.ackeecz.snapshots.framework.UiMode
import io.github.ackeecz.snapshots.paparazzi.PaparazziSnapshotTests
import io.github.ackeecz.snapshots.ui.PreviewGroup

/**
 * Baseline concern: both strategies (components + screens) over the full (uiMode × fontScale) Cartesian,
 * with no overrides. Every `Default`-group preview is snapshot in LIGHT and DARK, at NORMAL and LARGE.
 */
class DefaultConfigSnapshotTests : PaparazziSnapshotTests({
    previews(previewsInGroup(PreviewGroup.Default))
    decorate(sampleDecorate)
    variants {
        components()
        screens(Device.Pixel6.portrait)
        uiModes(UiMode.LIGHT, UiMode.DARK)
        fontScales(FontScale.NORMAL, FontScale.LARGE)
    }
})
