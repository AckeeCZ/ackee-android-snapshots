package io.github.ackeecz.snapshots.framework.dsl

/** [DslMarker] for the snapshot config DSL — confines DSL receivers so scopes don't leak into each other. */
@DslMarker
@Retention(AnnotationRetention.BINARY)
annotation class SnapshotDsl
