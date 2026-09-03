package io.github.ackeecz.snapshots.framework.wrapper

/** Reads raw class-file bytes by JVM binary name; returns null when the class is not available. */
internal fun interface ClassBytesSource {

    fun read(binaryName: String): ByteArray?
}

/** Reads class bytes from [classLoader]'s resources. */
internal class ClassLoaderBytesSource(private val classLoader: ClassLoader) : ClassBytesSource {

    override fun read(binaryName: String): ByteArray? =
        classLoader.getResourceAsStream("${binaryName.replace('.', '/')}.class")?.use { it.readBytes() }
}
