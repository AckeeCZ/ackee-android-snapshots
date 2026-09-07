package io.github.ackeecz.snapshots.framework.wrapper

import com.airbnb.android.showkase.models.ShowkaseBrowserComponent
import io.github.ackeecz.snapshots.framework.PreviewFunction
import io.github.ackeecz.snapshots.framework.PreviewWrappers
import io.github.ackeecz.snapshots.framework.wrapper.fixtures.AmbiguousPreviewFixturesAmbiguous
import io.github.ackeecz.snapshots.framework.wrapper.fixtures.BrokenWrapperPreviewFixturesBroken
import io.github.ackeecz.snapshots.framework.wrapper.fixtures.InternalWrappedPreviewFixturesInternal
import io.github.ackeecz.snapshots.framework.wrapper.fixtures.MultipreviewWrappedPreviewFixturesMultipreview
import io.github.ackeecz.snapshots.framework.wrapper.fixtures.ObjectPreviewFixturesObject
import io.github.ackeecz.snapshots.framework.wrapper.fixtures.ParameterizedPreviewFixturesParameterized
import io.github.ackeecz.snapshots.framework.wrapper.fixtures.PlainPreviewFixturesPlain
import io.github.ackeecz.snapshots.framework.wrapper.fixtures.SnakeCasePreviewFixturesSnake
import io.github.ackeecz.snapshots.framework.wrapper.fixtures.WrappedPreviewFixturesWrapped
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.types.shouldBeInstanceOf

private const val FIXTURES = "io.github.ackeecz.snapshots.framework.wrapper.fixtures"
private const val UNMAPPABLE_KEY = "io.example.ui_NoSuchPreview_null_Cards_Card_0_null"

private const val UNSUPPORTED_MAJOR = 99
private const val SYNTHETIC_PACKAGE = "io.example.ui"
private const val SYNTHETIC_SINGLETON_HOST = "$SYNTHETIC_PACKAGE.ComposableSingletons\$TargetPreviewCardsCardKt"
private const val SYNTHETIC_FACADE = "$SYNTHETIC_PACKAGE.TargetKt"
private const val SYNTHETIC_ANNOTATION = "$SYNTHETIC_PACKAGE.NewerAnnotation"

private val unmappableComponent = fixtureComponent(key = UNMAPPABLE_KEY)
private val hostWithoutCallComponent = fixtureComponent(
    key = "${FIXTURES}_Wrapped_null_PreviewFixtures_Wrapped_0_null",
    group = "PreviewFixtures",
    name = "Wrapped",
)
private val syntheticComponent = fixtureComponent(key = "${SYNTHETIC_PACKAGE}_TargetPreview_null_Cards_Card_0_null")

internal class BytecodePreviewWrapperResolverTest : FunSpec({

    fun resolver(
        bytes: ClassBytesSource = fixtureBytesSource(),
        settings: PreviewWrappers.Enabled = PreviewWrappers.Enabled(),
    ) = BytecodePreviewWrapperResolver(settings, bytes, fixtureClassLoader)

    fun resolveWith(settings: PreviewWrappers.Enabled, component: ShowkaseBrowserComponent) = resolver(settings = settings).resolve(component)

    fun wrapperClassOf(component: ShowkaseBrowserComponent) =
        resolver().resolve(component).shouldBeInstanceOf<WrapperResolution.Wrapped>().factory.className

    fun failureReasonOf(resolution: WrapperResolution) = resolution.shouldBeInstanceOf<WrapperResolution.Failed>().reason

    fun failureReason(component: ShowkaseBrowserComponent) = failureReasonOf(resolver().resolve(component))

    test("a directly annotated preview resolves to a factory for its wrapper") {
        wrapperClassOf(WrappedPreviewFixturesWrapped) shouldBe "$FIXTURES.FrameWrapper"
    }

    test("an unannotated preview resolves to Unwrapped") {
        resolver().resolve(PlainPreviewFixturesPlain) shouldBe WrapperResolution.Unwrapped
    }

    test("an internal preview resolves") {
        wrapperClassOf(InternalWrappedPreviewFixturesInternal) shouldBe "$FIXTURES.OtherWrapper"
    }

    test("an object-nested preview resolves") {
        wrapperClassOf(ObjectPreviewFixturesObject) shouldBe "$FIXTURES.FrameWrapper"
    }

    test("a preview inheriting its wrapper from a custom annotation resolves") {
        wrapperClassOf(MultipreviewWrappedPreviewFixturesMultipreview) shouldBe "$FIXTURES.FrameWrapper"
    }

    test("a preview-parameter component resolves") {
        wrapperClassOf(ParameterizedPreviewFixturesParameterized[1]) shouldBe "$FIXTURES.FrameWrapper"
    }

    test("later package splits are tried when the first has no host") {
        wrapperClassOf(SnakeCasePreviewFixturesSnake) shouldBe "$FIXTURES.FrameWrapper"
    }

    test("fails naming the componentKey when no generated host exists") {
        failureReason(unmappableComponent) shouldContain UNMAPPABLE_KEY
    }

    test("every mapping failure names both remedies") {
        val missingClass = PreviewWrappers.Enabled { PreviewFunction("io.example.MissingKt", "WrappedPreview") }
        val missingFunction = PreviewWrappers.Enabled { PreviewFunction("$FIXTURES.FixturePreviewsKt", "NoSuchPreview") }
        val reasons = listOf(
            failureReason(unmappableComponent),
            failureReason(hostWithoutCallComponent),
            failureReasonOf(resolveWith(missingClass, WrappedPreviewFixturesWrapped)),
            failureReasonOf(resolveWith(missingFunction, WrappedPreviewFixturesWrapped)),
        )

        reasons.forEach { reason ->
            withClue(reason) {
                reason shouldContain "PreviewWrappers.Enabled"
                reason shouldContain "PreviewWrappers.Disabled"
            }
        }
    }

    test("fails naming the host and function when the host has no call to it") {
        val reason = failureReason(hostWithoutCallComponent)

        reason shouldContain "ComposableSingletons\$WrappedPreviewFixturesWrappedKt"
        reason shouldContain "function 'Wrapped'"
    }

    test("fails naming the wrapper when its class is invalid") {
        val reason = failureReason(BrokenWrapperPreviewFixturesBroken)

        reason shouldContain "$FIXTURES.NoDefaultCtorWrapper"
        reason shouldNotContain "PreviewWrappers.Disabled"
    }

    test("fails as ambiguous naming both inherited wrappers") {
        val reason = failureReason(AmbiguousPreviewFixturesAmbiguous)

        reason shouldContain "$FIXTURES.FrameWrapper"
        reason shouldContain "$FIXTURES.OtherWrapper"
        reason shouldNotContain "PreviewWrappers.Disabled"
    }

    test("fails naming the class and its major version when a class file is newer than ASM supports") {
        val bytes = FakeClassBytesSource(
            mapOf(
                SYNTHETIC_SINGLETON_HOST to SyntheticClasses.classWithCalls(
                    SYNTHETIC_SINGLETON_HOST,
                    calls = emptyList(),
                    majorVersion = UNSUPPORTED_MAJOR,
                ),
            ),
        )

        val reason = resolver(bytes).resolve(syntheticComponent).shouldBeInstanceOf<WrapperResolution.Failed>().reason

        reason shouldContain SYNTHETIC_SINGLETON_HOST
        reason shouldContain UNSUPPORTED_MAJOR.toString()
        reason shouldContain "org.ow2.asm:asm"
    }

    test("extra: an unsupported annotation class file fails with the same remedy") {
        val bytes = FakeClassBytesSource(
            mapOf(
                SYNTHETIC_SINGLETON_HOST to SyntheticClasses.classWithCalls(
                    SYNTHETIC_SINGLETON_HOST,
                    calls = listOf(SyntheticClasses.staticCall(SYNTHETIC_FACADE, "TargetPreview")),
                ),
                SYNTHETIC_FACADE to SyntheticClasses.classWithAnnotatedMethod(SYNTHETIC_FACADE, "TargetPreview", listOf(SYNTHETIC_ANNOTATION)),
                SYNTHETIC_ANNOTATION to SyntheticClasses.annotationClass(SYNTHETIC_ANNOTATION, emptyList(), majorVersion = UNSUPPORTED_MAJOR),
            ),
        )

        val reason = resolver(bytes).resolve(syntheticComponent).shouldBeInstanceOf<WrapperResolution.Failed>().reason

        reason shouldContain SYNTHETIC_ANNOTATION
        reason shouldContain "org.ow2.asm:asm"
    }

    test("a non-null locate result bypasses automatic mapping") {
        val settings = PreviewWrappers.Enabled { PreviewFunction("$FIXTURES.FixturePreviewsKt", "WrappedPreview") }

        val resolution = resolveWith(settings, unmappableComponent)

        resolution.shouldBeInstanceOf<WrapperResolution.Wrapped>().factory.className shouldBe "$FIXTURES.FrameWrapper"
    }

    test("a null locate result falls through to automatic mapping") {
        val settings = PreviewWrappers.Enabled { null }

        resolveWith(settings, PlainPreviewFixturesPlain) shouldBe WrapperResolution.Unwrapped
    }

    test("locate is invoked exactly once per component") {
        val calls = mutableListOf<ShowkaseBrowserComponent>()
        val settings = PreviewWrappers.Enabled { component ->
            calls += component
            null
        }

        resolveWith(settings, WrappedPreviewFixturesWrapped)

        calls shouldBe listOf(WrappedPreviewFixturesWrapped)
    }

    test("a locate result naming a missing class fails naming that class") {
        val settings = PreviewWrappers.Enabled { PreviewFunction("io.example.MissingKt", "WrappedPreview") }

        val resolution = resolveWith(settings, WrappedPreviewFixturesWrapped)

        resolution.shouldBeInstanceOf<WrapperResolution.Failed>().reason shouldContain "io.example.MissingKt"
    }
})
