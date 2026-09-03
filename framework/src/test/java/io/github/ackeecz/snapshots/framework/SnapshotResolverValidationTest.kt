package io.github.ackeecz.snapshots.framework

import io.github.ackeecz.snapshots.framework.wrapper.FakePreviewWrapperResolver
import io.github.ackeecz.snapshots.framework.wrapper.RecordingWrapperResolverFactory
import io.github.ackeecz.snapshots.framework.wrapper.WrapperResolution
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain

private lateinit var underTest: SnapshotResolver

internal class SnapshotResolverValidationTest : FunSpec({

    beforeEach {
        underTest = SnapshotResolver()
    }

    fun resolveErrorMessage(config: SnapshotConfig): String =
        shouldThrow<SnapshotConfigException> { underTest.resolve(config) }.message ?: ""

    val collidingConfig = snapshotConfig(
        previews = listOf(
            componentTagged(group = "A", name = "B_0", key = "p1"),
            componentTagged(group = "A", name = "B", key = "p2"),
            componentTagged(group = "A", name = "B", key = "p3"),
        ),
    )

    test("an untagged preview throws SnapshotConfigException listing it") {
        val config = snapshotConfig(previews = listOf(previewComponent(group = "My", name = "Widget")))

        resolveErrorMessage(config) shouldContain "My_Widget"
    }

    test("only untagged previews are reported, not tagged ones") {
        val config = snapshotConfig(
            previews = listOf(
                componentTagged(group = "Good", name = "One"),
                previewComponent(group = "Bad", name = "Two"),
            ),
        )

        val message = resolveErrorMessage(config)
        message shouldContain "Bad_Two"
        message shouldNotContain "Good_One"
    }

    test("all untagged previews are listed together") {
        val config = snapshotConfig(
            previews = listOf(
                previewComponent(group = "A", name = "X"),
                previewComponent(group = "B", name = "Y"),
            ),
        )

        val message = resolveErrorMessage(config)
        message shouldContain "A_X"
        message shouldContain "B_Y"
    }

    test("a component preview whose kind is not enabled is over-restricted") {
        val config = snapshotConfig(
            previews = listOf(componentTagged(group = "Comp", name = "Off")),
            componentsEnabled = false,
        )

        resolveErrorMessage(config) shouldContain "Comp_Off"
    }

    test("a screen preview with no configured devices is over-restricted") {
        val config = snapshotConfig(
            previews = listOf(screenTagged(group = "Screen", name = "NoDevice")),
            devices = emptyList(),
        )

        resolveErrorMessage(config) shouldContain "Screen_NoDevice"
    }

    test("a preview explicitly requesting an excluded cell is reported as over-restricted") {
        val config = snapshotConfig(
            previews = listOf(componentTagged(group = "Dark", name = "Only", extraTokens = listOf("uiMode=DARK"))),
            uiModes = listOf(UiMode.LIGHT),
            fontScales = listOf(FontScale.NORMAL),
            excludes = listOf { variant -> variant.uiMode == UiMode.DARK },
        )

        resolveErrorMessage(config) shouldContain "Dark_Only"
    }

    test("a preview whose only surviving cells are excluded resolves to empty") {
        val config = snapshotConfig(
            previews = listOf(componentTagged(group = "All", name = "Gone")),
            uiModes = listOf(UiMode.LIGHT),
            fontScales = listOf(FontScale.NORMAL),
            excludes = listOf { variant -> variant.uiMode == UiMode.LIGHT },
        )

        resolveErrorMessage(config) shouldContain "All_Gone"
    }

    test("two previews resolving to the same name throw a duplicate-name error") {
        resolveErrorMessage(collidingConfig) shouldContain "Duplicate snapshot name"
    }

    test("the duplicate-name error names the colliding tests") {
        resolveErrorMessage(collidingConfig) shouldContain "A_B_0_LIGHT_FontScale-NORMAL"
    }

    test("wrapper resolution is skipped for a preview without a kind tag") {
        val resolver = FakePreviewWrapperResolver()
        val config = snapshotConfig(
            previews = listOf(
                componentTagged(group = "Good", name = "One", key = "tagged"),
                previewComponent(group = "Bad", name = "Two", key = "untagged"),
            ),
            previewWrappers = PreviewWrappers.Enabled(),
        )

        shouldThrow<SnapshotConfigException> {
            SnapshotResolver(wrapperResolverFactory = RecordingWrapperResolverFactory(resolver)).resolve(config)
        }

        resolver.resolvedKeys shouldBe listOf("tagged")
    }

    test("a Failed resolution throws naming the preview id and the reason") {
        val config = snapshotConfig(
            previews = listOf(componentTagged(group = "My", name = "Widget", key = "broken")),
            previewWrappers = PreviewWrappers.Enabled(),
        )
        val factory = resolutionsOf("broken" to WrapperResolution.Failed("the wrapper class is abstract"))

        val message = shouldThrow<SnapshotConfigException> {
            SnapshotResolver(wrapperResolverFactory = factory).resolve(config)
        }.message ?: ""

        message shouldContain "My_Widget"
        message shouldContain "the wrapper class is abstract"
    }

    test("wrapper failures aggregate with other preview errors in one exception") {
        val config = snapshotConfig(
            previews = listOf(
                componentTagged(group = "Broken", name = "One", key = "broken"),
                previewComponent(group = "Untagged", name = "Two", key = "untagged"),
            ),
            previewWrappers = PreviewWrappers.Enabled(),
        )
        val factory = resolutionsOf("broken" to WrapperResolution.Failed("the wrapper class is abstract"))

        val message = shouldThrow<SnapshotConfigException> {
            SnapshotResolver(wrapperResolverFactory = factory).resolve(config)
        }.message ?: ""

        message shouldContain "Broken_One"
        message shouldContain "the wrapper class is abstract"
        message shouldContain "Untagged_Two"
    }
})
