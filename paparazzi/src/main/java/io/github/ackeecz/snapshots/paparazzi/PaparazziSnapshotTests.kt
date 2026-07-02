package io.github.ackeecz.snapshots.paparazzi

import io.github.ackeecz.snapshots.framework.AckeeSnapshotTests
import io.github.ackeecz.snapshots.framework.dsl.SnapshotConfigScope

/**
 * Ready-to-use, Paparazzi-backed [AckeeSnapshotTests]. Subclass it and describe the snapshot matrix in
 * the `config` block; snapshots are rendered on the JVM with [PaparazziEngine].
 */
abstract class PaparazziSnapshotTests(
    config: SnapshotConfigScope.() -> Unit,
) : AckeeSnapshotTests(
    engine = PaparazziEngine(),
    config = config,
)
