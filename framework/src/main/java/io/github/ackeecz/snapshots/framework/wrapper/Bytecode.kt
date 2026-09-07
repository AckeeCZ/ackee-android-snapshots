package io.github.ackeecz.snapshots.framework.wrapper

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

internal const val ASM_API = Opcodes.ASM9

/** Class files skip method code by default; only annotation lookups need it, added on top. */
internal const val SKIP_DEBUG_AND_FRAMES = ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES

/**
 * Whether the JVM method name [jvmName] denotes the Kotlin function [kotlinName]. Kotlin mangles
 * `internal` *members* as `name$module`; top-level `internal` functions keep their name.
 */
internal fun matchesKotlinName(jvmName: String, kotlinName: String) = jvmName == kotlinName || jvmName.startsWith("$kotlinName$")

/** A binary name (`com.app.Foo`) as the JVM internal name ASM and class-loader resource paths use (`com/app/Foo`). */
internal fun String.toInternalName(): String = replace('.', '/')

/** An ASM internal name (`com/app/Foo`) as the binary name (`com.app.Foo`) the rest of the pipeline works with. */
internal fun String.toBinaryName(): String = replace('/', '.')

/** A class file the bundled ASM cannot read; carries what the error message has to name, over ASM's own [cause]. */
internal class UnsupportedClassFileException(
    val className: String,
    val majorVersion: Int,
    cause: Throwable,
) : Exception("Unsupported class file major version $majorVersion in '$className'", cause)

/** Parses [className]'s bytes, translating ASM's refusal of a too-new class file into [UnsupportedClassFileException]. */
internal fun ByteArray.acceptClass(className: String, visitor: ClassVisitor, parsingOptions: Int) {
    val reader = try {
        ClassReader(this)
    } catch (e: IllegalArgumentException) {
        throw UnsupportedClassFileException(className, majorVersion(), e)
    }
    reader.accept(visitor, parsingOptions)
}

private fun ByteArray.majorVersion() = ((this[MAJOR_VERSION_OFFSET].toInt() and BYTE_MASK) shl Byte.SIZE_BITS) or
    (this[MAJOR_VERSION_OFFSET + 1].toInt() and BYTE_MASK)

private const val MAJOR_VERSION_OFFSET = 6
private const val BYTE_MASK = 0xFF
