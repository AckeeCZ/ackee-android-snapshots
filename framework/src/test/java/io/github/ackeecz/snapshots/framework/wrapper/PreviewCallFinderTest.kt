package io.github.ackeecz.snapshots.framework.wrapper

import io.github.ackeecz.snapshots.framework.PreviewFunction
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith

private const val FIXTURES = "io.github.ackeecz.snapshots.framework.wrapper.fixtures"
private const val SYNTHETIC_PACKAGE = "io.example.synthetic"
private const val SYNTHETIC_HOST = "$SYNTHETIC_PACKAGE.PropKt"

private val syntheticLocation = ComponentLocation(SYNTHETIC_PACKAGE, "Prop", "Target")

private lateinit var underTest: PreviewCallFinder

internal class PreviewCallFinderTest : FunSpec({

    beforeEach {
        underTest = PreviewCallFinder(fixtureBytesSource())
    }

    fun fixtureLocation(propertyName: String, functionName: String) = ComponentLocation(FIXTURES, propertyName, functionName)

    fun syntheticFinder(vararg classes: Pair<String, ByteArray>) = PreviewCallFinder(FakeClassBytesSource(classes.toMap()))

    test("finds a top-level preview call in the ComposableSingletons host") {
        val location = fixtureLocation("WrappedPreviewFixturesWrapped", "WrappedPreview")

        underTest.find(location) shouldBe PreviewFunction("$FIXTURES.FixturePreviewsKt", "WrappedPreview")
    }

    test("finds an object member call") {
        val location = fixtureLocation("ObjectPreviewFixturesObject", "ObjectPreview")

        underTest.find(location) shouldBe PreviewFunction("$FIXTURES.FixtureObject", "ObjectPreview")
    }

    test("finds a wrapper-class member call") {
        val location = fixtureLocation("HolderPreviewFixturesHolder", "HolderPreview")

        underTest.find(location) shouldBe PreviewFunction("$FIXTURES.FixtureHolder", "HolderPreview")
    }

    test("finds a name-mangled internal member call by its Kotlin name") {
        val location = fixtureLocation("InternalObjectPreviewFixturesInternalObject", "InternalObjectPreview")

        val found = underTest.find(location).shouldNotBeNull()
        found.className shouldBe "$FIXTURES.FixtureObject"
        found.functionName shouldStartWith "InternalObjectPreview$"
    }

    test("finds a preview-parameter call in the property facade host") {
        val location = fixtureLocation("ParameterizedPreviewFixturesParameterized", "ParameterizedPreview")

        underTest.find(location) shouldBe PreviewFunction("$FIXTURES.FixturePreviewsKt", "ParameterizedPreview")
    }

    test("finds the call inside a nested lambda class declared by the host") {
        val nested = "$SYNTHETIC_PACKAGE.PropKt\$1"
        val finder = syntheticFinder(
            SYNTHETIC_HOST to SyntheticClasses.classWithCalls(SYNTHETIC_HOST, calls = emptyList(), innerClasses = listOf(nested)),
            nested to SyntheticClasses.classWithCalls(nested, calls = listOf(SyntheticClasses.staticCall("$SYNTHETIC_PACKAGE.TargetKt", "Target"))),
        )

        finder.find(syntheticLocation) shouldBe PreviewFunction("$SYNTHETIC_PACKAGE.TargetKt", "Target")
    }

    test("returns null when no candidate host class exists") {
        val finder = syntheticFinder()

        finder.find(syntheticLocation) shouldBe null
    }

    test("returns null when the hosts contain no call to the function") {
        val finder = syntheticFinder(
            SYNTHETIC_HOST to SyntheticClasses.classWithCalls(
                SYNTHETIC_HOST,
                calls = listOf(SyntheticClasses.staticCall("java.lang.System", "nanoTime")),
            ),
        )

        finder.find(syntheticLocation) shouldBe null
    }

    test("ignores constructor calls with the same owner") {
        val finder = syntheticFinder(
            SYNTHETIC_HOST to SyntheticClasses.classWithCalls(
                SYNTHETIC_HOST,
                calls = listOf(SyntheticClasses.constructorCall("$SYNTHETIC_PACKAGE.Target")),
            ),
        )

        finder.find(syntheticLocation) shouldBe null
    }
})
