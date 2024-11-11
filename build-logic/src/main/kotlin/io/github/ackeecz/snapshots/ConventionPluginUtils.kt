package io.github.ackeecz.snapshots

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the
import org.gradle.plugin.use.PluginDependency
import java.util.Optional

val Project.libs get() = the<org.gradle.accessors.dm.LibrariesForLibs>()

fun Project.androidCommon(action: CommonExtension<*, *, *, *, *, *>.() -> Unit) {
    extensions.configure(CommonExtension::class, action)
}

fun Project.androidBase(action: BaseExtension.() -> Unit) {
    extensions.configure(BaseExtension::class, action)
}

fun PluginManager.apply(plugin: Provider<PluginDependency>) {
    apply(plugin.get().pluginId)
}

fun DependencyHandlerScope.implementation(provider: Optional<Provider<MinimalExternalModuleDependency>>) {
    implementation(provider.get())
}

fun DependencyHandlerScope.implementation(dependencyNotation: Any) {
    add("implementation", dependencyNotation)
}

fun DependencyHandlerScope.debugImplementation(dependencyNotation: Any) {
    add("debugImplementation", dependencyNotation)
}

fun DependencyHandlerScope.testImplementation(provider: Optional<Provider<MinimalExternalModuleDependency>>) {
    testImplementationInternal(provider.get())
}

private fun DependencyHandlerScope.testImplementationInternal(dependency: Any) {
    add("testImplementation", dependency)
}

fun DependencyHandlerScope.testImplementation(provider: Provider<MinimalExternalModuleDependency>) {
    testImplementationInternal(provider.get())
}

fun DependencyHandlerScope.testImplementation(project: Project) {
    testImplementationInternal(project)
}

fun DependencyHandlerScope.testRuntimeOnly(provider: Optional<Provider<MinimalExternalModuleDependency>>) {
    add("testRuntimeOnly", provider.get())
}

fun DependencyHandlerScope.ksp(provider: Optional<Provider<MinimalExternalModuleDependency>>) {
    add("ksp", provider.get())
}
