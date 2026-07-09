plugins {
    alias(libs.plugins.ackeecz.snapshots.android.library)
    alias(libs.plugins.ackeecz.snapshots.publishing)
    alias(libs.plugins.ackeecz.snapshots.compose)
}


android {
    namespace = "io.github.ackeecz.snapshots.framework"

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    implementation(projects.annotations)

    implementation(libs.showkase.core)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)

    implementation(libs.kotest.runner.junit5)

    testImplementation(platform(libs.junit5.bom))
    testImplementation(libs.kotest.assertions.core)
}
