package io.github.ackeecz.snapshots.framework.wrapper

/** Serves canned class bytes by binary name; every other name is reported as unavailable. */
internal class FakeClassBytesSource(private val classes: Map<String, ByteArray>) : ClassBytesSource {

    override fun read(binaryName: String): ByteArray? = classes[binaryName]
}
