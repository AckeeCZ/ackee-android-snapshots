package io.github.ackeecz.snapshots.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

internal class AndroidLibraryConventionPlugin : Plugin<Project> {

    private val androidConventionPlugin = AndroidConventionPlugin()
    private val detektConventionPlugin = DetektConventionPlugin()
    private val kotlinConventionPlugin = KotlinConventionPlugin()

    override fun apply(target: Project) {
        target.configure()
        androidConventionPlugin.apply(target)
        detektConventionPlugin.apply(target)
        kotlinConventionPlugin.apply(target)
    }

    private fun Project.configure() {
        pluginManager.apply(libs.plugins.android.library)
    }
}
