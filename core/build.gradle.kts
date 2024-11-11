plugins {
    alias(libs.plugins.ackeecz.snapshots.android.library)
    alias(libs.plugins.ackeecz.snapshots.compose)
    alias(libs.plugins.ackeecz.snapshots.publishing)
}


android {
    namespace = "io.github.ackeecz.snapshots.core"
}

dependencies {
    implementation(libs.showkase.core)
    implementation(libs.kotest.framework.api)
}
