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

    compileOnly(libs.paparazzi)
    implementation(libs.kotest.framework.api)
    implementation(libs.showkase.core)
    compileOnly(platform(libs.androidx.compose.bom))
    compileOnly(libs.androidx.ui)
}
