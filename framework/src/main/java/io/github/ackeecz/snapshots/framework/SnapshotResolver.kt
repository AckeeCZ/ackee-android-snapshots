package io.github.ackeecz.snapshots.framework

internal class SnapshotResolver {

    private val previewIdentifier = PreviewIdentifier()
    private val extraMetadataParser = ExtraMetadataParser()

    fun resolve(config: SnapshotConfig): List<ResolvedSnapshot> {
        val errors = mutableListOf<String>()
        val resolvedSnapshots = previewIdentifier.assignIds(config.previews).flatMap { preview ->
            val parsedMetadata = extraMetadataParser.parse(preview.component.extraMetadata, config.profiles.keys)
            if (parsedMetadata.kind == null) {
                errors += "Preview '${preview.id}' has no snapshot kind tag (expected a component or screen tag)."
                return@flatMap emptyList()
            }

            val variants = variantsFor(config, parsedMetadata)
            if (variants.isEmpty()) {
                errors += "Preview '${preview.id}' resolves to zero snapshot variants " +
                    "(its kind is not enabled, or it is over-restricted by overrides or exclude)."
                return@flatMap emptyList()
            }

            variants.map { variant ->
                ResolvedSnapshot(
                    name = variantName(preview.id, variant),
                    variant = variant,
                    content = preview.component.component,
                )
            }
        }
        val duplicateNames = resolvedSnapshots.groupBy { it.name }.filterValues { it.size > 1 }.keys
        if (duplicateNames.isNotEmpty()) {
            errors += "Duplicate snapshot name(s): ${duplicateNames.joinToString()}"
        }
        if (errors.isNotEmpty()) {
            throw SnapshotConfigException(errors.joinToString(separator = "\n"))
        }
        return resolvedSnapshots
    }

    private fun variantsFor(config: SnapshotConfig, parsedMetadata: ParsedMetadata): List<SnapshotVariant> {
        val profile = parsedMetadata.profileKey?.let { config.profiles[it] }
        val uiModes = resolveAxis(inline = parsedMetadata.inlineUiModes, profile = profile?.uiModes, classLevel = config.uiModes)
        val fontScales = resolveAxis(inline = parsedMetadata.inlineFontScales, profile = profile?.fontScales, classLevel = config.fontScales)
        val devices = resolveAxis(inline = parsedMetadata.inlineDevices, profile = profile?.devices, classLevel = config.devices)
        return buildList {
            if (parsedMetadata.kind == KindTag.COMPONENT && config.componentsEnabled) {
                addAll(variantsOf(SnapshotKind.Component, devices = listOf(null), uiModes = uiModes, fontScales = fontScales))
            }
            if (parsedMetadata.kind == KindTag.SCREEN) {
                addAll(variantsOf(SnapshotKind.Screen, devices = devices, uiModes = uiModes, fontScales = fontScales))
            }
        }
            .distinct()
            .filter { variant -> config.excludes.none { exclude -> exclude(variant) } }
    }

    private fun variantsOf(
        kind: SnapshotKind,
        devices: List<DeviceConfig?>,
        uiModes: List<UiMode>,
        fontScales: List<FontScale>,
    ): List<SnapshotVariant> = devices.flatMap { device ->
        uiModes.flatMap { uiMode ->
            fontScales.map { fontScale ->
                SnapshotVariant(kind = kind, device = device, uiMode = uiMode, fontScale = fontScale)
            }
        }
    }

    private fun <T> resolveAxis(inline: Set<T>?, profile: List<T>?, classLevel: List<T>): List<T> =
        inline?.toList() ?: profile ?: classLevel
}
