import io.github.ackeecz.snapshots.compileOnly
import io.github.ackeecz.snapshots.implementation
import io.github.ackeecz.snapshots.libs

plugins {
    alias(libs.plugins.ackeecz.snapshots.android.library)
    alias(libs.plugins.ackeecz.snapshots.publishing)
    alias(libs.plugins.ackeecz.snapshots.compose)
}


android {
    namespace = "io.github.ackeecz.snapshots.framework"
}

dependencies {
    implementation(libs.showkase.core)

    compileOnly(platform(libs.compose.bom))
    compileOnly(libs.compose.ui)

    implementation(libs.kotest.runner.junit5)
}
