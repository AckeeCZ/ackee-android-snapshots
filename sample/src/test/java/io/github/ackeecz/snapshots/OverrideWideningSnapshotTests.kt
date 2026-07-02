package io.github.ackeecz.snapshots

import io.github.ackeecz.snapshots.framework.Device
import io.github.ackeecz.snapshots.framework.FontScale
import io.github.ackeecz.snapshots.framework.UiMode
import io.github.ackeecz.snapshots.paparazzi.PaparazziSnapshotTests
import io.github.ackeecz.snapshots.ui.PreviewGroup
import io.github.ackeecz.snapshots.ui.PreviewProfile

/**
 * Overrides don't only filter — they can also **widen** an axis beyond what the class declares. The
 * class here is deliberately minimal (a single UI mode, font scale and device); each preview then
 * *adds* values the class never listed, at both levels:
 *  - profile level (`ProfileFontScale` / `ProfileUiMode` / `ProfileDevice`), and
 *  - preview level via repeated inline tokens (`InlineFontScale` / `InlineUiMode` / `InlineDevice`).
 *
 * Every preview therefore produces 2 goldens where the bare class matrix would produce 1.
 */
class OverrideWideningSnapshotTests : PaparazziSnapshotTests({
    previews(previewsInGroup(PreviewGroup.OverrideWidening))
    decorate(sampleDecorate)
    variants {
        components()
        screens(Device.Pixel6.portrait)
        uiModes(UiMode.LIGHT)
        fontScales(FontScale.NORMAL)
        profile(PreviewProfile.WidenFontScale) {
            fontScales(FontScale.NORMAL, FontScale.LARGE)
        }
        profile(PreviewProfile.WidenUiMode) {
            uiModes(UiMode.LIGHT, UiMode.DARK)
        }
        profile(PreviewProfile.WidenDevice) {
            devices(Device.Pixel6.portrait, Device.Nexus10.landscape)
        }
    }
})
