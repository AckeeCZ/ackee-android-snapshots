package io.github.ackeecz.snapshots.framework

import androidx.compose.runtime.Composable
import io.github.ackeecz.snapshots.framework.wrapper.PreviewWrapperFactory

internal data class ResolvedSnapshot(
    val name: String,
    val variant: SnapshotVariant,
    val content: @Composable () -> Unit,
    val wrapper: PreviewWrapperFactory? = null,
)
