package io.github.ackeecz.snapshots.framework.wrapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider

/**
 * Wrapper factory for the integration tests: hands out a fresh pass-through provider per [create] call and
 * keeps every created instance so a test can assert both the call count and per-snapshot distinctness.
 */
internal class FakePreviewWrapperFactory(override val className: String = "FakeWrapper") : PreviewWrapperFactory {

    val created = mutableListOf<PreviewWrapperProvider>()

    override fun create(): PreviewWrapperProvider = PassThroughWrapper().also { created += it }
}

private class PassThroughWrapper : PreviewWrapperProvider {

    @Composable
    override fun Wrap(content: @Composable () -> Unit) {
        content()
    }
}
