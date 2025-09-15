package io.github.ackeecz.snapshots.plugin

import com.android.build.gradle.BaseExtension
import io.github.ackeecz.snapshots.util.Constants
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class AndroidConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.configure()
    }

    private fun Project.configure() {
        androidBase {
            configureSdkVersions()
            configureCompileOptions()
            configurePackagingOptions()
        }
    }

    private fun BaseExtension.configureSdkVersions() {
        compileSdkVersion(Constants.COMPILE_SDK)
        defaultConfig {
            minSdk = Constants.MIN_SDK
            targetSdk = Constants.TARGET_SDK
        }
    }

    private fun BaseExtension.configureCompileOptions() {
        compileOptions {
            sourceCompatibility = Constants.COMPILE_JAVA_VERSION
            targetCompatibility = Constants.COMPILE_JAVA_VERSION
        }
    }

    private fun BaseExtension.configurePackagingOptions() {
        packagingOptions {
            resources {
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
}
