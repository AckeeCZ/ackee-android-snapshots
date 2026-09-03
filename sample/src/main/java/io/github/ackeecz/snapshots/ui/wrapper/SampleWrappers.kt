package io.github.ackeecz.snapshots.ui.wrapper

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import androidx.compose.ui.unit.dp

/**
 * Preview wrappers for the `PreviewWrapper` sample group. Each one paints a labelled frame in a
 * [MaterialTheme] colour, which is what proves the framework composes `decorate` (the app theme)
 * **outside** the wrapper: a wrapper reading `MaterialTheme.colorScheme` could not follow the
 * light/dark axis otherwise.
 */

/** Square, primary-coloured frame labelled `FrameWrapper`. */
class FrameWrapper : PreviewWrapperProvider {

    @Composable
    override fun Wrap(content: @Composable () -> Unit) {
        LabelledFrame(label = "FrameWrapper", shape = RectangleShape, content = content)
    }
}

/** Rounded, tertiary-coloured frame labelled `BadgeWrapper`. */
class BadgeWrapper : PreviewWrapperProvider {

    @Composable
    override fun Wrap(content: @Composable () -> Unit) {
        LabelledFrame(
            label = "BadgeWrapper",
            shape = RoundedCornerShape(16.dp),
            tertiary = true,
            content = content,
        )
    }
}

@Composable
private fun LabelledFrame(
    label: String,
    shape: Shape,
    tertiary: Boolean = false,
    content: @Composable () -> Unit,
) {
    val color = if (tertiary) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = color)
        Box(
            modifier = Modifier
                .border(BorderStroke(width = 4.dp, color = color), shape = shape)
                .padding(8.dp),
        ) {
            content()
        }
    }
}

/** Custom preview annotation carrying [FrameWrapper]; proves wrappers are inherited from multipreview annotations. */
@PreviewWrapper(FrameWrapper::class)
annotation class FramedPreview

/** Feeds the `@PreviewParameter` shape control with two values. */
class WrapperLabelProvider : PreviewParameterProvider<String> {

    override val values: Sequence<String> = sequenceOf("One", "Two")
}
