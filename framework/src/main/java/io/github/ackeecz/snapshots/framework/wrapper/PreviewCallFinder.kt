package io.github.ackeecz.snapshots.framework.wrapper

import io.github.ackeecz.snapshots.framework.PreviewFunction
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

/**
 * Finds the preview function invoked from the lambda Showkase generated for a component, by scanning
 * the generated host classes of a [ComponentLocation].
 */
internal class PreviewCallFinder(private val bytes: ClassBytesSource) {

    fun find(location: ComponentLocation): PreviewFunction? =
        hostNames(location).firstNotNullOfOrNull { host -> findIn(host, location.functionName) }

    private fun hostNames(location: ComponentLocation): List<String> = listOf(
        "${location.packageName}.ComposableSingletons$${location.propertyName}Kt",
        "${location.packageName}.${location.propertyName}Kt",
    )

    /** The [location]'s candidate host classes that actually exist, in probe order. */
    fun existingHosts(location: ComponentLocation): List<String> = hostNames(location).filter { bytes.read(it) != null }

    private fun findIn(hostName: String, functionName: String): PreviewFunction? {
        val hostBytes = bytes.read(hostName) ?: return null
        val nested = nestedClassNames(hostName, hostBytes).mapNotNull { name -> bytes.read(name)?.let { name to it } }
        return (listOf(hostName to hostBytes) + nested)
            .firstNotNullOfOrNull { (name, classBytes) -> findCall(name, classBytes, functionName) }
    }

    private fun nestedClassNames(hostName: String, hostBytes: ByteArray): List<String> {
        val prefix = "${hostName.replace('.', '/')}$"
        val names = mutableListOf<String>()
        hostBytes.accept(hostName, 
            object : ClassVisitor(ASM_API) {

                override fun visitInnerClass(name: String, outerName: String?, innerName: String?, access: Int) {
                    if (name.startsWith(prefix)) names += name.replace('/', '.')
                }
            },
        )
        return names
    }

    private fun findCall(className: String, classBytes: ByteArray, functionName: String): PreviewFunction? {
        var found: PreviewFunction? = null
        classBytes.accept(className, 
            object : ClassVisitor(ASM_API) {

                override fun visitMethod(
                    access: Int,
                    name: String,
                    descriptor: String,
                    signature: String?,
                    exceptions: Array<out String>?,
                ) = object : MethodVisitor(ASM_API) {

                    override fun visitMethodInsn(
                        opcode: Int,
                        owner: String,
                        callName: String,
                        callDescriptor: String,
                        isInterface: Boolean,
                    ) {
                        if (found == null && matchesKotlinName(callName, functionName)) {
                            found = PreviewFunction(className = owner.replace('/', '.'), functionName = callName)
                        }
                    }
                }
            },
        )
        return found
    }

    private fun ByteArray.accept(className: String, visitor: ClassVisitor) = acceptClass(className, visitor, SKIP_UNNEEDED)

    private companion object {

        const val SKIP_UNNEEDED = ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES
    }
}
