package io.github.ackeecz.snapshots.framework.wrapper

import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import java.lang.reflect.Modifier

/** Instantiates a validated `PreviewWrapperProvider` class reflectively, once per snapshot. */
internal class ReflectivePreviewWrapperFactory private constructor(private val type: Class<*>) : PreviewWrapperFactory {

    override val className: String get() = type.name

    override fun create(): PreviewWrapperProvider = type.getConstructor().newInstance() as PreviewWrapperProvider

    companion object {

        fun forClass(className: String, classLoader: ClassLoader): FactoryLookup {
            val type = loadClass(className, classLoader)
                ?: return invalid(className, "is not on the test classpath")
            val defect = defectOf(type)
            return if (defect == null) FactoryLookup.Ready(ReflectivePreviewWrapperFactory(type)) else invalid(className, defect)
        }

        private fun loadClass(className: String, classLoader: ClassLoader): Class<*>? =
            runCatching { Class.forName(className, false, classLoader) }.getOrNull()

        // Mirrors androidx's own wrapper instantiation: public constructors only, exactly one of them without parameters.
        private fun defectOf(type: Class<*>): String? = when {
            !PreviewWrapperProvider::class.java.isAssignableFrom(type) -> "does not implement PreviewWrapperProvider"
            Modifier.isAbstract(type.modifiers) -> "is abstract"
            type.constructors.count { it.parameterCount == 0 } != 1 -> "has no public zero-argument constructor"
            else -> null
        }

        private fun invalid(className: String, defect: String) = FactoryLookup.Invalid("its wrapper class '$className' $defect")
    }
}
