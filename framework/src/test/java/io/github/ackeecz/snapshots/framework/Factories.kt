package io.github.ackeecz.snapshots.framework

import com.airbnb.android.showkase.models.ShowkaseBrowserComponent
import com.airbnb.android.showkase.models.ShowkaseElementsMetadata
import io.github.ackeecz.snapshots.annotations.PreviewSnapshotKind
import io.github.ackeecz.snapshots.framework.wrapper.FakePreviewWrapperResolver
import io.github.ackeecz.snapshots.framework.wrapper.RecordingWrapperResolverFactory
import io.github.ackeecz.snapshots.framework.wrapper.WrapperResolution

internal fun metadataOf(vararg components: ShowkaseBrowserComponent) =
    ShowkaseElementsMetadata(componentList = components.toList())

internal fun previewComponent(
    group: String = "Group",
    name: String = "Preview",
    key: String = "$group:$name",
    extraMetadata: List<String> = emptyList(),
    styleName: String? = null,
) = ShowkaseBrowserComponent(
    componentKey = key,
    group = group,
    componentName = name,
    componentKDoc = "",
    component = {},
    styleName = styleName,
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

// Mirrors the SnapshotConfig data class (exempt from the rule via ignoreDataClasses); the factory
// only adds test-friendly defaults, so the parameter count matches by design.
@Suppress("LongParameterList")
internal fun snapshotConfig(
    previews: List<ShowkaseBrowserComponent>,
    componentsEnabled: Boolean = true,
    devices: List<DeviceConfig> = emptyList(),
    uiModes: List<UiMode> = listOf(UiMode.LIGHT),
    fontScales: List<FontScale> = listOf(FontScale.NORMAL),
    excludes: List<(SnapshotVariant) -> Boolean> = emptyList(),
    profiles: Map<String, ProfileOverride> = emptyMap(),
    previewWrappers: PreviewWrappers = PreviewWrappers.Disabled,
) = SnapshotConfig(
    previews = previews,
    componentsEnabled = componentsEnabled,
    devices = devices,
    uiModes = uiModes,
    fontScales = fontScales,
    excludes = excludes,
    profiles = profiles,
    previewWrappers = previewWrappers,
)

/** A [RecordingWrapperResolverFactory] whose resolver serves the given `componentKey` -> [WrapperResolution] map. */
internal fun resolutionsOf(vararg resolutions: Pair<String, WrapperResolution>) =
    RecordingWrapperResolverFactory(FakePreviewWrapperResolver(mapOf(*resolutions)))
