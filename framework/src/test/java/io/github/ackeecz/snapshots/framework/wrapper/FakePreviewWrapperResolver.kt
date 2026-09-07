package io.github.ackeecz.snapshots.framework.wrapper

import com.airbnb.android.showkase.models.ShowkaseBrowserComponent

/**
 * Serves a canned [WrapperResolution] per `componentKey` and records the keys it was asked about, so tests can
 * assert both what the resolver attaches and how often it is consulted.
 */
internal class FakePreviewWrapperResolver(
    private val resolutions: Map<String, WrapperResolution> = emptyMap(),
) : PreviewWrapperResolver {

    val resolvedKeys = mutableListOf<String>()

    override fun resolve(component: ShowkaseBrowserComponent): WrapperResolution {
        resolvedKeys += component.componentKey
        return resolutions[component.componentKey] ?: WrapperResolution.Unwrapped
    }
}
