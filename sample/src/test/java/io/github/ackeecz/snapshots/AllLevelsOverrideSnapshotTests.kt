package io.github.ackeecz.snapshots

import io.github.ackeecz.snapshots.framework.Device
import io.github.ackeecz.snapshots.framework.FontScale
import io.github.ackeecz.snapshots.framework.UiMode
import io.github.ackeecz.snapshots.paparazzi.PaparazziSnapshotTests
import io.github.ackeecz.snapshots.ui.PreviewGroup
import io.github.ackeecz.snapshots.ui.PreviewProfile

/**
 * All three resolution levels at once. The class sets every axis fully (2 devices × 2 UI modes × 2 font
 * scales); a profile narrows font scale to NORMAL; the `AllLevelsScreen` preview additionally carries an
 * inline `device=PIXEL_6_PORTRAIT` token. Resolving `inline ?: profile ?: class` per axis gives
 * 1 device × **2 UI modes (untouched, from the class)** × 1 font scale = **2 goldens**.
 */
class AllLevelsOverrideSnapshotTests : PaparazziSnapshotTests({
    previews(previewsInGroup(PreviewGroup.AllLevels))
    decorate(sampleDecorate)
    variants {
        screens(Device.Pixel6.portrait, Device.Nexus10.landscape)
        uiModes(UiMode.LIGHT, UiMode.DARK)
        fontScales(FontScale.NORMAL, FontScale.LARGE)
        profile(PreviewProfile.SingleFontScale) {
            fontScales(FontScale.NORMAL)
        }
    }
})
