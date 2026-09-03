package io.github.ackeecz.snapshots.framework.wrapper

/**
 * One possible Showkase codegen location of a component: its [packageName], the generated
 * [propertyName] (which is also the generated file name) and the preview function's Kotlin name.
 */
internal data class ComponentLocation(
    val packageName: String,
    val propertyName: String,
    val functionName: String,
)
