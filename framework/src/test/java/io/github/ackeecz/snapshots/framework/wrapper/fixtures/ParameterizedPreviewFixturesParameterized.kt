@file:Suppress("TopLevelPropertyNaming")

package io.github.ackeecz.snapshots.framework.wrapper.fixtures

import androidx.compose.runtime.Composable
import com.airbnb.android.showkase.models.ShowkaseBrowserComponent

internal val ParameterizedPreviewFixturesParameterized: List<ShowkaseBrowserComponent> =
    FixtureLabelProvider().values.iterator().asSequence().mapIndexed { index, previewParam ->
        ShowkaseBrowserComponent(
            group = "Fixtures",
            componentName = "Parameterized",
            componentKDoc = "",
            componentKey = "io.github.ackeecz.snapshots.framework.wrapper.fixtures_ParameterizedPreview_null_Fixtures_Parameterized_0_null_$index",
            isDefaultStyle = false,
            extraMetadata = listOf("component"),
            component = @Composable { ParameterizedPreview(label = previewParam) },
        )
    }.toList()
