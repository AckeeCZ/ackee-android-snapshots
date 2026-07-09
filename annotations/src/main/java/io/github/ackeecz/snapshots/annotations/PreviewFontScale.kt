package io.github.ackeecz.snapshots.annotations

/**
 * Per-preview inline font-scale override tokens. Tagging a preview with one (or more) of these
 * restricts (or widens) the font scales it is snapshot at, overriding the profile and class config
 * for that preview only.
 */
object PreviewFontScale {

    const val Small = "fontScale=SMALL"
    const val Normal = "fontScale=NORMAL"
    const val Large = "fontScale=LARGE"
}
