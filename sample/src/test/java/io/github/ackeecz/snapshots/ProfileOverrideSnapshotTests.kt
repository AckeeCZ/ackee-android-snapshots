package io.github.ackeecz.snapshots

import io.github.ackeecz.snapshots.framework.Device
import io.github.ackeecz.snapshots.framework.FontScale
import io.github.ackeecz.snapshots.framework.UiMode
import io.github.ackeecz.snapshots.paparazzi.PaparazziSnapshotTests
import io.github.ackeecz.snapshots.ui.PreviewGroup
import io.github.ackeecz.snapshots.ui.PreviewProfile

/**
 * Profile-level narrowing of every axis, across both a component and screens. The class offers the full
 * matrix; each preview references a profile that pins one axis to a single value for itself only:
 *  - `FontScaleComponent` / `FontScaleScreen` → font scale narrowed to NORMAL (no LARGE golden);
 *  - `UiModeComponent` → UI mode narrowed to LIGHT (no DARK golden);
 *  - `DeviceScreen` → device narrowed to Pixel 6 portrait (no Nexus 10 golden).
 */
class ProfileOverrideSnapshotTests : PaparazziSnapshotTests({
    previews(previewsInGroup(PreviewGroup.ProfileOverride))
    decorate(sampleDecorate)
    variants {
        components()
        screens(Device.Pixel6.portrait, Device.Nexus10.landscape)
        uiModes(UiMode.LIGHT, UiMode.DARK)
        fontScales(FontScale.NORMAL, FontScale.LARGE)
        profile(PreviewProfile.SingleFontScale) {
            fontScales(FontScale.NORMAL)
        }
        profile(PreviewProfile.SingleUiMode) {
            uiModes(UiMode.LIGHT)
        }
        profile(PreviewProfile.SingleDevice) {
            devices(Device.Pixel6.portrait)
        }
    }
})
