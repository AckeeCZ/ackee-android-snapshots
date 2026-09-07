package io.github.ackeecz.snapshots.framework.wrapper

import io.github.ackeecz.snapshots.framework.PreviewFunction
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf

private const val FIXTURES = "io.github.ackeecz.snapshots.framework.wrapper.fixtures"
private const val PREVIEWS = "$FIXTURES.FixturePreviewsKt"
private const val OBJECT = "$FIXTURES.FixtureObject"
private const val SYNTHETIC_OWNER = "io.example.Owner"

private lateinit var underTest: PreviewWrapperAnnotationReader

internal class PreviewWrapperAnnotationReaderTest : FunSpec({

    beforeEach {
        underTest = PreviewWrapperAnnotationReader(fixtureBytesSource())
    }

    fun previewFunction(functionName: String, className: String = PREVIEWS) = PreviewFunction(className, functionName)

    fun syntheticReader(vararg classes: Pair<String, ByteArray>) = PreviewWrapperAnnotationReader(FakeClassBytesSource(classes.toMap()))

    fun unreadableReason(function: PreviewFunction) = underTest.read(function).shouldBeInstanceOf<AnnotationLookup.Unreadable>().reason

    fun ambiguousReason(function: PreviewFunction) = underTest.read(function).shouldBeInstanceOf<AnnotationLookup.Ambiguous>().reason

    test("returns the wrapper declared on the function") {
        underTest.read(previewFunction("WrappedPreview")) shouldBe AnnotationLookup.Found("$FIXTURES.FrameWrapper")
    }

    test("returns Absent for a function without @PreviewWrapper") {
        underTest.read(previewFunction("PlainPreview")) shouldBe AnnotationLookup.Absent
    }

    test("reads a name-mangled internal member when asked by its Kotlin name") {
        underTest.read(previewFunction("InternalObjectPreview", OBJECT)) shouldBe AnnotationLookup.Found("$FIXTURES.OtherWrapper")
    }

    test("reads an internal top-level function") {
        underTest.read(previewFunction("InternalWrappedPreview")) shouldBe AnnotationLookup.Found("$FIXTURES.OtherWrapper")
    }

    test("fails naming the class when the declaring class is not found") {
        unreadableReason(previewFunction("WrappedPreview", "io.example.MissingKt")) shouldContain "io.example.MissingKt"
    }

    test("fails naming the function when it is not found in the class") {
        unreadableReason(previewFunction("NoSuchPreview")) shouldContain "NoSuchPreview"
    }

    test("returns the wrapper inherited from a custom preview annotation") {
        underTest.read(previewFunction("MultipreviewWrappedPreview")) shouldBe AnnotationLookup.Found("$FIXTURES.FrameWrapper")
    }

    test("returns the wrapper inherited through a nested annotation chain") {
        underTest.read(previewFunction("NestedWrappedPreview")) shouldBe AnnotationLookup.Found("$FIXTURES.FrameWrapper")
    }

    test("a function-level wrapper wins over an inherited one") {
        underTest.read(previewFunction("FunctionBeatsAnnotationPreview")) shouldBe AnnotationLookup.Found("$FIXTURES.OtherWrapper")
    }

    test("the same wrapper reached through two annotations is not ambiguous") {
        underTest.read(previewFunction("SameWrapperTwicePreview")) shouldBe AnnotationLookup.Found("$FIXTURES.FrameWrapper")
    }

    test("two different inherited wrappers fail as ambiguous, naming both") {
        val reason = ambiguousReason(previewFunction("AmbiguousPreview"))

        reason shouldContain "$FIXTURES.FrameWrapper"
        reason shouldContain "$FIXTURES.OtherWrapper"
    }

    test("annotation types missing from the classpath are skipped") {
        val reader = syntheticReader(
            SYNTHETIC_OWNER to SyntheticClasses.classWithAnnotatedMethod(SYNTHETIC_OWNER, "Preview", listOf("io.example.Unknown")),
        )

        reader.read(PreviewFunction(SYNTHETIC_OWNER, "Preview")) shouldBe AnnotationLookup.Absent
    }

    test("cyclic annotation references terminate") {
        val first = "io.example.First"
        val second = "io.example.Second"
        val reader = syntheticReader(
            SYNTHETIC_OWNER to SyntheticClasses.classWithAnnotatedMethod(SYNTHETIC_OWNER, "Preview", listOf(first)),
            first to SyntheticClasses.annotationClass(first, listOf(second)),
            second to SyntheticClasses.annotationClass(second, listOf(first)),
        )

        reader.read(PreviewFunction(SYNTHETIC_OWNER, "Preview")) shouldBe AnnotationLookup.Absent
    }

    test("reads each class's bytes once across lookups in the same facade") {
        val counting = CountingClassBytesSource(fixtureBytesSource())
        val reader = PreviewWrapperAnnotationReader(counting)

        reader.read(previewFunction("WrappedPreview"))
        reader.read(previewFunction("PlainPreview"))

        counting.reads.count { it == PREVIEWS } shouldBe 1
    }

    test("reads an annotation class once across previews that share it") {
        val counting = CountingClassBytesSource(fixtureBytesSource())
        val reader = PreviewWrapperAnnotationReader(counting)

        reader.read(previewFunction("MultipreviewWrappedPreview"))
        reader.read(previewFunction("SameWrapperTwicePreview"))

        counting.reads.count { it == "$FIXTURES.FramedPreview" } shouldBe 1
    }
})
