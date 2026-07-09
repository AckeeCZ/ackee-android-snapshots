package io.github.ackeecz.snapshots.plugin

import io.github.ackeecz.snapshots.util.Constants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

internal class KotlinConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.configure()
    }

    private fun Project.configure() {
        pluginManager.apply(libs.plugins.kotlin.android)
        tasks.withType<KotlinJvmCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(Constants.COMPILE_JVM_TARGET)
                allWarningsAsErrors.set(true)
                freeCompilerArgs.addAll(
                    "-Xopt-in=kotlin.RequiresOptIn",
                )
                // Auto opt-in for the library's own experimental marker, so internal/sample code can
                // use experimental APIs without per-site @OptIn and without tripping allWarningsAsErrors.
                // Consumers still opt in explicitly. The marker lives in :annotations, which every
                // module depends on, so this FQN resolves everywhere the convention is applied.
                optIn.add("io.github.ackeecz.snapshots.annotations.ExperimentalSnapshotsApi")
            }
        }
    }
}
