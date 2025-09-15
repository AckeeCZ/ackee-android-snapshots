import io.github.ackeecz.snapshots.verification.task.VerifyBomVersionTask

plugins {
    `java-platform`
    alias(libs.plugins.ackeecz.snapshots.publishing)
}

dependencies {
    constraints {
        api(projects.annotations)
        api(projects.framework)
        api(projects.paparazzi)
    }
}

VerifyBomVersionTask.registerFor(project)
