package io.github.ackeecz.snapshots.framework

/**
 * Engine factory for the wiring test: hands out a fresh [FakeSnapshotEngine] per call and keeps every
 * created engine so the test can assert both per-group instantiation and the aggregated calls.
 */
internal class FakeSnapshotEngineFactory : () -> SnapshotEngine {

    val engines = mutableListOf<FakeSnapshotEngine>()

    override fun invoke(): SnapshotEngine = FakeSnapshotEngine().also { engines += it }

    val allInitCalls: List<Triple<SnapshotKind, DeviceConfig?, UiMode>> get() = engines.flatMap { it.initCalls }

    val allSnapshotNames: List<String> get() = engines.flatMap { it.snapshotNames }
}
