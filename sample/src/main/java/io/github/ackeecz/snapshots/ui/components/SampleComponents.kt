package io.github.ackeecz.snapshots.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Sample UI components. These are pure composables; the snapshot preview entry points that tag them for
 * Showkase live in `SamplePreviews.kt`.
 */

/** Text-heavy component whose layout clearly reacts to the font-scale axis. */
@Composable
fun ArticleCard(title: String, body: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.padding(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/** Text-less component: font scale has no visible effect on it. */
@Composable
fun Avatar(color: Color, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.padding(16.dp)) {
        Column(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(color),
        ) {}
    }
}

/** A small pill-shaped label. */
@Composable
fun StatusBadge(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.padding(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(percent = 50),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
        )
    }
}

/** A filled chip, themed off the secondary container so the UI-mode axis is clearly visible. */
@Composable
fun ThemedChip(label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.padding(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(size = 8.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

/** A colored banner with a single line of text. */
@Composable
fun PromoBanner(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.padding(16.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.padding(16.dp),
        )
    }
}

/** Large text, so the font-scale axis is obvious at a glance. */
@Composable
fun PriceTag(price: String, modifier: Modifier = Modifier) {
    Text(
        text = price,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(16.dp),
    )
}
