import io.github.ackeecz.snapshots.debugImplementation
import io.github.ackeecz.snapshots.implementation
import io.github.ackeecz.snapshots.libs

plugins {
    alias(libs.plugins.ackeecz.snapshots.android.application)
    alias(libs.plugins.ackeecz.snapshots.compose)

    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.paparazzi)
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

    testImplementation(projects.framework)
    testImplementation(projects.paparazzi)
    implementation(projects.annotations)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.showkase.core)
    ksp(libs.showkase.processor)

    testImplementation(platform(libs.junit5.bom))
    testImplementation(libs.junit)
    // Runner for JUnit 4 tests
    testRuntimeOnly(libs.junit.vintage.engine)
    testRuntimeOnly(libs.kotest.runnerJunit5)
    testImplementation(libs.kotest.framework.api)
    testImplementation(libs.junit)
}
