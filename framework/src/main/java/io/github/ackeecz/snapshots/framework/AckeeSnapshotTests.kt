package io.github.ackeecz.snapshots.framework

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import io.github.ackeecz.snapshots.annotations.ExperimentalSnapshotsApi
import io.github.ackeecz.snapshots.framework.dsl.SnapshotConfigScope
import io.github.ackeecz.snapshots.framework.dsl.SnapshotConfigScopeImpl
import io.kotest.core.spec.style.FunSpec

/**
 * Base Kotest [FunSpec] that generates snapshot tests from a config DSL. Subclass it, pass an
 * [engineFactory], and describe the snapshot matrix in the `config` block ([SnapshotConfigScope]).
 *
 * On construction it builds and resolves the config into the exact set of variants — the Cartesian of
 * the enabled kinds, devices, UI modes and font scales, minus `exclude`d cells and after applying any
 * per-preview overrides — then groups them by (kind, device, UI mode) into Kotest `context`s and
 * registers one `test` per variant, each rendered through the engine.
 *
 * A **fresh [SnapshotEngine] is created via [engineFactory] for each group**, so an engine instance is
 * ever configured for and used by exactly one group. Backends therefore need no cross-group state
 * management: `init` is called exactly once per instance.
 *
 * `PaparazziSnapshotTests` in the `:paparazzi` module is the ready-to-use, Paparazzi-backed subclass.
 */
@ExperimentalSnapshotsApi
abstract class AckeeSnapshotTests(
    engineFactory: () -> SnapshotEngine,
    config: SnapshotConfigScope.() -> Unit,
) : FunSpec({
    val snapshotConfig = SnapshotConfigScopeImpl().apply(config).build()
    val resolved = SnapshotResolver().resolve(snapshotConfig)
    resolved.groupBy { Triple(it.variant.kind, it.variant.device, it.variant.uiMode) }.forEach { (group, snapshots) ->
        val (kind, device, uiMode) = group
        context("${snapshotTargetLabel(device)}_$uiMode") {
            val engine = engineFactory()
            engine.init(kind, device, uiMode, this)
            snapshots.forEach { snapshot ->
                test(snapshot.name) {
                    takeSnapshot(
                        snapshotConfig = snapshotConfig,
                        engine = engine,
                        snapshot = snapshot,
                        uiMode = uiMode,
                    )
                }
            }
        }
    }
})

private fun takeSnapshot(
    snapshotConfig: SnapshotConfig,
    engine: SnapshotEngine,
    snapshot: ResolvedSnapshot,
    uiMode: UiMode,
) {
    // Run the optional per-test setup against the engine's prepared context (readied by the group's
    // context-scoped `beforeEach`). The default no-op is skipped by identity so backends whose context
    // is unavailable or only valid mid-render aren't forced to materialise one when nothing was requested.
    if (snapshotConfig.before !== NO_OP_BEFORE) {
        snapshotConfig.before(engine.context)
    }
    engine.snapshot(snapshot.name) {
        snapshotConfig.decorate(uiMode) {
            CompositionLocalProvider(
                LocalDensity provides Density(
                    density = LocalDensity.current.density,
                    fontScale = snapshot.variant.fontScale.scale,
                ),
            ) {
                snapshot.content()
            }
        }
    }
}
