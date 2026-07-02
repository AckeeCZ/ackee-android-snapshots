package io.github.ackeecz.snapshots.ui

/**
 * Showkase groups used to scope the sample previews to a single snapshot-test concern. Each per-concern
 * test class filters `Showkase.getMetadata()` down to one of these groups, so its golden set stays
 * focused and a regression points at a specific framework feature.
 */
object PreviewGroup {

    const val Default = "Default"
    const val ProfileOverride = "ProfileOverride"
    const val InlineOverride = "InlineOverride"
    const val OverrideWidening = "OverrideWidening"
    const val Precedence = "Precedence"
    const val AllLevels = "AllLevels"
}

/**
 * Profile keys referenced by previews (via `@ShowkaseComposable` `extraMetadata`) and defined by the
 * snapshot config DSL (`variants { profile(key) { … } }`). Kept as constants so a preview's tag and the
 * profile key it resolves against can never drift apart. Narrowing profiles pin an axis to a single
 * value; widening profiles push an axis beyond what the class declares.
 */
object PreviewProfile {

    const val SingleFontScale = "singleFontScale"
    const val SingleUiMode = "singleUiMode"
    const val SingleDevice = "singleDevice"

    const val WidenFontScale = "widenFontScale"
    const val WidenUiMode = "widenUiMode"
    const val WidenDevice = "widenDevice"
}
