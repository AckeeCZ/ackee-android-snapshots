import io.github.ackeecz.snapshots.implementation
import io.github.ackeecz.snapshots.libs

plugins {
    alias(libs.plugins.ackeecz.snapshots.android.library)
    alias(libs.plugins.ackeecz.snapshots.compose)
    alias(libs.plugins.ackeecz.snapshots.publishing)
}

android {
    namespace = "io.github.ackeecz.snapshots.paparazzi"
}

dependencies {
    implementation(projects.framework)

    implementation(libs.kotest.runner.junit5)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)

    implementation(libs.paparazzi)
    implementation(libs.showkase.core)
}
