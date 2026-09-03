package io.github.ackeecz.snapshots.framework.wrapper

import io.github.ackeecz.snapshots.framework.wrapper.fixtures.FrameWrapper
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf

private const val FIXTURES = "io.github.ackeecz.snapshots.framework.wrapper.fixtures"
private const val FRAME_WRAPPER = "$FIXTURES.FrameWrapper"

internal class ReflectivePreviewWrapperFactoryTest : FunSpec({

    fun lookup(className: String) = ReflectivePreviewWrapperFactory.forClass(className, fixtureClassLoader)

    fun readyFactory(className: String) = lookup(className).shouldBeInstanceOf<FactoryLookup.Ready>().factory

    fun shouldBeInvalidBecause(className: String, defect: String) {
        val reason = lookup(className).shouldBeInstanceOf<FactoryLookup.Invalid>().reason

        reason shouldContain className
        reason shouldContain defect
    }

    test("forClass on a valid wrapper is Ready with the class name") {
        readyFactory(FRAME_WRAPPER).className shouldBe FRAME_WRAPPER
    }

    test("create returns an instance of the declared class") {
        readyFactory(FRAME_WRAPPER).create().shouldBeInstanceOf<FrameWrapper>()
    }

    test("create returns a new instance on every call") {
        val factory = readyFactory(FRAME_WRAPPER)

        factory.create() shouldNotBeSameInstanceAs factory.create()
    }

    test("fails when the class is not on the classpath") {
        shouldBeInvalidBecause("io.example.MissingWrapper", "not on the test classpath")
    }

    test("fails when the class does not implement PreviewWrapperProvider") {
        shouldBeInvalidBecause("$FIXTURES.NotAWrapper", "PreviewWrapperProvider")
    }

    test("fails when the class has no zero-argument constructor") {
        shouldBeInvalidBecause("$FIXTURES.NoDefaultCtorWrapper", "public zero-argument constructor")
    }

    test("fails when the only zero-argument constructor is private") {
        shouldBeInvalidBecause("$FIXTURES.PrivateCtorWrapper", "public zero-argument constructor")
    }

    test("fails when the class is abstract") {
        shouldBeInvalidBecause("$FIXTURES.AbstractWrapper", "abstract")
    }
})
