package io.github.ackeecz.snapshots.plugin

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import java.util.Locale

internal class DependencyUpdatesConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            tasks.withType<DependencyUpdatesTask> {
                rejectVersionIf {
                    isNonStable(candidate.version) && !isNonStable(currentVersion)
                }
                outputFormatter = "json"
            }
        }
    }

    private fun isNonStable(version: String): Boolean {
        val containsStableKeyword = listOf("RELEASE", "FINAL", "GA").any {
            version.uppercase(Locale.US).contains(it)
        }
        val stableRegex = "^[0-9,.v-]+(-r)?$".toRegex()
        val isStable = containsStableKeyword || stableRegex.matches(version)
        return isStable.not()
    }
}
