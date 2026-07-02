package io.github.ackeecz.snapshots.annotations

/**
 * Per-preview inline UI-mode override tokens. Tagging a preview with one (or more) of these
 * restricts (or widens) the UI modes it is snapshot in, overriding the profile and class config
 * for that preview only.
 */
object PreviewUiMode {

    const val Light = "uiMode=LIGHT"
    const val Dark = "uiMode=DARK"
}
