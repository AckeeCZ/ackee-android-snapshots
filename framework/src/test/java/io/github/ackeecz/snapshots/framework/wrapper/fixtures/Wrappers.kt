package io.github.ackeecz.snapshots.framework.wrapper.fixtures

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider

internal class FrameWrapper : PreviewWrapperProvider {

    @Composable
    override fun Wrap(content: @Composable () -> Unit) {
        content()
    }
}

internal class OtherWrapper : PreviewWrapperProvider {

    @Composable
    override fun Wrap(content: @Composable () -> Unit) {
        content()
    }
}

internal class NoDefaultCtorWrapper(val label: String) : PreviewWrapperProvider {

    @Composable
    override fun Wrap(content: @Composable () -> Unit) {
        content()
    }
}

internal class PrivateCtorWrapper private constructor() : PreviewWrapperProvider {

    @Composable
    override fun Wrap(content: @Composable () -> Unit) {
        content()
    }
}

internal abstract class AbstractWrapper : PreviewWrapperProvider

internal class NotAWrapper
