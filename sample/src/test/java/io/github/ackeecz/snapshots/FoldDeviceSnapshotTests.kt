package io.github.ackeecz.snapshots

import io.github.ackeecz.snapshots.framework.Device
import io.github.ackeecz.snapshots.framework.FontScale
import io.github.ackeecz.snapshots.framework.UiMode
import io.github.ackeecz.snapshots.paparazzi.PaparazziSnapshotTests
import io.github.ackeecz.snapshots.ui.PreviewGroup

/**
 * Device concern: the `Default` group's two screens rendered on the Pixel 9 Pro Fold's unfolded inner
 * display, in both orientations — the end-to-end proof that the fold device reaches Paparazzi and
 * renders, which the pure mapping unit test cannot show.
 *
 * The fold is the interesting device to cover because it is nearly square (852x883dp): it is wider than
 * Material's Expanded breakpoint yet far narrower than a tablet in landscape, and both orientations land
 * on the same side of most width breakpoints.
 *
 * One UI mode and one font scale keep this at **2 screens × 2 orientations + 1 component = 5 goldens**;
 * the remaining axes are already covered by [DefaultConfigSnapshotTests]. `components()` is enabled
 * because the `Default` group also holds a Component-tagged preview, and a preview that resolves to zero
 * variants is a config error — the component cell rides along device-independently.
 */
class FoldDeviceSnapshotTests : PaparazziSnapshotTests({
    previews(previewsInGroup(PreviewGroup.Default))
    decorate(sampleDecorate)
    variants {
        components()
        screens(Device.Pixel9ProFold.portrait, Device.Pixel9ProFold.landscape)
        uiModes(UiMode.LIGHT)
        fontScales(FontScale.NORMAL)
    }
})
