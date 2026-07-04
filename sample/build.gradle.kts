import org.gradle.api.tasks.testing.Test

plugins {
    alias(libs.plugins.ackeecz.snapshots.android.application)
    alias(libs.plugins.ackeecz.snapshots.compose)

    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.paparazzi)
}

// Paparazzi 2.0.0-alpha04 relies on Gradle internals for HTML test reports that break on Gradle 9
// (required by AGP 9). Disable HTML reports for the Paparazzi test tasks as a workaround.
// https://github.com/cashapp/paparazzi/issues/2111
tasks.withType<Test>().configureEach {
    reports.html.required = false
}

android {
    namespace = "io.github.ackeecz.snapshots"

    defaultConfig {
        applicationId = "io.github.ackeecz.snapshots.sample"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {

    implementation(projects.annotations)
    testImplementation(projects.framework)
    testImplementation(projects.paparazzi)

    implementation(libs.showkase.core)
    ksp(libs.showkase.processor)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.material3)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)

    testImplementation(platform(libs.junit5.bom))
    testImplementation(libs.kotest.runner.junit5)
}
