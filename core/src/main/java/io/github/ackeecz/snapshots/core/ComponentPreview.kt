package io.github.ackeecz.snapshots.core

import androidx.compose.runtime.Composable
import com.airbnb.android.showkase.models.ShowkaseBrowserComponent

/**
 * Encapsulation over [ShowkaseBrowserComponent] with String id for easier identification.
 */
class ComponentPreview(
    private val id: String,
    showkaseBrowserComponent: ShowkaseBrowserComponent
) {

    val content: @Composable () -> Unit = showkaseBrowserComponent.component

    override fun toString() = id
}
