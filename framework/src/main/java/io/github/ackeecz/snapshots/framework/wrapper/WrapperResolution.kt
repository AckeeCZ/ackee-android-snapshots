package io.github.ackeecz.snapshots.framework.wrapper

import com.airbnb.android.showkase.models.ShowkaseBrowserComponent

/** Outcome of resolving the wrapper of a single discovered preview. */
internal sealed interface WrapperResolution {

    /** The preview is wrapped; [factory] creates one wrapper instance per snapshot. */
    data class Wrapped(val factory: PreviewWrapperFactory) : WrapperResolution

    /** The preview declares no wrapper and renders as-is. */
    data object Unwrapped : WrapperResolution

    /** The wrapper could not be resolved; [reason] carries the message without the `Preview '<id>'` prefix. */
    data class Failed(val reason: String) : WrapperResolution
}

internal fun interface PreviewWrapperResolver {

    fun resolve(component: ShowkaseBrowserComponent): WrapperResolution
}
