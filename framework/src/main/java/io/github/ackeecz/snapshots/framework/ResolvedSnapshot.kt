package io.github.ackeecz.snapshots.framework

import androidx.compose.runtime.Composable

internal data class ResolvedSnapshot(
    val name: String,
    val variant: SnapshotVariant,
    val content: @Composable () -> Unit,
)
