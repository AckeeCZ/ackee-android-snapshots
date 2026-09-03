@file:Suppress("FunctionNaming", "FunctionOnlyReturningConstant")

package io.github.ackeecz.snapshots.framework.wrapper.fixtures

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewWrapper

@PreviewWrapper(FrameWrapper::class)
@Composable
internal fun WrappedPreview() {
    // Rendering is irrelevant; only the declaration's bytecode is under test.
}

@Composable
internal fun PlainPreview() {
    // Rendering is irrelevant; only the declaration's bytecode is under test.
}

@PreviewWrapper(OtherWrapper::class)
@Composable
internal fun InternalWrappedPreview() {
    // Rendering is irrelevant; only the declaration's bytecode is under test.
}

@PreviewWrapper(NoDefaultCtorWrapper::class)
@Composable
internal fun BrokenWrapperPreview() {
    // Rendering is irrelevant; only the declaration's bytecode is under test.
}

@FramedPreview
@Composable
internal fun MultipreviewWrappedPreview() {
    // Rendering is irrelevant; only the declaration's bytecode is under test.
}

@NestedFramedPreview
@Composable
internal fun NestedWrappedPreview() {
    // Rendering is irrelevant; only the declaration's bytecode is under test.
}

@PreviewWrapper(OtherWrapper::class)
@FramedPreview
@Composable
internal fun FunctionBeatsAnnotationPreview() {
    // Rendering is irrelevant; only the declaration's bytecode is under test.
}

@FramedPreview
@OtherFramedPreview
@Composable
internal fun AmbiguousPreview() {
    // Rendering is irrelevant; only the declaration's bytecode is under test.
}

@FramedPreview
@NestedFramedPreview
@Composable
internal fun SameWrapperTwicePreview() {
    // Rendering is irrelevant; only the declaration's bytecode is under test.
}

@PreviewWrapper(FrameWrapper::class)
@Composable
internal fun Snake_CasePreview() {
    // Rendering is irrelevant; only the declaration's bytecode is under test.
}

@PreviewWrapper(FrameWrapper::class)
@Composable
internal fun ParameterizedPreview(label: String) {
    // Rendering is irrelevant; only the declaration's bytecode is under test.
    label.length
}

internal object FixtureObject {

    @PreviewWrapper(FrameWrapper::class)
    @Composable
    fun ObjectPreview() {
        // Rendering is irrelevant; only the declaration's bytecode is under test.
    }

    @PreviewWrapper(OtherWrapper::class)
    @Composable
    internal fun InternalObjectPreview() {
        // Rendering is irrelevant; only the declaration's bytecode is under test.
    }
}

internal class FixtureHolder {

    @Composable
    fun HolderPreview() {
        // Rendering is irrelevant; only the declaration's bytecode is under test.
    }
}

internal class FixtureLabelProvider : PreviewParameterProvider<String> {

    override val values = sequenceOf("One", "Two")
}
