import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-Xexplicit-api=strict")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    compileOnly(files(libs::class.java.superclass.protectionDomain.codeSource.location))
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.dokka.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.gradle.maven.publish.gradlePlugin)
    compileOnly(libs.gradle.versions.gradlePlugin)

    testImplementation(platform(libs.junit5.bom))
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.runner.junit5)
}

gradlePlugin {
    plugins {
        plugin(
            dependency = libs.plugins.ackeecz.snapshots.dependency.updates,
            pluginName = "DependencyUpdatesConventionPlugin",
        )
        plugin(
            dependency = libs.plugins.ackeecz.snapshots.android.application,
            pluginName = "AndroidApplicationConventionPlugin",
        )
        plugin(
            dependency = libs.plugins.ackeecz.snapshots.android.library,
            pluginName = "AndroidLibraryConventionPlugin"
        )
        plugin(
            dependency = libs.plugins.ackeecz.snapshots.compose,
            pluginName = "ComposeConventionPlugin"
        )
        plugin(
            dependency = libs.plugins.ackeecz.snapshots.publishing,
            pluginName = "PublishingConventionPlugin"
        )
        plugin(
            dependency = libs.plugins.ackeecz.snapshots.preflightchecks,
            pluginName = "RegisterPreflightChecksPlugin",
        )
    }
}

fun NamedDomainObjectContainer<PluginDeclaration>.plugin(
    dependency: Provider<out PluginDependency>,
    pluginName: String,
) {
    val packageName = "io.github.ackeecz.snapshots"
    val pluginId = dependency.get().pluginId
    register(pluginId) {
        id = pluginId
        implementationClass = "$packageName.plugin.$pluginName"
    }
}
