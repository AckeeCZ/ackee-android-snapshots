package io.github.ackeecz.snapshots.plugin

import io.github.ackeecz.snapshots.Constants
import io.github.ackeecz.snapshots.apply
import io.github.ackeecz.snapshots.libs
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
            }
        }
    }
}
