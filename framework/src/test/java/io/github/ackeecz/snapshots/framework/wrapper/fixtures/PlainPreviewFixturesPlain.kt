@file:Suppress("TopLevelPropertyNaming")

package io.github.ackeecz.snapshots.framework.wrapper.fixtures

import androidx.compose.runtime.Composable
import com.airbnb.android.showkase.models.ShowkaseBrowserComponent

internal val PlainPreviewFixturesPlain: ShowkaseBrowserComponent = ShowkaseBrowserComponent(
    group = "Fixtures",
    componentName = "Plain",
    componentKDoc = "",
    componentKey = "io.github.ackeecz.snapshots.framework.wrapper.fixtures_PlainPreview_null_Fixtures_Plain_0_null",
    isDefaultStyle = false,
    extraMetadata = listOf("component"),
    component = @Composable { PlainPreview() },
)
