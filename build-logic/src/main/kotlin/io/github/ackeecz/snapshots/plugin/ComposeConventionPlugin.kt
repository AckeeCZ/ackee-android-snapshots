package io.github.ackeecz.snapshots.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

internal class ComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.configure()
    }

    private fun Project.configure() {
        configureCompose()
    }

    private fun Project.configureCompose() {
        pluginManager.apply(libs.plugins.kotlin.compose)
        androidCommon {
            buildFeatures {
                compose = true
            }
        }
    }
}
