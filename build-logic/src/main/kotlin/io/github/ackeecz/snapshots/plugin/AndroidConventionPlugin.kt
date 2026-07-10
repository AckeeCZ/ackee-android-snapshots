package io.github.ackeecz.snapshots.plugin

import com.android.build.api.dsl.CommonExtension
import io.github.ackeecz.snapshots.util.Constants
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class AndroidConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.configure()
    }

    private fun Project.configure() {
        androidCommon {
            configureSdkVersions()
            configureCompileOptions()
            configurePackagingOptions()
        }
    }

    private fun CommonExtension.configureSdkVersions() {
        compileSdk = Constants.COMPILE_SDK
        defaultConfig.apply {
            minSdk = Constants.MIN_SDK
        }
    }

    private fun CommonExtension.configureCompileOptions() {
        compileOptions.apply {
            sourceCompatibility = Constants.COMPILE_JAVA_VERSION
            targetCompatibility = Constants.COMPILE_JAVA_VERSION
        }
    }

    private fun CommonExtension.configurePackagingOptions() {
        packaging.resources.apply {
            excludes += listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/NOTICE",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE.txt",
                "META-INF/atomicfu.kotlin_module",
                "AndroidManifest.xml",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/licenses/ASM"
            )
            // For byte-buddy from coroutines
            pickFirsts += setOf(
                "win32-x86-64/attach_hotspot_windows.dll",
                "win32-x86/attach_hotspot_windows.dll",
            )
        }
    }
}
