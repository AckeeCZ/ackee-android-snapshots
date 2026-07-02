package io.github.ackeecz.snapshots.framework

import android.content.Context
import androidx.compose.runtime.Composable

internal val NO_OP_BEFORE: (Context) -> Unit = {}

internal val PASSTHROUGH_DECORATE: @Composable (UiMode, @Composable () -> Unit) -> Unit = { _, content -> content() }
