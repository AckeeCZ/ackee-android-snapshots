plugins {
    alias(libs.plugins.ackeecz.snapshots.android.library)
    alias(libs.plugins.ackeecz.snapshots.compose)
    alias(libs.plugins.ackeecz.snapshots.publishing)
}

android {
    namespace = "io.github.ackeecz.snapshots.paparazzi"
}

dependencies {
    implementation(projects.core)

    implementation(libs.paparazzi)
    implementation(libs.kotest.framework.api)
    implementation(libs.showkase.core)
}
