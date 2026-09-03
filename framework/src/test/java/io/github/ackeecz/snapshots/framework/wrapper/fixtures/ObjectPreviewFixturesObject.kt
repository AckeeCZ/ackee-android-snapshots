@file:Suppress("TopLevelPropertyNaming")

package io.github.ackeecz.snapshots.framework.wrapper.fixtures

import androidx.compose.runtime.Composable
import com.airbnb.android.showkase.models.ShowkaseBrowserComponent

// The key is assembled from a constant only to stay inside the 150 character line limit; the folded value is
// byte-for-byte the literal Showkase generates.
private const val ENCLOSING = "io.github.ackeecz.snapshots.framework.wrapper.fixtures.FixtureObject"

internal val ObjectPreviewFixturesObject: ShowkaseBrowserComponent = ShowkaseBrowserComponent(
    group = "Fixtures",
    componentName = "Object",
    componentKDoc = "",
    componentKey = "${ENCLOSING}_ObjectPreview_${ENCLOSING}_Fixtures_Object_0_null",
    isDefaultStyle = false,
    extraMetadata = listOf("component"),
    component = @Composable { FixtureObject.ObjectPreview() },
)
