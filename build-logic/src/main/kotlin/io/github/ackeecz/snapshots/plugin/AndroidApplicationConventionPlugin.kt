package io.github.ackeecz.snapshots.plugin

import com.android.build.api.dsl.ApplicationExtension
import io.github.ackeecz.snapshots.util.Constants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal class AndroidApplicationConventionPlugin : Plugin<Project> {

    private val androidConventionPlugin = AndroidConventionPlugin()
    private val kotlinConventionPlugin = KotlinConventionPlugin()

    override fun apply(target: Project) {
        target.configure()
        androidConventionPlugin.apply(target)
        kotlinConventionPlugin.apply(target)
    }

    private fun Project.configure() {
        pluginManager.apply(libs.plugins.android.application)
        // targetSdk is only meaningful for apps (and tests) in the new AGP DSL, so it lives here
        // rather than in the shared AndroidConventionPlugin used by libraries too.
        extensions.configure<ApplicationExtension> {
            defaultConfig {
                targetSdk = Constants.TARGET_SDK
            }
        }
    }
}
