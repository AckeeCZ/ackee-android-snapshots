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
 * [SnapshotEngine] backed by [Paparazzi]. A fresh instance is created per (kind, device, UI mode)
 * group, so [init] builds this group's single [Paparazzi] once — with its fixed device, night mode and
 * rendering mode — and wires the standard per-test `apply` lifecycle onto the group's Kotest container
 * scope. No cross-group state to guard: this engine serves exactly one group.
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
        scope.beforeEach { testCase ->
            // The golden identity is carried entirely by the JUnit Description:
            //  - className = the spec FQN, so the golden path stays under the test class rather than the
            //    Kotest context id, and its package/class casing is preserved verbatim;
            //  - methodName = this test's variant name (equal to the goldenName passed to `snapshot`),
            //    which Paparazzi keeps case-sensitive (only collapsing whitespace).
            // We deliberately do NOT pass a `name` to `paparazzi.snapshot`, because Paparazzi lower-cases
            // and whitespace-collapses that argument — which would corrupt the variant name (e.g. the
            // device and UI mode) in the golden file name.
            val description = Description.createTestDescription(testCase.spec::class.java.name, testCase.name.name)
            paparazzi.apply(base = NoopStatement, description = description).evaluate()
            paparazzi.prepare(description)
        }
        scope.afterEach { paparazzi.close() }
    }

    override fun snapshot(goldenName: String, content: @Composable () -> Unit) {
        // `goldenName` already identifies this test through the Description built in [init] (it equals the
        // Kotest test name); see the note there for why it is not passed to Paparazzi as a `name`.
        paparazzi.snapshot { content() }
    }

    private companion object {

        private const val DEFAULT_THEME = "android:Theme.Material.Light.NoActionBar"
    }
}

private object NoopStatement : Statement() {

    override fun evaluate() = Unit
}
