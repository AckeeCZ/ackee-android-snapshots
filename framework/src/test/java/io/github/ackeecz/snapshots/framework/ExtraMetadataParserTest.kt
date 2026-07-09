package io.github.ackeecz.snapshots.framework

import io.github.ackeecz.snapshots.annotations.PreviewDevice
import io.github.ackeecz.snapshots.annotations.PreviewSnapshotKind
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private lateinit var underTest: ExtraMetadataParser

internal class ExtraMetadataParserTest : FunSpec({

    beforeEach {
        underTest = ExtraMetadataParser()
    }

    fun parse(vararg tokens: String, profileKeys: Set<String> = emptySet()) =
        underTest.parse(tokens.toList(), profileKeys)

    test("component tag is detected") {
        parse(PreviewSnapshotKind.Component).kind shouldBe KindTag.COMPONENT
    }

    test("screen tag is detected") {
        parse(PreviewSnapshotKind.Screen).kind shouldBe KindTag.SCREEN
    }

    test("no kind tag yields null kind") {
        parse().kind shouldBe null
    }

    test("a preview tagged with more than one kind throws SnapshotConfigException") {
        shouldThrow<SnapshotConfigException> { parse(PreviewSnapshotKind.Component, PreviewSnapshotKind.Screen) }
    }

    test("single fontScale token yields that font scale") {
        val parsed = parse("fontScale=SMALL")

        parsed.inlineFontScales shouldBe setOf(FontScale.SMALL)
        parsed.inlineUiModes shouldBe null
    }

    test("multiple fontScale tokens yield the set") {
        parse("fontScale=SMALL", "fontScale=LARGE").inlineFontScales shouldBe setOf(FontScale.SMALL, FontScale.LARGE)
    }

    test("uiMode token yields that uiMode") {
        val parsed = parse("uiMode=DARK")

        parsed.inlineUiModes shouldBe setOf(UiMode.DARK)
        parsed.inlineFontScales shouldBe null
    }

    test("multiple uiMode tokens yield the set") {
        parse("uiMode=LIGHT", "uiMode=DARK").inlineUiModes shouldBe setOf(UiMode.LIGHT, UiMode.DARK)
    }

    test("each device token maps to its device config") {
        parse(PreviewDevice.Pixel6Portrait).inlineDevices shouldBe setOf(Device.Pixel6.portrait)
        parse(PreviewDevice.Pixel6Landscape).inlineDevices shouldBe setOf(Device.Pixel6.landscape)
        parse(PreviewDevice.Nexus10Portrait).inlineDevices shouldBe setOf(Device.Nexus10.portrait)
        parse(PreviewDevice.Nexus10Landscape).inlineDevices shouldBe setOf(Device.Nexus10.landscape)
    }

    test("multiple device tokens yield the set") {
        parse(PreviewDevice.Pixel6Portrait, PreviewDevice.Nexus10Landscape).inlineDevices shouldBe
            setOf(Device.Pixel6.portrait, Device.Nexus10.landscape)
    }

    test("a device= token with an unknown value throws") {
        shouldThrow<SnapshotConfigException> { parse("device=FLIP_PHONE") }
    }

    test("absent axis tokens yield no inline restriction (null)") {
        val parsed = parse(PreviewSnapshotKind.Component)

        parsed.inlineFontScales shouldBe null
        parsed.inlineUiModes shouldBe null
        parsed.inlineDevices shouldBe null
    }

    test("a fontScale= token with an unknown enum name throws") {
        shouldThrow<SnapshotConfigException> { parse("fontScale=HUGE") }
    }

    test("a uiMode= token with an unknown enum name throws") {
        shouldThrow<SnapshotConfigException> { parse("uiMode=SEPIA") }
    }

    test("an unrecognized non-prefixed token is ignored") {
        val parsed = parse("somethingRandom")

        parsed.inlineFontScales shouldBe null
        parsed.inlineUiModes shouldBe null
    }

    test("a token equal to a profile key is recognized as that profile") {
        parse("noText", profileKeys = setOf("noText")).profileKey shouldBe "noText"
    }

    test("more than one profile token throws SnapshotConfigException") {
        shouldThrow<SnapshotConfigException> {
            parse("compact", "noText", profileKeys = setOf("compact", "noText"))
        }
    }
})
