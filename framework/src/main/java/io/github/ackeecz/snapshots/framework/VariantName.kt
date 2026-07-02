package io.github.ackeecz.snapshots.framework

/** The label identifying a variant group in the Kotest test tree: its device for screens, or "component". */
internal fun snapshotTargetLabel(device: DeviceConfig?): String = device?.name ?: "component"

/**
 * The variant name — the golden identity of a snapshot. Every axis is encoded, and each segment is
 * itself free of the `_` used to join segments, so the whole name reads as clean, `_`-delimited parts
 * that mirror the source you'd write (with `-` standing in for the `.` of source, since a `.` would be
 * rewritten to `_` by the Kotest test name → Paparazzi golden-file pipeline):
 *  - the preview `id` (`group_componentName`), verbatim;
 *  - for screens, the device as its `Device.Pixel6.portrait` shortcut (`Pixel6-portrait`); components,
 *    being device-independent, omit this segment entirely;
 *  - the UI mode as its bare enum value (`LIGHT` / `DARK`), unambiguous on its own;
 *  - the font scale as `FontScale-<value>` — the type is kept because a bare `NORMAL` / `SMALL` / `LARGE`
 *    wouldn't otherwise read as a font scale.
 */
internal fun variantName(id: String, variant: SnapshotVariant): String = buildString {
    append(id)
    variant.device?.let { append('_').append(it.name) }
    append('_').append(variant.uiMode.name)
    append("_FontScale-").append(variant.fontScale.name)
}
