package io.github.ackeecz.snapshots

import io.github.ackeecz.snapshots.framework.FontScale
import io.github.ackeecz.snapshots.framework.UiMode
import io.github.ackeecz.snapshots.paparazzi.PaparazziSnapshotTests
import io.github.ackeecz.snapshots.ui.PreviewGroup

/**
 * androidx `@PreviewWrapper` support: the framework resolves each preview's wrapper and applies it
 * innermost, inside `decorate`. `FrameWrapped` declares its wrapper directly, `BadgeWrapped` is an
 * `internal` preview with a second wrapper, `MultipreviewWrapped` inherits one from a custom preview
 * annotation and `UnwrappedControl` has none. Because the wrappers paint with `MaterialTheme` colours,
 * the DARK goldens also prove `decorate` sits outside the wrapper. `ObjectNested` and `Parameterized`
 * are LIGHT-only shape controls proving the Showkase mapper follows real codegen for an `object`-nested
 * and a `@PreviewParameter` preview.
 */
class PreviewWrapperSnapshotTests : PaparazziSnapshotTests({
    previews(previewsInGroup(PreviewGroup.PreviewWrapper))
    decorate(sampleDecorate)
    variants {
        components()
        uiModes(UiMode.LIGHT, UiMode.DARK)
        fontScales(FontScale.NORMAL)
    }
})
