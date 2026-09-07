package io.github.ackeecz.snapshots.framework.wrapper

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

/**
 * Builds class bytes for the shapes the Kotlin compiler cannot emit against the fixture sources —
 * hosts with hand-picked call sites and inner-class tables, and annotations that are not on the classpath.
 */
internal object SyntheticClasses {

    private const val JAVA_11 = 55
    private const val ANNOTATION_ACCESS = Opcodes.ACC_PUBLIC or Opcodes.ACC_ANNOTATION or Opcodes.ACC_ABSTRACT or Opcodes.ACC_INTERFACE

    /** A class with a single method issuing [calls], each given as opcode, owner binary name and method name. */
    fun classWithCalls(
        binaryName: String,
        calls: List<Triple<Int, String, String>>,
        innerClasses: List<String> = emptyList(),
        majorVersion: Int = JAVA_11,
    ): ByteArray {
        val writer = newClass(binaryName, Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL, majorVersion)
        innerClasses.forEach { writer.visitInnerClass(it.internalName(), binaryName.internalName(), null, Opcodes.ACC_STATIC) }
        val method = writer.visitMethod(Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC, "invoke", "()V", null, null)
        method.visitCode()
        calls.forEach { (opcode, owner, name) -> method.visitMethodInsn(opcode, owner.internalName(), name, "()V", false) }
        method.visitInsn(Opcodes.RETURN)
        method.visitMaxs(0, 0)
        method.visitEnd()
        writer.visitEnd()
        return writer.toByteArray()
    }

    /** A class with one method named [methodName] carrying [annotations], each given as a binary name. */
    fun classWithAnnotatedMethod(binaryName: String, methodName: String, annotations: List<String>): ByteArray {
        val writer = newClass(binaryName, Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL)
        val method = writer.visitMethod(Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC, methodName, "()V", null, null)
        annotations.forEach { method.visitAnnotation(it.descriptor(), true).visitEnd() }
        method.visitCode()
        method.visitInsn(Opcodes.RETURN)
        method.visitMaxs(0, 0)
        method.visitEnd()
        writer.visitEnd()
        return writer.toByteArray()
    }

    /** An annotation class itself carrying [annotations], each given as a binary name. */
    fun annotationClass(binaryName: String, annotations: List<String>, majorVersion: Int = JAVA_11): ByteArray {
        val writer = newClass(binaryName, ANNOTATION_ACCESS, majorVersion)
        annotations.forEach { writer.visitAnnotation(it.descriptor(), true).visitEnd() }
        writer.visitEnd()
        return writer.toByteArray()
    }

    fun staticCall(owner: String, name: String) = Triple(Opcodes.INVOKESTATIC, owner, name)

    fun constructorCall(owner: String) = Triple(Opcodes.INVOKESPECIAL, owner, "<init>")

    private fun newClass(binaryName: String, access: Int, majorVersion: Int = JAVA_11) = ClassWriter(0).apply {
        visit(majorVersion, access, binaryName.internalName(), null, "java/lang/Object", null)
    }

    private fun String.descriptor() = "L${internalName()};"

    private fun String.internalName() = replace('.', '/')
}
