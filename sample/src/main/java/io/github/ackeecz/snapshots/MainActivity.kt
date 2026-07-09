package io.github.ackeecz.snapshots

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.ackeecz.snapshots.ui.screens.WelcomeScreen
import io.github.ackeecz.snapshots.ui.theme.SnapshotsSampleTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SnapshotsSampleTheme {
                WelcomeScreen()
            }
        }
    }
}
