package io.github.ackeecz.snapshots.framework

import io.github.ackeecz.snapshots.annotations.PreviewDevice
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder

private val PIXEL_PORTRAIT = Device.Pixel6.portrait
private val NEXUS_LANDSCAPE = Device.Nexus10.landscape

private fun component(uiMode: UiMode, fontScale: FontScale) =
    SnapshotVariant(kind = SnapshotKind.Component, device = null, uiMode = uiMode, fontScale = fontScale)

private fun screen(device: DeviceConfig, uiMode: UiMode, fontScale: FontScale) =
    SnapshotVariant(kind = SnapshotKind.Screen, device = device, uiMode = uiMode, fontScale = fontScale)

private lateinit var underTest: SnapshotResolver

internal class SnapshotResolverTest : FunSpec({

    beforeEach {
        underTest = SnapshotResolver()
    }

    val bothModes = listOf(UiMode.LIGHT, UiMode.DARK)
    val bothScales = listOf(FontScale.NORMAL, FontScale.LARGE)

    fun resolvedVariants(config: SnapshotConfig) = underTest.resolve(config).map { it.variant }

    test("a component preview renders as Component and ignores configured devices") {
        val config = snapshotConfig(
            previews = listOf(componentTagged()),
            componentsEnabled = true,
            devices = listOf(PIXEL_PORTRAIT),
        )

        resolvedVariants(config) shouldContainExactlyInAnyOrder listOf(
            component(UiMode.LIGHT, FontScale.NORMAL),
        )
    }

    test("a screen preview renders on every configured device") {
        val config = snapshotConfig(
            previews = listOf(screenTagged()),
            devices = listOf(PIXEL_PORTRAIT, NEXUS_LANDSCAPE),
        )

        resolvedVariants(config) shouldContainExactlyInAnyOrder listOf(
            screen(PIXEL_PORTRAIT, UiMode.LIGHT, FontScale.NORMAL),
            screen(NEXUS_LANDSCAPE, UiMode.LIGHT, FontScale.NORMAL),
        )
    }

    test("screen variants are the Cartesian of devices, uiModes and fontScales") {
        val config = snapshotConfig(
            previews = listOf(screenTagged()),
            devices = listOf(PIXEL_PORTRAIT, NEXUS_LANDSCAPE),
            uiModes = bothModes,
            fontScales = listOf(FontScale.NORMAL),
        )

        resolvedVariants(config) shouldContainExactlyInAnyOrder listOf(
            screen(PIXEL_PORTRAIT, UiMode.LIGHT, FontScale.NORMAL),
            screen(PIXEL_PORTRAIT, UiMode.DARK, FontScale.NORMAL),
            screen(NEXUS_LANDSCAPE, UiMode.LIGHT, FontScale.NORMAL),
            screen(NEXUS_LANDSCAPE, UiMode.DARK, FontScale.NORMAL),
        )
    }

    test("component variants are the Cartesian of uiModes and fontScales") {
        val config = snapshotConfig(
            previews = listOf(componentTagged()),
            uiModes = bothModes,
            fontScales = bothScales,
        )

        resolvedVariants(config) shouldContainExactlyInAnyOrder listOf(
            component(UiMode.LIGHT, FontScale.NORMAL),
            component(UiMode.LIGHT, FontScale.LARGE),
            component(UiMode.DARK, FontScale.NORMAL),
            component(UiMode.DARK, FontScale.LARGE),
        )
    }

    test("identical variants from repeated axis values are deduped") {
        val config = snapshotConfig(
            previews = listOf(componentTagged()),
            uiModes = listOf(UiMode.LIGHT, UiMode.LIGHT),
        )

        resolvedVariants(config) shouldContainExactlyInAnyOrder listOf(
            component(UiMode.LIGHT, FontScale.NORMAL),
        )
    }

    test("exclude drops matching cells from the default Cartesian") {
        val config = snapshotConfig(
            previews = listOf(componentTagged()),
            uiModes = bothModes,
            fontScales = bothScales,
            excludes = listOf { variant -> variant.uiMode == UiMode.DARK && variant.fontScale == FontScale.LARGE },
        )

        resolvedVariants(config) shouldContainExactlyInAnyOrder listOf(
            component(UiMode.LIGHT, FontScale.NORMAL),
            component(UiMode.LIGHT, FontScale.LARGE),
            component(UiMode.DARK, FontScale.NORMAL),
        )
    }

    test("multiple exclude predicates compose (any match drops the cell)") {
        val config = snapshotConfig(
            previews = listOf(componentTagged()),
            uiModes = bothModes,
            fontScales = bothScales,
            excludes = listOf(
                { variant: SnapshotVariant -> variant.fontScale == FontScale.LARGE },
                { variant: SnapshotVariant -> variant.uiMode == UiMode.DARK },
            ),
        )

        resolvedVariants(config) shouldContainExactlyInAnyOrder listOf(
            component(UiMode.LIGHT, FontScale.NORMAL),
        )
    }

    test("inline uiMode(DARK) yields {D+N} (exclude still removes D+L)") {
        val config = snapshotConfig(
            previews = listOf(componentTagged(extraTokens = listOf("uiMode=DARK"))),
            uiModes = bothModes,
            fontScales = bothScales,
            excludes = listOf { variant -> variant.uiMode == UiMode.DARK && variant.fontScale == FontScale.LARGE },
        )

        resolvedVariants(config) shouldContainExactlyInAnyOrder listOf(
            component(UiMode.DARK, FontScale.NORMAL),
        )
    }

    test("profile fontScales(NORMAL) narrows to {L+N, D+N}") {
        val config = snapshotConfig(
            previews = listOf(componentTagged(extraTokens = listOf("compact"))),
            uiModes = bothModes,
            fontScales = bothScales,
            profiles = mapOf("compact" to ProfileOverride(fontScales = listOf(FontScale.NORMAL))),
        )

        resolvedVariants(config) shouldContainExactlyInAnyOrder listOf(
            component(UiMode.LIGHT, FontScale.NORMAL),
            component(UiMode.DARK, FontScale.NORMAL),
        )
    }

    test("profile uiModes(DARK) narrows the uiMode axis to {DARK}") {
        val config = snapshotConfig(
            previews = listOf(componentTagged(extraTokens = listOf("darkOnly"))),
            uiModes = bothModes,
            fontScales = listOf(FontScale.NORMAL),
            profiles = mapOf("darkOnly" to ProfileOverride(uiModes = listOf(UiMode.DARK))),
        )

        resolvedVariants(config) shouldContainExactlyInAnyOrder listOf(
            component(UiMode.DARK, FontScale.NORMAL),
        )
    }

    test("inline fontScale(SMALL) widens with class uiModes to {L+S, D+S}") {
        val config = snapshotConfig(
            previews = listOf(componentTagged(extraTokens = listOf("fontScale=SMALL"))),
            uiModes = bothModes,
            fontScales = bothScales,
        )

        resolvedVariants(config) shouldContainExactlyInAnyOrder listOf(
            component(UiMode.LIGHT, FontScale.SMALL),
            component(UiMode.DARK, FontScale.SMALL),
        )
    }

    test("inline fontScale beats profile fontScale on the same axis") {
        val config = snapshotConfig(
            previews = listOf(componentTagged(extraTokens = listOf("compact", "fontScale=LARGE"))),
            uiModes = listOf(UiMode.LIGHT),
            fontScales = bothScales,
            profiles = mapOf("compact" to ProfileOverride(fontScales = listOf(FontScale.NORMAL))),
        )

        resolvedVariants(config) shouldContainExactlyInAnyOrder listOf(
            component(UiMode.LIGHT, FontScale.LARGE),
        )
    }

    test("an axis with no inline or profile override falls back to the class list") {
        val config = snapshotConfig(
            previews = listOf(componentTagged(extraTokens = listOf("compact"))),
            uiModes = bothModes,
            fontScales = bothScales,
            profiles = mapOf("compact" to ProfileOverride(fontScales = listOf(FontScale.SMALL))),
        )

        resolvedVariants(config) shouldContainExactlyInAnyOrder listOf(
            component(UiMode.LIGHT, FontScale.SMALL),
            component(UiMode.DARK, FontScale.SMALL),
        )
    }

    test("profile devices() restricts a screen preview to the named device only") {
        val config = snapshotConfig(
            previews = listOf(screenTagged(extraTokens = listOf("tabletOnly"))),
            devices = listOf(PIXEL_PORTRAIT, NEXUS_LANDSCAPE),
            profiles = mapOf("tabletOnly" to ProfileOverride(devices = listOf(NEXUS_LANDSCAPE))),
        )

        resolvedVariants(config) shouldContainExactlyInAnyOrder listOf(
            screen(NEXUS_LANDSCAPE, UiMode.LIGHT, FontScale.NORMAL),
        )
    }

    test("inline device narrows a screen preview to the tagged device") {
        val config = snapshotConfig(
            previews = listOf(screenTagged(extraTokens = listOf(PreviewDevice.Nexus10Landscape))),
            devices = listOf(PIXEL_PORTRAIT, NEXUS_LANDSCAPE),
        )

        resolvedVariants(config) shouldContainExactlyInAnyOrder listOf(
            screen(NEXUS_LANDSCAPE, UiMode.LIGHT, FontScale.NORMAL),
        )
    }

    test("inline device beats profile devices on the same axis") {
        val config = snapshotConfig(
            previews = listOf(screenTagged(extraTokens = listOf("phoneOnly", PreviewDevice.Nexus10Landscape))),
            devices = listOf(PIXEL_PORTRAIT),
            profiles = mapOf("phoneOnly" to ProfileOverride(devices = listOf(PIXEL_PORTRAIT))),
        )

        resolvedVariants(config) shouldContainExactlyInAnyOrder listOf(
            screen(NEXUS_LANDSCAPE, UiMode.LIGHT, FontScale.NORMAL),
        )
    }

    test("inline device widens beyond the class devices") {
        val config = snapshotConfig(
            previews = listOf(screenTagged(extraTokens = listOf(PreviewDevice.Nexus10Landscape))),
            devices = listOf(PIXEL_PORTRAIT),
        )

        resolvedVariants(config) shouldContainExactlyInAnyOrder listOf(
            screen(NEXUS_LANDSCAPE, UiMode.LIGHT, FontScale.NORMAL),
        )
    }
})
