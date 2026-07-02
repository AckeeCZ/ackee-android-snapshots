package io.github.ackeecz.snapshots

import io.github.ackeecz.snapshots.framework.Device
import io.github.ackeecz.snapshots.framework.FontScale
import io.github.ackeecz.snapshots.framework.UiMode
import io.github.ackeecz.snapshots.paparazzi.PaparazziSnapshotTests
import io.github.ackeecz.snapshots.ui.PreviewGroup

/**
 * Exclude concern: the same baseline matrix as [DefaultConfigSnapshotTests], minus the coupled
 * (DARK × LARGE) cell. No `*_DARK_fs=LARGE` golden should exist for any preview.
 */
class ExcludeCouplingSnapshotTests : PaparazziSnapshotTests({
    previews(previewsInGroup(PreviewGroup.Default))
    decorate(sampleDecorate)
    variants {
        components()
        screens(Device.Pixel6.portrait)
        uiModes(UiMode.LIGHT, UiMode.DARK)
        fontScales(FontScale.NORMAL, FontScale.LARGE)
        exclude { it.uiMode == UiMode.DARK && it.fontScale == FontScale.LARGE }
    }
})
