package io.github.ackeecz.snapshots.framework

import io.github.ackeecz.snapshots.annotations.ExperimentalSnapshotsApi

/**
 * A preview function's location: its JVM binary [className] (`com.app.ui.PreviewsKt`, `com.app.ui.Outer$Inner`)
 * and its Kotlin [functionName].
 */
@ExperimentalSnapshotsApi
data class PreviewFunction(val className: String, val functionName: String)
