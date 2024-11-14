package io.github.ackeecz.snapshots.plugin

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import groovy.util.Node
import groovy.util.NodeList
import io.github.ackeecz.snapshots.apply
import io.github.ackeecz.snapshots.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.util.Properties

class PublishingConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.configurePublishing()
    }

    private fun Project.configurePublishing() {
        pluginManager.apply(libs.plugins.gradle.maven.publish)
        extensions.configure<MavenPublishBaseExtension> {
            val commonLibProperties = Properties().apply { load(file("${rootDir.absolutePath}/lib.properties").inputStream()) }
            val moduleLibProperties = Properties().apply { load(file("${projectDir.absolutePath}/lib.properties").inputStream()) }
            // include versions of compose and paparazzi in particular modules
            val versionSuffix = when (name) {
                "framework" -> libs.versions.composeBom.get()
                "paparazzi" -> libs.versions.paparazzi.get()
                else -> null
            }
            val groupId = commonLibProperties.getProperty("GROUP")
            val artifactId = "${commonLibProperties.getProperty("ARTIFACT_BASE_NAME")}-$name"
            val version = buildString {
                append(commonLibProperties.getProperty("VERSION_NAME"))
                if (versionSuffix != null) {
                    append("-$versionSuffix")
                }
            }
            println("Publishing $groupId:$artifactId:$version")
            coordinates(
                groupId = groupId,
                artifactId = artifactId,
                version = version
            )

            pom {
                name.set(commonLibProperties.getProperty("POM_NAME"))
                description.set(moduleLibProperties.getProperty("POM_DESCRIPTION"))

                inceptionYear.set(commonLibProperties.getProperty("POM_YEAR"))
                url.set(commonLibProperties.getProperty("POM_URL"))
                licenses {
                    license {
                        name.set(commonLibProperties.getProperty("POM_LICENCE_NAME"))
                        url.set(commonLibProperties.getProperty("POM_LICENCE_URL"))
                        distribution.set(commonLibProperties.getProperty("POM_LICENCE_URL"))
                    }
                }
                developers {
                    developer {
                        id.set(commonLibProperties.getProperty("POM_DEVELOPER_ID"))
                        name.set(commonLibProperties.getProperty("POM_DEVELOPER_NAME"))
                        url.set(commonLibProperties.getProperty("POM_DEVELOPER_EMAIL"))
                    }
                }
                scm {
                    url.set(commonLibProperties.getProperty("POM_SCM_URL"))
                    connection.set(commonLibProperties.getProperty("POM_SCM_CONNECTION"))
                    developerConnection.set(commonLibProperties.getProperty("POM_SCM_DEVELOPER_CONNECTION"))
                }
                // exclude compose, paparazzi and kotlin since these are very unstable when used together with incorrect
                // version so move this responsibility to callers
                withXml {
                    // remove kotlin, paparazzi and compose from dependencies
                    val dependencies = ((asNode().get("dependencies") as NodeList)[0] as Node).children() as NodeList
                    dependencies.removeAll { dep ->
                        val node = dep as Node
                        val groupId = (node.children() as List<Node>).find {
                            it.name().toString().contains("groupId")
                        }?.text()
                        groupId?.contains("androidx") == true ||
                            groupId == "app.cash.paparazzi" ||
                            groupId == "org.jetbrains.kotlin"
                    }
                    // remove compose bom from dependency management
                    val dependencyManagementDependenciesNode =
                        ((asNode().get("dependencyManagement") as? NodeList)?.getOrNull(0) as? Node)?.children() as? NodeList
                    val dependencyManagementDependencies = (dependencyManagementDependenciesNode?.getOrNull(0) as? Node)?.children()
                    dependencyManagementDependencies?.removeAll { dep ->
                        val node = dep as Node
                        val groupId = (node.children() as List<Node>).find {
                            it.name().toString().contains("groupId")
                        }?.text()
                        groupId?.contains("androidx") == true
                    }
                }
            }

//            signAllPublications()
            publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
        }
    }
}
