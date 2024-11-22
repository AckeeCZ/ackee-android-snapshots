package io.github.ackeecz.snapshots.plugin

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
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
            }

            signAllPublications()
            publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
        }
    }
}
