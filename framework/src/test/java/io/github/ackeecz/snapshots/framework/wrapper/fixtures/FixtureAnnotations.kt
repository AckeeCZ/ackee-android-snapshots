package io.github.ackeecz.snapshots.framework.wrapper.fixtures

import androidx.compose.ui.tooling.preview.PreviewWrapper

@PreviewWrapper(FrameWrapper::class)
internal annotation class FramedPreview

@PreviewWrapper(OtherWrapper::class)
internal annotation class OtherFramedPreview

@FramedPreview
internal annotation class NestedFramedPreview
