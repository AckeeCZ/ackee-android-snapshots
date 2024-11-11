package io.github.ackeecz.snapshots.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.android.showkase.annotation.ShowkaseComposable
import io.github.ackeecz.snapshots.common.PreviewSnapshotStrategy
import io.github.ackeecz.snapshots.ui.theme.SnapshotsSampleTheme

@Composable
fun PrimaryButton(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text = text)
    }
}

@Preview
@ShowkaseComposable(extraMetadata = [PreviewSnapshotStrategy.Component])
@Composable
fun PrimaryButtonPreview() {
    SnapshotsSampleTheme {
        PrimaryButton(text = "Click me") { }
    }
}
