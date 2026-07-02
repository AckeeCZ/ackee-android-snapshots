package io.github.ackeecz.snapshots.framework

import com.airbnb.android.showkase.models.ShowkaseBrowserComponent
import com.airbnb.android.showkase.models.ShowkaseElementsMetadata
import io.github.ackeecz.snapshots.annotations.PreviewSnapshotKind

internal fun metadataOf(vararg components: ShowkaseBrowserComponent) =
    ShowkaseElementsMetadata(componentList = components.toList())

internal fun previewComponent(
    group: String = "Group",
    name: String = "Preview",
    key: String = "$group:$name",
    extraMetadata: List<String> = emptyList(),
) = ShowkaseBrowserComponent(
    componentKey = key,
    group = group,
    componentName = name,
    componentKDoc = "",
    component = {},
    extraMetadata = extraMetadata,
)

internal fun componentTagged(
    group: String = "Group",
    name: String = "Preview",
    key: String = "$group:$name",
    extraTokens: List<String> = emptyList(),
) = previewComponent(group, name, key, listOf(PreviewSnapshotKind.Component) + extraTokens)

internal fun screenTagged(
    group: String = "Group",
    name: String = "Preview",
    key: String = "$group:$name",
    extraTokens: List<String> = emptyList(),
) = previewComponent(group, name, key, listOf(PreviewSnapshotKind.Screen) + extraTokens)

internal fun snapshotConfig(
    previews: List<ShowkaseBrowserComponent>,
    componentsEnabled: Boolean = true,
    devices: List<DeviceConfig> = emptyList(),
    uiModes: List<UiMode> = listOf(UiMode.LIGHT),
    fontScales: List<FontScale> = listOf(FontScale.NORMAL),
    excludes: List<(SnapshotVariant) -> Boolean> = emptyList(),
    profiles: Map<String, ProfileOverride> = emptyMap(),
) = SnapshotConfig(
    previews = previews,
    componentsEnabled = componentsEnabled,
    devices = devices,
    uiModes = uiModes,
    fontScales = fontScales,
    excludes = excludes,
    profiles = profiles,
)
