package io.github.ackeecz.snapshots.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.android.showkase.annotation.ShowkaseComposable
import io.github.ackeecz.snapshots.annotations.PreviewSnapshotKind
import io.github.ackeecz.snapshots.ui.theme.SnapshotsSampleTheme

@Composable
fun PrimaryButton(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
    Button(modifier = modifier, onClick = onClick) {
        Text(text = text)
    }
}

@Preview
@ShowkaseComposable(extraMetadata = [PreviewSnapshotKind.Component])
@Composable
fun PrimaryButtonPreview() {
    SnapshotsSampleTheme {
        PrimaryButton(text = "Click me") { }
    }
}
