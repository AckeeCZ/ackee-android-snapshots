package io.github.ackeecz.snapshots.framework

import com.airbnb.android.showkase.models.ShowkaseBrowserComponent
import io.github.ackeecz.snapshots.annotations.ExperimentalSnapshotsApi

/**
 * How the framework treats androidx's `@PreviewWrapper` on the discovered previews.
 * Defaults to [Enabled] without a locator.
 */
@ExperimentalSnapshotsApi
sealed interface PreviewWrappers {

    /** Never resolve or apply wrappers; annotated previews render unwrapped. */
    data object Disabled : PreviewWrappers

    /**
     * Resolve `@PreviewWrapper` — declared directly on the preview function or inherited from a custom preview
     * annotation — and apply it innermost around the preview.
     *
     * [locate] is consulted once per preview before the automatic Showkase-based mapping: a non-null result is used
     * as the preview's declaration site (its annotation is still read there), null falls through to the automatic
     * mapping. Use it for previews the automatic mapping cannot follow.
     */
    class Enabled(val locate: ((ShowkaseBrowserComponent) -> PreviewFunction?)? = null) : PreviewWrappers
}
