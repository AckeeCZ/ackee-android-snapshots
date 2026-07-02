package io.github.ackeecz.snapshots.framework

/** The name segment identifying what a variant renders: its device for screens, or "component". */
internal fun snapshotTargetLabel(device: DeviceConfig?): String = device?.name ?: "component"

internal fun variantName(id: String, variant: SnapshotVariant): String =
    "${id}_${snapshotTargetLabel(variant.device)}_${variant.uiMode}_fs=${variant.fontScale}"
