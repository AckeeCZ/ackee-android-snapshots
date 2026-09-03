package io.github.ackeecz.snapshots.framework.wrapper

/** Outcome of looking for the `@PreviewWrapper` that applies to a preview function. */
internal sealed interface AnnotationLookup {

    /** The preview is wrapped by the `PreviewWrapperProvider` with this binary [wrapperClassName]. */
    data class Found(val wrapperClassName: String) : AnnotationLookup

    /** No wrapper applies to the preview. */
    data object Absent : AnnotationLookup

    /** The declaring class or function could not be read; [reason] names the missing subject. */
    data class Unreadable(val reason: String) : AnnotationLookup

    /** The preview inherits two or more distinct wrappers; [reason] names every one of them. */
    data class Ambiguous(val reason: String) : AnnotationLookup
}
