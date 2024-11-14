plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.gradle.maven.publish.gradlePlugin)
}

gradlePlugin {
    plugins {
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
