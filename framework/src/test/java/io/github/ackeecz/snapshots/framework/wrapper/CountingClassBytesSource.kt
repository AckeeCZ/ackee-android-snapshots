package io.github.ackeecz.snapshots.framework.wrapper

/** Delegates to [delegate] and records every binary name it was asked for, in order. */
internal class CountingClassBytesSource(private val delegate: ClassBytesSource) : ClassBytesSource {

    val reads = mutableListOf<String>()

    override fun read(binaryName: String): ByteArray? {
        reads += binaryName
        return delegate.read(binaryName)
    }
}
