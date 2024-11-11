package io.github.ackeecz.snapshots.plugin

import io.github.ackeecz.snapshots.androidCommon
import io.github.ackeecz.snapshots.apply
import io.github.ackeecz.snapshots.debugImplementation
import io.github.ackeecz.snapshots.implementation
import io.github.ackeecz.snapshots.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class ComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.configure()
    }

    private fun Project.configure() {
        configureCompose()
        addDependencies()
    }

    private fun Project.configureCompose() {
        pluginManager.apply(libs.plugins.kotlin.compose)
        androidCommon {
            buildFeatures {
                compose = true
            }
        }
    }

    private fun Project.addDependencies() {
        dependencies {
            implementation(platform(libs.androidx.compose.bom))
            implementation(libs.androidx.ui)
            debugImplementation(libs.androidx.ui.tooling)
            implementation(libs.androidx.ui.tooling.preview)
            implementation(libs.androidx.material3)
            implementation(libs.androidx.activity.compose)
        }
    }
}
