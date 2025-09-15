package io.github.ackeecz.snapshots.plugin

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import io.github.ackeecz.snapshots.properties.LibraryProperties
import io.github.ackeecz.snapshots.verification.task.CheckIfUpdateNeededSinceCurrentTagTask
import io.github.ackeecz.snapshots.verification.task.VerifyPublishingTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

internal class PublishingConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.configure()
    }

    private fun Project.configure() {
        // com.vanniktech.maven.publish plugin can detect and use applied Dokka plugin automatically
        pluginManager.apply(libs.plugins.dokka)
        pluginManager.apply(libs.plugins.gradle.maven.publish)

        val libraryProperties = LibraryProperties(project)
        val artifactProperties = libraryProperties.getArtifactProperties()

        group = libraryProperties.groupId
        version = artifactProperties.version

        mavenPublishing {

            coordinates(artifactId = artifactProperties.id)

            pom {
                name.set(artifactProperties.pomName)
                description.set(artifactProperties.pomDescription)
                inceptionYear.set(artifactProperties.pomYear)
                url.set(libraryProperties.pomUrl)
                licenses {
                    license {
                        name.set(libraryProperties.pomLicenceName)
                        url.set(libraryProperties.pomLicenceUrl)
                        distribution.set(libraryProperties.pomLicenceUrl)
                    }
                }
                developers {
                    developer {
                        id.set(libraryProperties.pomDeveloperId)
                        name.set(libraryProperties.pomDeveloperName)
                        url.set(libraryProperties.pomDeveloperEmail)
                    }
                }
                scm {
                    url.set(libraryProperties.pomScmUrl)
                    connection.set(libraryProperties.pomScmConnection)
                    developerConnection.set(libraryProperties.pomScmDeveloperConnection)
                }
            }

            signAllPublications()
            publishToMavenCentral()
        }

        configurePublishingSkipping(
            groupId = libraryProperties.groupId,
            artifactId = artifactProperties.id,
            version = artifactProperties.version,
        )
        CheckIfUpdateNeededSinceCurrentTagTask.registerFor(project)
        VerifyPublishingTask.registerFor(project)
    }
}

/**
 * Configures publishing skipping, if the artifact with the given [version] already exists in
 * the Maven Central repository. This allows us to conveniently run a single publishing task
 * everytime, without worrying about what actually can be published.
 */
private fun Project.configurePublishingSkipping(groupId: String, artifactId: String, version: String) {
    tasks.withType<PublishToMavenRepository>().configureEach {
        onlyIf {
            val groupIdPath = groupId.replace('.', '/')
            val url = "https://repo1.maven.org/maven2/$groupIdPath/$artifactId/$version"
            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build()
            val client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            val httpStatusCode = response.statusCode()
            // We fail the task if we get unexpected error or redirect, except 404 which is expected.
            // Unexpected status codes might potentially lead to publishing issues, because we can't be
            // sure if the artifact is already published or not, so it is better to fail the task.
            val artifactPublishedStatusCodes = 200..299
            val artifactNotPublishedStatusCode = 404
            val isUnexpectedStatusCode = httpStatusCode !in artifactPublishedStatusCodes &&
                httpStatusCode != artifactNotPublishedStatusCode
            if (isUnexpectedStatusCode) {
                error("Unexpected HTTP status code $httpStatusCode for URL: $url")
            }
            return@onlyIf (httpStatusCode == artifactNotPublishedStatusCode)
        }
    }
}

private fun Project.mavenPublishing(action: MavenPublishBaseExtension.() -> Unit) {
    extensions.configure(MavenPublishBaseExtension::class, action)
}
