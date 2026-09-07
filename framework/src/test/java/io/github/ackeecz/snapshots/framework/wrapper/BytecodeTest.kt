package io.github.ackeecz.snapshots.framework.wrapper

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.objectweb.asm.ClassVisitor

private const val TOO_NEW_CLASS = "io.example.TooNew"
private const val UNSUPPORTED_MAJOR = 99

internal class BytecodeTest : FunSpec({

    fun translatedException() = shouldThrow<UnsupportedClassFileException> {
        SyntheticClasses.classWithCalls(TOO_NEW_CLASS, calls = emptyList(), majorVersion = UNSUPPORTED_MAJOR)
            .acceptClass(TOO_NEW_CLASS, object : ClassVisitor(ASM_API) {}, SKIP_DEBUG_AND_FRAMES)
    }

    test("a class file newer than ASM supports keeps ASM's refusal as the cause") {
        translatedException().cause.shouldBeInstanceOf<IllegalArgumentException>()
    }

    test("the translated message names the class and its major version without the cause's text") {
        translatedException().message shouldBe "Unsupported class file major version $UNSUPPORTED_MAJOR in '$TOO_NEW_CLASS'"
    }
})
