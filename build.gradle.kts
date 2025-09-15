// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.ackeecz.snapshots.preflightchecks) apply true
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.binaryCompatibilityValidator) apply true
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.gradle.maven.publish) apply false
}

apiValidation {
    ignoredProjects.addAll(
        listOf(
            "sample",
        )
    )
}
