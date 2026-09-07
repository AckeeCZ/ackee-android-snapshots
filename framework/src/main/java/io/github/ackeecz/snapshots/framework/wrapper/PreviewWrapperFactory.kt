package io.github.ackeecz.snapshots.framework.wrapper

import androidx.compose.ui.tooling.preview.PreviewWrapperProvider

/** Creates the `PreviewWrapperProvider` that wraps a preview, one fresh instance per snapshot. */
internal interface PreviewWrapperFactory {

    /** Binary name of the wrapper class this factory instantiates. */
    val className: String

    fun create(): PreviewWrapperProvider
}

/** Outcome of validating a wrapper class named by a `@PreviewWrapper`. */
internal sealed interface FactoryLookup {

    data class Ready(val factory: PreviewWrapperFactory) : FactoryLookup

    data class Invalid(val reason: String) : FactoryLookup
}
