package io.github.ackeecz.snapshots.util

internal sealed interface PublishableProject {

    val projectName: String

    object Bom : PublishableProject {

        override val projectName = "bom"
    }

    object Annotations : PublishableProject {

        override val projectName = "annotations"
    }

    object Framework : PublishableProject {

        override val projectName = "framework"
    }

    object Paparazzi : PublishableProject {

        override val projectName = "paparazzi"
    }
}
