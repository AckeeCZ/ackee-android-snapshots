package io.github.ackeecz.snapshots.framework.wrapper

import io.github.ackeecz.snapshots.framework.PreviewFunction
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type

/**
 * Reads the `@PreviewWrapper` that applies to a preview function from its declaring class's bytecode,
 * following custom preview annotations recursively.
 */
internal class PreviewWrapperAnnotationReader(rawBytes: ClassBytesSource) {

    // Cached because previews sharing a facade or a custom preview annotation would otherwise re-read the same bytes.
    private val bytes: ClassBytesSource = CachingClassBytesSource(rawBytes)

    fun read(function: PreviewFunction): AnnotationLookup {
        val classBytes = bytes.read(function.className)
            ?: return AnnotationLookup.Unreadable("its declaring class '${function.className}' is not on the test classpath")
        val annotations = methodAnnotations(function.className, classBytes, function.functionName)
        return if (annotations == null) {
            AnnotationLookup.Unreadable("function '${function.functionName}' was not found in class '${function.className}'")
        } else {
            resolve(annotations)
        }
    }

    private fun resolve(annotations: List<AnnotationRef>): AnnotationLookup {
        val direct = annotations.firstNotNullOfOrNull { it.wrapperClassName }
        if (direct != null) return AnnotationLookup.Found(direct)
        val inherited = linkedSetOf<String>()
        val visited = mutableSetOf<String>()
        annotations.forEach { collectInherited(it.className, inherited, visited) }
        return when (inherited.size) {
            0 -> AnnotationLookup.Absent
            1 -> AnnotationLookup.Found(inherited.first())
            else -> AnnotationLookup.Ambiguous(
                "its preview annotations declare conflicting wrappers (${inherited.joinToString()}); " +
                    "declare @PreviewWrapper directly on the preview function to pick one",
            )
        }
    }

    private fun collectInherited(annotationClassName: String, into: MutableSet<String>, visited: MutableSet<String>) {
        if (!visited.add(annotationClassName)) return
        val annotations = bytes.read(annotationClassName)?.let { classAnnotations(annotationClassName, it) } ?: return
        val direct = annotations.firstNotNullOfOrNull { it.wrapperClassName }
        if (direct != null) {
            into += direct
        } else {
            annotations.forEach { collectInherited(it.className, into, visited) }
        }
    }

    private fun methodAnnotations(className: String, classBytes: ByteArray, functionName: String): List<AnnotationRef>? {
        var annotations: MutableList<AnnotationRef>? = null
        classBytes.accept(
            className,
            object : ClassVisitor(ASM_API) {

                override fun visitMethod(
                    access: Int,
                    name: String,
                    descriptor: String,
                    signature: String?,
                    exceptions: Array<out String>?,
                ): MethodVisitor? {
                    if (!matchesKotlinName(name, functionName)) return null
                    val collected = annotations ?: mutableListOf<AnnotationRef>().also { annotations = it }
                    return object : MethodVisitor(ASM_API) {

                        override fun visitAnnotation(annotationDescriptor: String, visible: Boolean) =
                            annotationRefVisitor(annotationDescriptor, collected)
                    }
                }
            },
        )
        return annotations
    }

    private fun classAnnotations(className: String, classBytes: ByteArray): List<AnnotationRef> {
        val annotations = mutableListOf<AnnotationRef>()
        classBytes.accept(
            className,
            object : ClassVisitor(ASM_API) {

                override fun visitAnnotation(descriptor: String, visible: Boolean) = annotationRefVisitor(descriptor, annotations)
            },
        )
        return annotations
    }

    private fun annotationRefVisitor(descriptor: String, into: MutableList<AnnotationRef>): AnnotationVisitor {
        val ref = AnnotationRef(className = Type.getType(descriptor).className)
        into += ref
        return object : AnnotationVisitor(ASM_API) {

            override fun visit(name: String?, value: Any?) {
                if (ref.className == PREVIEW_WRAPPER && value is Type) ref.wrapperClassName = value.className
            }
        }
    }

    private fun ByteArray.accept(className: String, visitor: ClassVisitor) =
        acceptClass(className, visitor, ClassReader.SKIP_CODE or SKIP_DEBUG_AND_FRAMES)

    private class AnnotationRef(val className: String, var wrapperClassName: String? = null)

    private companion object {

        const val PREVIEW_WRAPPER = "androidx.compose.ui.tooling.preview.PreviewWrapper"
    }
}
