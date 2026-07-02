package io.github.ackeecz.snapshots

import io.github.ackeecz.snapshots.framework.FontScale
import io.github.ackeecz.snapshots.framework.UiMode
import io.github.ackeecz.snapshots.paparazzi.PaparazziSnapshotTests
import io.github.ackeecz.snapshots.ui.PreviewGroup
import io.github.ackeecz.snapshots.ui.PreviewProfile

/**
 * Layering precedence on a single axis: `inline ?: profile ?: class`. The `InlineBeatsProfile` preview
 * references a profile that narrows font scale to NORMAL **and** carries an inline `fontScale=LARGE`
 * token — inline must win, so it is snapshot only at LARGE. Its single golden is the direct proof.
 */
class OverridePrecedenceSnapshotTests : PaparazziSnapshotTests({
    previews(previewsInGroup(PreviewGroup.Precedence))
    decorate(sampleDecorate)
    variants {
        components()
        uiModes(UiMode.LIGHT)
        fontScales(FontScale.NORMAL, FontScale.LARGE)
        profile(PreviewProfile.SingleFontScale) {
            fontScales(FontScale.NORMAL)
        }
    }
})
