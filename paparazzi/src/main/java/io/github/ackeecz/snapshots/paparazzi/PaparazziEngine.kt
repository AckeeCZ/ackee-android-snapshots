package io.github.ackeecz.snapshots.paparazzi

import android.content.Context
import androidx.compose.runtime.Composable
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.TestName
import io.github.ackeecz.snapshots.framework.DeviceConfig
import io.github.ackeecz.snapshots.framework.SnapshotEngine
import io.github.ackeecz.snapshots.framework.SnapshotKind
import io.github.ackeecz.snapshots.framework.UiMode
import io.kotest.core.spec.style.scopes.FunSpecContainerScope

/**
 * [SnapshotEngine] backed by [Paparazzi]. A fresh instance is created per (kind, device, UI mode)
 * group, so [init] builds this group's single [Paparazzi] once — with its fixed device, night mode and
 * rendering mode — and wires the per-test `setup`/`teardown` lifecycle onto the group's Kotest container
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
            // The golden identity is carried entirely by the Paparazzi TestName:
            //  - packageName/className = derived from the spec FQN, so the golden path stays under the test
            //    class rather than the Kotest context id, and its package/class casing is preserved verbatim.
            //    We split the FQN exactly as Paparazzi's own Description->TestName conversion does (package =
            //    everything before the last '.', class = everything after);
            //  - methodName = this test's variant name (equal to the goldenName passed to `snapshot`),
            //    which Paparazzi keeps case-sensitive (only collapsing whitespace).
            // We deliberately do NOT pass a `name` to `paparazzi.snapshot`, because Paparazzi lower-cases
            // and whitespace-collapses that argument — which would corrupt the variant name (e.g. the
            // device and UI mode) in the golden file name.
            val specName = testCase.spec::class.java.name
            paparazzi.setup(
                TestName(
                    packageName = specName.substringBeforeLast('.', ""),
                    className = specName.substringAfterLast('.'),
                    methodName = testCase.name.name,
                ),
            )
        }
        scope.afterEach { paparazzi.teardown() }
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
