package io.github.ackeecz.snapshots.plugin

import io.github.ackeecz.snapshots.util.Constants
import io.github.ackeecz.snapshots.verification.task.VerifyBomVersionTask
import io.github.ackeecz.snapshots.verification.task.VerifyPublishingTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.process.ExecOperations
import javax.inject.Inject

internal abstract class RegisterPreflightChecksPlugin : Plugin<Project> {

    @get:Inject
    abstract val execOperations: ExecOperations

    override fun apply(target: Project) {
        RegisterPreMergeRequestCheck(target).invoke()
        RegisterPrePublishCheck(target).invoke()
    }

    private fun Task.dependsOnTaskFromProjects(taskName: String, projects: Collection<Project>) {
        getTaskFromProjects(taskName, projects).forEach { dependsOn(it) }
    }

    private fun getTaskFromProjects(taskName: String, projects: Collection<Project>): List<Task> {
        return projects
            .flatMap { subProject ->
                subProject.tasks.filter { it.name == taskName }
            }
            .ifEmpty { throw GradleException("Task $taskName not found in any project") }
    }

    private inner class RegisterPreMergeRequestCheck(private val currentProject: Project) {

        operator fun invoke() {
            // Changes to this task must be synchronized with the basic-preflight-check/action.yml action
            // to run the same checks on the CI as well
            currentProject.tasks.register(PRE_MERGE_REQUEST_CHECK_TASK_NAME) {
                group = Constants.ACKEE_TASKS_GROUP
                description = "Performs basic verifications before making a MR like running Detekt, tests, etc."

                dependsOnDetekt()
                dependsOnAssemble()
                dependsOnLibraryTests()
                dependsOnSampleAppSnapshotTests()
                dependsOnBinaryCompatibilityCheck()
                dependsOnBuildLogicTests()
            }
        }

        private fun Task.dependsOnDetekt() {
            dependsOnTaskFromProjects(
                taskName = "detekt",
                projects = currentProject.subprojects,
            )
        }

        private fun Task.dependsOnAssemble() {
            dependsOnTaskFromProjects(
                taskName = "assembleRelease",
                projects = currentProject.subprojects,
            )
        }

        private fun Task.dependsOnLibraryTests() {
            dependsOnTaskFromProjects(
                taskName = "testDebugUnitTest",
                projects = currentProject.subprojects.filterNot { it.name == SAMPLE_APP_NAME },
            )
        }

        private fun Task.dependsOnSampleAppSnapshotTests() {
            dependsOnTaskFromProjects(
                taskName = "verifyPaparazziDebug",
                projects = currentProject.subprojects.filter { it.name == SAMPLE_APP_NAME },
            )
        }

        private fun Task.dependsOnBinaryCompatibilityCheck() {
            dependsOnTaskFromProjects(
                taskName = "apiCheck",
                projects = currentProject.subprojects,
            )
        }

        private fun Task.dependsOnBuildLogicTests() {
            dependsOn(
                currentProject.gradle
                    .includedBuild(BUILD_LOGIC_ROOT_PROJECT_NAME)
                    .task(":test")
            )
        }
    }

    private inner class RegisterPrePublishCheck(private val currentProject: Project) {

        operator fun invoke() {
            // Changes to this task must be synchronized with the deploy.yml workflow
            // to run the same checks on the CI as well
            currentProject.tasks.register(PRE_PUBLISH_CHECK_TASK_NAME) {
                group = Constants.ACKEE_TASKS_GROUP
                description = "Performs all necessary verifications before publishing new artifacts versions"

                dependsOn(PRE_MERGE_REQUEST_CHECK_TASK_NAME)
                dependsOnVerifyPublishing()
                dependsOnVerifyBomVersion()
            }
        }

        private fun Task.dependsOnVerifyPublishing() {
            dependsOnTaskFromProjects(
                taskName = VerifyPublishingTask.NAME,
                projects = currentProject.subprojects,
            )
        }

        private fun Task.dependsOnVerifyBomVersion() {
            dependsOnTaskFromProjects(
                taskName = VerifyBomVersionTask.NAME,
                projects = currentProject.subprojects,
            )
        }
    }

    companion object {

        private const val PRE_MERGE_REQUEST_CHECK_TASK_NAME = "preMergeRequestCheck"
        private const val PRE_PUBLISH_CHECK_TASK_NAME = "prePublishCheck"

        private const val SAMPLE_APP_NAME = "sample"
        private const val BUILD_LOGIC_ROOT_PROJECT_NAME = "build-logic"
    }
}
