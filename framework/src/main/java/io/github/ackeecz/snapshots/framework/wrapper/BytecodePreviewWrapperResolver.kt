package io.github.ackeecz.snapshots.framework.wrapper

import com.airbnb.android.showkase.models.ShowkaseBrowserComponent
import io.github.ackeecz.snapshots.framework.PreviewFunction
import io.github.ackeecz.snapshots.framework.PreviewWrappers

/**
 * Resolves a preview's `@PreviewWrapper` by mapping its Showkase component key to the declaring function
 * and reading that function's class file.
 */
internal class BytecodePreviewWrapperResolver(
    private val settings: PreviewWrappers.Enabled = PreviewWrappers.Enabled(),
    bytes: ClassBytesSource = ClassLoaderBytesSource(frameworkClassLoader),
    private val classLoader: ClassLoader = frameworkClassLoader,
) : PreviewWrapperResolver {

    private val cachedBytes = CachingClassBytesSource(bytes)
    private val keyParser = ShowkaseKeyParser()
    private val callFinder = PreviewCallFinder(cachedBytes)
    private val annotationReader = PreviewWrapperAnnotationReader(cachedBytes)

    override fun resolve(component: ShowkaseBrowserComponent): WrapperResolution = try {
        resolveWrapper(component)
    } catch (e: UnsupportedClassFileException) {
        WrapperResolution.Failed(unsupportedClassFileFailure(e))
    }

    private fun resolveWrapper(component: ShowkaseBrowserComponent): WrapperResolution =
        settings.locate?.invoke(component)?.let { wrapperOf(it) } ?: automaticallyResolve(component)

    private fun automaticallyResolve(component: ShowkaseBrowserComponent): WrapperResolution {
        val candidates = keyParser.candidates(component)
        val function = candidates.firstNotNullOfOrNull { callFinder.find(it) }
            ?: return WrapperResolution.Failed(mappingFailure(component, candidates))
        return wrapperOf(function)
    }

    private fun wrapperOf(function: PreviewFunction): WrapperResolution = when (val lookup = annotationReader.read(function)) {
        is AnnotationLookup.Absent -> WrapperResolution.Unwrapped
        is AnnotationLookup.Unreadable -> WrapperResolution.Failed("${lookup.reason} $REMEDIES")
        is AnnotationLookup.Ambiguous -> WrapperResolution.Failed(lookup.reason)
        is AnnotationLookup.Found -> factoryFor(lookup.wrapperClassName)
    }

    private fun factoryFor(wrapperClassName: String): WrapperResolution =
        when (val lookup = ReflectivePreviewWrapperFactory.forClass(wrapperClassName, classLoader)) {
            is FactoryLookup.Ready -> WrapperResolution.Wrapped(lookup.factory)
            is FactoryLookup.Invalid -> WrapperResolution.Failed(lookup.reason)
        }

    private fun mappingFailure(component: ShowkaseBrowserComponent, candidates: List<ComponentLocation>): String {
        val located = candidates.firstNotNullOfOrNull { candidate ->
            callFinder.existingHosts(candidate).takeIf { it.isNotEmpty() }?.let { candidate to it }
        }
        return if (located == null) {
            "no generated Showkase host could be found for componentKey '${component.componentKey}', " +
                "so its @PreviewWrapper could not be read. $REMEDIES"
        } else {
            "the generated Showkase host(s) ${located.second.joinToString()} contain no call to " +
                "function '${located.first.functionName}', so its @PreviewWrapper could not be read. $REMEDIES"
        }
    }

    private fun unsupportedClassFileFailure(exception: UnsupportedClassFileException) =
        "class '${exception.className}' has class file major version ${exception.majorVersion}, which the bundled ASM " +
            "cannot read. Add a newer 'org.ow2.asm:asm' test dependency (Gradle resolves to the highest version), " +
            DISABLE_REMEDY

    private companion object {

        const val DISABLE_REMEDY = "or turn wrapper support off with previewWrappers(PreviewWrappers.Disabled)."
        const val REMEDIES = "Point the framework at the preview with previewWrappers(PreviewWrappers.Enabled { … }), " + DISABLE_REMEDY
    }
}

private val frameworkClassLoader: ClassLoader
    get() = checkNotNull(BytecodePreviewWrapperResolver::class.java.classLoader)
