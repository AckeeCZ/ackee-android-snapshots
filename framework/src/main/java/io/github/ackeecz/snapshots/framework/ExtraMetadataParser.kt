package io.github.ackeecz.snapshots.framework

import io.github.ackeecz.snapshots.annotations.PreviewDevice
import io.github.ackeecz.snapshots.annotations.PreviewFontScale
import io.github.ackeecz.snapshots.annotations.PreviewSnapshotKind
import io.github.ackeecz.snapshots.annotations.PreviewUiMode

internal enum class KindTag {

    COMPONENT,
    SCREEN
}

internal data class ParsedMetadata(
    val kind: KindTag?,
    val inlineFontScales: Set<FontScale>?,
    val inlineUiModes: Set<UiMode>?,
    val inlineDevices: Set<DeviceConfig>?,
    val profileKey: String?,
)

internal class ExtraMetadataParser {

    // Each token constant maps to its framework value explicitly. The token strings are opaque:
    // they never have to equal an enum name, and renaming an enum value breaks compilation here
    // (rather than silently at parse time). The token prefix only identifies the axis family.
    private val fontScaleTokens: Map<String, FontScale> = mapOf(
        PreviewFontScale.Small to FontScale.SMALL,
        PreviewFontScale.Normal to FontScale.NORMAL,
        PreviewFontScale.Large to FontScale.LARGE,
    )

    private val uiModeTokens: Map<String, UiMode> = mapOf(
        PreviewUiMode.Light to UiMode.LIGHT,
        PreviewUiMode.Dark to UiMode.DARK,
    )

    private val deviceTokens: Map<String, DeviceConfig> = mapOf(
        PreviewDevice.Pixel6Portrait to Device.Pixel6.portrait,
        PreviewDevice.Pixel6Landscape to Device.Pixel6.landscape,
        PreviewDevice.Nexus10Portrait to Device.Nexus10.portrait,
        PreviewDevice.Nexus10Landscape to Device.Nexus10.landscape,
    )

    fun parse(extraMetadata: List<String>, profileKeys: Set<String>): ParsedMetadata {
        val kind = parseKind(extraMetadata)
        val inlineFontScales = parseInlineAxis(extraMetadata, FONT_SCALE_PREFIX, fontScaleTokens, axis = "fontScale")
        val inlineUiModes = parseInlineAxis(extraMetadata, UI_MODE_PREFIX, uiModeTokens, axis = "uiMode")
        val inlineDevices = parseInlineAxis(extraMetadata, DEVICE_PREFIX, deviceTokens, axis = "device")
        val profileTokens = extraMetadata.filter { it in profileKeys }
        if (profileTokens.size > 1) {
            throw SnapshotConfigException("A preview may reference at most one profile, but referenced $profileTokens")
        }
        return ParsedMetadata(
            kind = kind,
            inlineFontScales = inlineFontScales,
            inlineUiModes = inlineUiModes,
            inlineDevices = inlineDevices,
            profileKey = profileTokens.firstOrNull(),
        )
    }

    private fun parseKind(extraMetadata: List<String>): KindTag? {
        val kinds = buildList {
            if (extraMetadata.contains(PreviewSnapshotKind.Component)) add(KindTag.COMPONENT)
            if (extraMetadata.contains(PreviewSnapshotKind.Screen)) add(KindTag.SCREEN)
        }
        if (kinds.size > 1) {
            throw SnapshotConfigException("A preview must have exactly one snapshot kind, but had $kinds")
        }
        return kinds.firstOrNull()
    }

    private fun <T> parseInlineAxis(
        extraMetadata: List<String>,
        prefix: String,
        tokens: Map<String, T>,
        axis: String,
    ): Set<T>? = extraMetadata
        .filter { it.startsWith(prefix) }
        .map { token -> tokens[token] ?: throw SnapshotConfigException("Unknown $axis token '$token'") }
        .toSet()
        .ifEmpty { null }

    private companion object {

        const val FONT_SCALE_PREFIX = "fontScale="
        const val UI_MODE_PREFIX = "uiMode="
        const val DEVICE_PREFIX = "device="
    }
}
