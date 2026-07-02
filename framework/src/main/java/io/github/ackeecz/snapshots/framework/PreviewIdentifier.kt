package io.github.ackeecz.snapshots.framework

import com.airbnb.android.showkase.models.ShowkaseBrowserComponent

internal data class IdentifiedPreview(
    val id: String,
    val component: ShowkaseBrowserComponent,
)

internal class PreviewIdentifier {

    fun assignIds(components: List<ShowkaseBrowserComponent>): List<IdentifiedPreview> {
        return components
            .groupBy { "${it.group}_${it.componentName}" }
            .flatMap { (key, grouped) ->
                grouped.mapIndexed { index, component ->
                    val id = if (grouped.size == 1) key else "${key}_$index"
                    IdentifiedPreview(id = id, component = component)
                }
            }
            .sortedBy { it.id }
    }
}
