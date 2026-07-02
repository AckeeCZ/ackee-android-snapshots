package io.github.ackeecz.snapshots.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.android.showkase.annotation.ShowkaseComposable
import io.github.ackeecz.snapshots.annotations.PreviewDevice
import io.github.ackeecz.snapshots.annotations.PreviewFontScale
import io.github.ackeecz.snapshots.annotations.PreviewSnapshotKind
import io.github.ackeecz.snapshots.annotations.PreviewUiMode
import io.github.ackeecz.snapshots.ui.components.ArticleCard
import io.github.ackeecz.snapshots.ui.components.Avatar
import io.github.ackeecz.snapshots.ui.components.PriceTag
import io.github.ackeecz.snapshots.ui.components.PromoBanner
import io.github.ackeecz.snapshots.ui.components.StatusBadge
import io.github.ackeecz.snapshots.ui.components.ThemedChip
import io.github.ackeecz.snapshots.ui.screens.LoginScreen
import io.github.ackeecz.snapshots.ui.screens.PricingScreen
import io.github.ackeecz.snapshots.ui.screens.ProfileScreen
import io.github.ackeecz.snapshots.ui.screens.SettingsScreen
import io.github.ackeecz.snapshots.ui.screens.WelcomeScreen

/**
 * All Showkase snapshot preview entry points, grouped by the test concern they feed (see [PreviewGroup]).
 * The `extraMetadata` on each preview is what the snapshot framework reads: a kind tag
 * ([PreviewSnapshotKind]), optional inline axis tokens ([PreviewFontScale] / [PreviewUiMode] /
 * [PreviewDevice]) and an optional profile reference ([PreviewProfile]).
 */

// region Default — baseline previews with no overrides (feed DefaultConfig + ExcludeCoupling).

@Preview
@ShowkaseComposable(name = "ArticleCard", group = PreviewGroup.Default, extraMetadata = [PreviewSnapshotKind.Component])
@Composable
fun ArticleCardPreview() {
    ArticleCard(
        title = "Snapshot testing",
        body = "Render Compose UI on the JVM with Paparazzi and guard it with committed golden images.",
    )
}

@Preview
@ShowkaseComposable(name = "WelcomeScreen", group = PreviewGroup.Default, extraMetadata = [PreviewSnapshotKind.Screen])
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen()
}

@Preview
@ShowkaseComposable(name = "SettingsScreen", group = PreviewGroup.Default, extraMetadata = [PreviewSnapshotKind.Screen])
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}

// endregion

// region ProfileOverride — a profile narrows one axis per preview, across components and screens.

@Preview
@ShowkaseComposable(
    name = "FontScaleComponent",
    group = PreviewGroup.ProfileOverride,
    extraMetadata = [PreviewSnapshotKind.Component, PreviewProfile.SingleFontScale],
)
@Composable
fun ProfileFontScaleComponentPreview() {
    Avatar(color = Color(0xFF6650A4))
}

@Preview
@ShowkaseComposable(
    name = "UiModeComponent",
    group = PreviewGroup.ProfileOverride,
    extraMetadata = [PreviewSnapshotKind.Component, PreviewProfile.SingleUiMode],
)
@Composable
fun ProfileUiModeComponentPreview() {
    ThemedChip(label = "Featured")
}

@Preview
@ShowkaseComposable(
    name = "DeviceScreen",
    group = PreviewGroup.ProfileOverride,
    extraMetadata = [PreviewSnapshotKind.Screen, PreviewProfile.SingleDevice],
)
@Composable
fun ProfileDeviceScreenPreview() {
    PricingScreen()
}

@Preview
@ShowkaseComposable(
    name = "FontScaleScreen",
    group = PreviewGroup.ProfileOverride,
    extraMetadata = [PreviewSnapshotKind.Screen, PreviewProfile.SingleFontScale],
)
@Composable
fun ProfileFontScaleScreenPreview() {
    ProfileScreen()
}

// endregion

// region InlineOverride — an inline token narrows one axis per preview, across components and screens.

@Preview
@ShowkaseComposable(
    name = "UiModeComponent",
    group = PreviewGroup.InlineOverride,
    extraMetadata = [PreviewSnapshotKind.Component, PreviewUiMode.Light],
)
@Composable
fun InlineUiModeComponentPreview() {
    StatusBadge(text = "Active")
}

@Preview
@ShowkaseComposable(
    name = "FontScaleComponent",
    group = PreviewGroup.InlineOverride,
    extraMetadata = [PreviewSnapshotKind.Component, PreviewFontScale.Normal],
)
@Composable
fun InlineFontScaleComponentPreview() {
    PromoBanner(text = "50% OFF today only")
}

@Preview
@ShowkaseComposable(
    name = "DeviceScreen",
    group = PreviewGroup.InlineOverride,
    extraMetadata = [PreviewSnapshotKind.Screen, PreviewDevice.Pixel6Portrait],
)
@Composable
fun InlineDeviceScreenPreview() {
    LoginScreen()
}

@Preview
@ShowkaseComposable(
    name = "FontScaleScreen",
    group = PreviewGroup.InlineOverride,
    extraMetadata = [PreviewSnapshotKind.Screen, PreviewFontScale.Normal],
)
@Composable
fun InlineFontScaleScreenPreview() {
    SettingsScreen()
}

// endregion

// region OverrideWidening — profile and inline overrides push an axis BEYOND the (narrow) class matrix.

@Preview
@ShowkaseComposable(
    name = "ProfileFontScale",
    group = PreviewGroup.OverrideWidening,
    extraMetadata = [PreviewSnapshotKind.Component, PreviewProfile.WidenFontScale],
)
@Composable
fun ProfileWidenFontScalePreview() {
    ArticleCard(title = "Widen", body = "Profile adds a font scale the class never declared.")
}

@Preview
@ShowkaseComposable(
    name = "ProfileUiMode",
    group = PreviewGroup.OverrideWidening,
    extraMetadata = [PreviewSnapshotKind.Component, PreviewProfile.WidenUiMode],
)
@Composable
fun ProfileWidenUiModePreview() {
    ThemedChip(label = "Widen UI mode")
}

@Preview
@ShowkaseComposable(
    name = "ProfileDevice",
    group = PreviewGroup.OverrideWidening,
    extraMetadata = [PreviewSnapshotKind.Screen, PreviewProfile.WidenDevice],
)
@Composable
fun ProfileWidenDeviceScreenPreview() {
    WelcomeScreen()
}

@Preview
@ShowkaseComposable(
    name = "InlineFontScale",
    group = PreviewGroup.OverrideWidening,
    extraMetadata = [PreviewSnapshotKind.Component, PreviewFontScale.Normal, PreviewFontScale.Large],
)
@Composable
fun InlineWidenFontScalePreview() {
    ArticleCard(title = "Widen", body = "Inline tokens add a font scale the class never declared.")
}

@Preview
@ShowkaseComposable(
    name = "InlineUiMode",
    group = PreviewGroup.OverrideWidening,
    extraMetadata = [PreviewSnapshotKind.Component, PreviewUiMode.Light, PreviewUiMode.Dark],
)
@Composable
fun InlineWidenUiModePreview() {
    ThemedChip(label = "Widen UI mode")
}

@Preview
@ShowkaseComposable(
    name = "InlineDevice",
    group = PreviewGroup.OverrideWidening,
    extraMetadata = [PreviewSnapshotKind.Screen, PreviewDevice.Pixel6Portrait, PreviewDevice.Nexus10Landscape],
)
@Composable
fun InlineWidenDeviceScreenPreview() {
    WelcomeScreen()
}

// endregion

// region Precedence — inline token beats the profile on the SAME axis (inline ?: profile ?: class).

@Preview
@ShowkaseComposable(
    name = "InlineBeatsProfile",
    group = PreviewGroup.Precedence,
    extraMetadata = [PreviewSnapshotKind.Component, PreviewProfile.SingleFontScale, PreviewFontScale.Large],
)
@Composable
fun InlineBeatsProfilePreview() {
    PriceTag(price = "$19.99")
}

// endregion

// region AllLevels — class sets all axes fully, a profile narrows font scale, an inline token narrows device.

@Preview
@ShowkaseComposable(
    name = "AllLevelsScreen",
    group = PreviewGroup.AllLevels,
    extraMetadata = [PreviewSnapshotKind.Screen, PreviewProfile.SingleFontScale, PreviewDevice.Pixel6Portrait],
)
@Composable
fun AllLevelsScreenPreview() {
    PricingScreen()
}

// endregion
