package io.github.ackeecz.snapshots.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Sample UI screens. These are pure composables; the snapshot preview entry points that tag them for
 * Showkase live in `SamplePreviews.kt`.
 */

/** A simple welcome screen. */
@Composable
fun WelcomeScreen(modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(text = "Welcome", style = MaterialTheme.typography.headlineLarge)
            Text(
                text = "Snapshot testing for Jetpack Compose, powered by Paparazzi and Showkase.",
                style = MaterialTheme.typography.bodyLarge,
            )
            Button(onClick = {}) {
                Text(text = "Get started")
            }
        }
    }
}

/** A settings list screen. */
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp),
            )
            listOf("Account", "Notifications", "Privacy", "About").forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                )
                HorizontalDivider()
            }
        }
    }
}

/** A pricing screen with a highlighted plan card. */
@Composable
fun PricingScreen(modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(text = "Choose a plan", style = MaterialTheme.typography.headlineMedium)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(text = "Pro", style = MaterialTheme.typography.titleLarge)
                    Text(text = "$19.99 / month", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        text = "Unlimited snapshots across every device and theme.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Subscribe")
            }
        }
    }
}

/** A user profile screen: avatar, name and bio. */
@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6650A4)),
            ) {}
            Text(text = "Ada Lovelace", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "Mobile engineer. Loves deterministic, device-free UI tests.",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

/** A login form screen. */
@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(text = "Sign in", style = MaterialTheme.typography.headlineMedium)
            OutlinedTextField(
                value = "ada@example.com",
                onValueChange = {},
                label = { Text(text = "Email") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text(text = "Password") },
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(onClick = {}) {
                    Text(text = "Log in")
                }
            }
        }
    }
}
