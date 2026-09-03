package io.github.ackeecz.snapshots.framework.wrapper

import com.airbnb.android.showkase.models.ShowkaseBrowserComponent
import io.github.ackeecz.snapshots.framework.wrapper.fixtures.FixtureObject

/** The class loader holding the compiled Showkase-like fixtures. */
internal val fixtureClassLoader: ClassLoader = checkNotNull(FixtureObject::class.java.classLoader)

/** Reads the compiled fixtures' class bytes off the real test classpath. */
internal fun fixtureBytesSource() = ClassLoaderBytesSource(fixtureClassLoader)

internal fun fixtureComponent(
    key: String,
    group: String = "Cards",
    name: String = "Card",
    styleName: String? = null,
) = ShowkaseBrowserComponent(
    componentKey = key,
    group = group,
    componentName = name,
    componentKDoc = "",
    component = {},
    styleName = styleName,
    extraMetadata = emptyList(),
)
