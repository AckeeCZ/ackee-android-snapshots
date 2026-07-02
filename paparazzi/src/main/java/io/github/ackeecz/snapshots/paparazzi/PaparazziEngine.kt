package io.github.ackeecz.snapshots.paparazzi

import android.content.Context
import androidx.compose.runtime.Composable
import app.cash.paparazzi.Paparazzi
import io.github.ackeecz.snapshots.framework.DeviceConfig
import io.github.ackeecz.snapshots.framework.SnapshotEngine
import io.github.ackeecz.snapshots.framework.SnapshotKind
import io.github.ackeecz.snapshots.framework.UiMode
import io.kotest.core.spec.style.scopes.FunSpecContainerScope
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * [SnapshotEngine] backed by [Paparazzi]. One instance is driven per (kind, device, UI mode) group:
 * [init] builds a single [Paparazzi] for the group with its fixed device, night mode and rendering
 * mode, and wires the standard per-test `apply` lifecycle onto the group's Kotest container scope.
 */
class PaparazziEngine : SnapshotEngine {

    private lateinit var paparazzi: Paparazzi

    override val context: Context
        get() = paparazzi.context

    override fun init(kind: SnapshotKind, device: DeviceConfig?, uiMode: UiMode, scope: FunSpecContainerScope) {
        val renderConfig = renderConfigFor(kind, device)
        paparazzi = Paparazzi(
            deviceConfig = renderConfig.deviceConfig.copy(nightMode = mapToNightMode(uiMode)),
            renderingMode = renderConfig.renderingMode,
            theme = DEFAULT_THEME,
        )
        val groupLabel = "${device?.name ?: "component"}_$uiMode"
        scope.beforeEach { testCase ->
            val description = Description.createTestDescription(testCase.spec::class.java.name, groupLabel)
            paparazzi.apply(base = NoopStatement, description = description).evaluate()
            paparazzi.prepare(description)
        }
        scope.afterEach { paparazzi.close() }
    }

    override fun snapshot(goldenName: String, content: @Composable () -> Unit) {
        paparazzi.snapshot(name = goldenName) { content() }
    }

    private companion object {

        const val DEFAULT_THEME = "android:Theme.Material.Light.NoActionBar"
    }
}

private object NoopStatement : Statement() {

    override fun evaluate() = Unit
}
