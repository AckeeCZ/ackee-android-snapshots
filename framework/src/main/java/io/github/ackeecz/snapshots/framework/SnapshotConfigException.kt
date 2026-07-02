package io.github.ackeecz.snapshots.framework

/**
 * Thrown when the snapshot configuration or a preview's tags are invalid — for example a preview with
 * no snapshot-kind tag or more than one, an unknown or malformed override token, a `variants { }` axis
 * left empty, or a preview that resolves to zero snapshot variants.
 */
class SnapshotConfigException(message: String) : RuntimeException(message)
