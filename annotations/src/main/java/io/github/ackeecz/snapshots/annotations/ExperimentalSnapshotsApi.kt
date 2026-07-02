package io.github.ackeecz.snapshots.annotations

/**
 * Marks a snapshots API as **experimental**: it is well-tested and safe to use, but its shape
 * (names, signatures, DSL structure) may still change in a future release as the framework is
 * exercised against more, and more complex, projects. Opting in acknowledges that a later upgrade
 * may require small changes to your calling code.
 *
 * Opt in per file or declaration with `@OptIn(ExperimentalSnapshotsApi::class)`, or module-wide via
 * the `-opt-in=io.github.ackeecz.snapshots.annotations.ExperimentalSnapshotsApi` Kotlin compiler
 * argument.
 */
@RequiresOptIn(
    message = "This snapshots API is experimental and may change in a future release. It is " +
        "well-tested and safe to use; opt in with @OptIn(ExperimentalSnapshotsApi::class) to " +
        "acknowledge that a future upgrade may require small code changes.",
    level = RequiresOptIn.Level.WARNING,
)
@Retention(AnnotationRetention.BINARY)
annotation class ExperimentalSnapshotsApi
