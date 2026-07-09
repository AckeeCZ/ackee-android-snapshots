plugins {
    alias(libs.plugins.ackeecz.snapshots.android.library)
    alias(libs.plugins.ackeecz.snapshots.compose)
    alias(libs.plugins.ackeecz.snapshots.publishing)
}

android {
    namespace = "io.github.ackeecz.snapshots.paparazzi"

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    implementation(projects.framework)
    // Needed so the module-wide @ExperimentalSnapshotsApi opt-in resolves (implementation deps are
    // not transitive), and to opt in to the experimental SnapshotEngine that PaparazziEngine implements.
    implementation(projects.annotations)

    implementation(libs.kotest.runner.junit5)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)

    implementation(libs.paparazzi)
    implementation(libs.showkase.core)

    testImplementation(platform(libs.junit5.bom))
    testImplementation(libs.kotest.assertions.core)
}
