package io.github.ackeecz.snapshots.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationExtension
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

internal class AndroidLibraryConventionPlugin : Plugin<Project> {

    private val androidConventionPlugin = AndroidConventionPlugin()
    private val detektConventionPlugin = DetektConventionPlugin()
    private val kotlinConventionPlugin = KotlinConventionPlugin()

    override fun apply(target: Project) {
        target.configure()
        androidConventionPlugin.apply(target)
        detektConventionPlugin.apply(target)
        kotlinConventionPlugin.apply(target)
        // Run after KotlinConventionPlugin applies kotlin-android, which is what registers the
        // abiValidation extension.
        target.configureAbiValidation()
    }

    private fun Project.configure() {
        pluginManager.apply(libs.plugins.android.library)
    }

    // Public API/ABI is tracked with the Kotlin Gradle Plugin's ABI validation. It provides the
    // checkLegacyAbi / updateLegacyAbi tasks and keeps the committed <module>/api/<module>.api format.
    // This is wired by the kotlin-android plugin (applied by KotlinConventionPlugin); AGP's built-in
    // Kotlin does not register it, which is why built-in Kotlin is currently disabled (see the TODOs in
    // gradle.properties and gradle/libs.versions.toml).
    @OptIn(ExperimentalAbiValidation::class)
    private fun Project.configureAbiValidation() {
        val kotlin = extensions.getByName("kotlin") as ExtensionAware
        kotlin.extensions.configure<AbiValidationExtension> {
            enabled.set(true)
        }
    }
}
