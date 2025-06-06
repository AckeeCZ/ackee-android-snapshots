package io.github.ackeecz.snapshots.framework

import android.content.Context
import androidx.compose.runtime.Composable
import io.kotest.core.spec.style.FunSpec

interface SnapshotEngine {

    fun init(strategy: SnapshotStrategy, uiTheme: UiTheme, funSpec: FunSpec)
    val context: Context
    fun snapshot(content: @Composable () -> Unit)
}
