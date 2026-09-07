package io.github.ackeecz.snapshots.framework.wrapper

/** Caches [delegate]'s reads by binary name, so a class read more than once costs a single lookup. */
internal class CachingClassBytesSource(private val delegate: ClassBytesSource) : ClassBytesSource {

    private val cache = mutableMapOf<String, ByteArray?>()

    override fun read(binaryName: String): ByteArray? = if (binaryName in cache) {
        cache[binaryName]
    } else {
        delegate.read(binaryName).also { cache[binaryName] = it }
    }
}
