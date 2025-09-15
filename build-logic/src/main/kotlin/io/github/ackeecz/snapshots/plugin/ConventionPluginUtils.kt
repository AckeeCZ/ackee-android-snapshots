package io.github.ackeecz.snapshots.plugin

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the
import org.gradle.plugin.use.PluginDependency
import java.util.Optional

internal val Project.libs get() = the<LibrariesForLibs>()

internal fun Project.androidCommon(action: CommonExtension<*, *, *, *, *, *>.() -> Unit) {
    extensions.configure(CommonExtension::class, action)
}

internal fun Project.androidBase(action: BaseExtension.() -> Unit) {
    extensions.configure(BaseExtension::class, action)
}

internal fun PluginManager.apply(plugin: Provider<PluginDependency>) {
    apply(plugin.get().pluginId)
}

internal fun DependencyHandlerScope.implementation(provider: Optional<Provider<MinimalExternalModuleDependency>>) {
    implementation(provider.get())
}

internal fun DependencyHandlerScope.implementation(dependencyNotation: Any) {
    add("implementation", dependencyNotation)
}

internal fun DependencyHandlerScope.compileOnly(dependencyNotation: Any) {
    add("compileOnly", dependencyNotation)
}

internal fun DependencyHandlerScope.debugImplementation(dependencyNotation: Any) {
    add("debugImplementation", dependencyNotation)
}

internal fun DependencyHandlerScope.testImplementation(provider: Optional<Provider<MinimalExternalModuleDependency>>) {
    testImplementationInternal(provider.get())
}

private fun DependencyHandlerScope.testImplementationInternal(dependency: Any) {
    add("testImplementation", dependency)
}

internal fun DependencyHandlerScope.testImplementation(provider: Provider<MinimalExternalModuleDependency>) {
    testImplementationInternal(provider.get())
}

internal fun DependencyHandlerScope.testImplementation(project: Project) {
    testImplementationInternal(project)
}

internal fun DependencyHandlerScope.testRuntimeOnly(provider: Optional<Provider<MinimalExternalModuleDependency>>) {
    add("testRuntimeOnly", provider.get())
}

internal fun DependencyHandlerScope.ksp(provider: Optional<Provider<MinimalExternalModuleDependency>>) {
    add("ksp", provider.get())
}
