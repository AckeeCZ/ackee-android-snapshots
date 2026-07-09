package io.github.ackeecz.snapshots.framework

import android.content.Context
import androidx.compose.runtime.Composable
import io.kotest.core.spec.style.scopes.FunSpecContainerScope

internal class FakeSnapshotEngine : SnapshotEngine {

    val initCalls = mutableListOf<Triple<SnapshotKind, DeviceConfig?, UiMode>>()
    val snapshotNames = mutableListOf<String>()

    override fun init(kind: SnapshotKind, device: DeviceConfig?, uiMode: UiMode, scope: FunSpecContainerScope) {
        initCalls += Triple(kind, device, uiMode)
    }

    override val context: Context get() = error("engine.context is not exercised by the wiring test")

    override fun snapshot(goldenName: String, content: @Composable () -> Unit) {
        snapshotNames += goldenName
    }
}
