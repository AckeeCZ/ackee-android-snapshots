package io.github.ackeecz.snapshots.framework.dsl

import android.content.Context
import androidx.compose.runtime.Composable
import com.airbnb.android.showkase.models.ShowkaseElementsMetadata
import io.github.ackeecz.snapshots.annotations.ExperimentalSnapshotsApi
import io.github.ackeecz.snapshots.framework.PreviewWrappers
import io.github.ackeecz.snapshots.framework.UiMode

/**
 * Top-level receiver of the snapshot config DSL (see `AckeeSnapshotTests`). Declares the previews to
 * snapshot, optional per-test setup and decoration, and the variant matrix.
 */
@ExperimentalSnapshotsApi
@SnapshotDsl
interface SnapshotConfigScope {

    /**
     * The Showkase-discovered previews to snapshot; pre-filter [metadata] to scope by module or any other requirement.
     * Required.
     */
    fun previews(metadata: ShowkaseElementsMetadata)

    /** Optional setup run before each snapshot, given the prepared rendering [Context]. Defaults to a no-op. */
    fun before(block: (Context) -> Unit)

    /**
     * Wrapper applied around every rendered preview — typically your app theme, keyed off the given
     * [UiMode]. Required: omitting it fails config building, since a UI mode is always configured and
     * would otherwise be ignored.
     */
    fun decorate(block: @Composable (UiMode, @Composable () -> Unit) -> Unit)

    /**
     * Optional. Configures how androidx's `@PreviewWrapper` is handled on the discovered previews; defaults to
     * [PreviewWrappers.Enabled] without a locator. The last call wins.
     */
    fun previewWrappers(wrappers: PreviewWrappers)

    /** Declares the variant matrix — kinds, devices, UI modes, font scales, excludes and profiles. Required. */
    fun variants(block: VariantsScope.() -> Unit)
}
