package io.github.ackeecz.snapshots

import io.github.ackeecz.snapshots.framework.Device
import io.github.ackeecz.snapshots.framework.FontScale
import io.github.ackeecz.snapshots.framework.UiMode
import io.github.ackeecz.snapshots.paparazzi.PaparazziSnapshotTests
import io.github.ackeecz.snapshots.ui.PreviewGroup

/**
 * Preview-level (inline `extraMetadata` token) narrowing of every axis, across both a component and
 * screens. The class offers the full matrix; each preview carries an inline token pinning one axis:
 *  - `UiModeComponent` → inline `uiMode=LIGHT` (no DARK golden);
 *  - `FontScaleComponent` / `FontScaleScreen` → inline `fontScale=NORMAL` (no LARGE golden);
 *  - `DeviceScreen` → inline `device=PIXEL_6_PORTRAIT` (no Nexus 10 golden).
 */
class InlineOverrideSnapshotTests : PaparazziSnapshotTests({
    previews(previewsInGroup(PreviewGroup.InlineOverride))
    decorate(sampleDecorate)
    variants {
        components()
        screens(Device.Pixel6.portrait, Device.Nexus10.landscape)
        uiModes(UiMode.LIGHT, UiMode.DARK)
        fontScales(FontScale.NORMAL, FontScale.LARGE)
    }
})
