package io.github.ackeecz.snapshots

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.android.showkase.annotation.ShowkaseComposable
import io.github.ackeecz.snapshots.annotations.PreviewSnapshotStrategy
import io.github.ackeecz.snapshots.ui.theme.SnapshotsSampleTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SnapshotsSampleTheme {
                GreetingScreen()
            }
        }
    }
}

@Composable
private fun GreetingScreen() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Greeting(
            name = "Android",
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview
@ShowkaseComposable(
    extraMetadata = [PreviewSnapshotStrategy.Screen]
)
@Composable
fun GreetingPreview() {
    SnapshotsSampleTheme {
        GreetingScreen()
    }
}
