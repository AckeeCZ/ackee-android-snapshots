package io.github.ackeecz.snapshots.framework.wrapper

import io.github.ackeecz.snapshots.framework.PreviewWrappers

/**
 * Stands in for `SnapshotResolver`'s wrapper-resolver factory: always hands back [resolver] and records the
 * [PreviewWrappers.Enabled] settings it was constructed with, so tests can assert both that the configured
 * instance is passed through and that `Disabled` never reaches the factory at all.
 */
internal class RecordingWrapperResolverFactory(
    private val resolver: PreviewWrapperResolver = FakePreviewWrapperResolver(),
) : (PreviewWrappers.Enabled) -> PreviewWrapperResolver {

    val receivedSettings = mutableListOf<PreviewWrappers.Enabled>()

    override fun invoke(settings: PreviewWrappers.Enabled): PreviewWrapperResolver {
        receivedSettings += settings
        return resolver
    }
}
