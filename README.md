![ackee|Ackee Android Snapshots](imgs/cover.png)

# Ackee Android Snapshots

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.ackeecz/snapshots-bom)](https://central.sonatype.com/artifact/io.github.ackeecz/snapshots-bom)

## Overview

Ackee Android Snapshots is an opinionated framework designed for snapshot testing in Android projects, particularly focusing on Jetpack Compose UI
components. It leverages Paparazzi for rendering snapshots, Showkase for component discovery, and Kotest as the testing framework.

You tag each Compose preview with a snapshot **kind**, then describe the whole snapshot matrix — kinds, devices, UI modes and font scales — in a single
**config DSL** on one test class. The framework expands that config (plus any per-preview exceptions) into the exact set of golden images and renders
each one through the engine. A single test class can therefore span many devices, UI modes and font scales at once.

## Architecture

The framework is designed with extensibility in mind, using a modular architecture:

- `annotations` module contains the preview tags read from `@ShowkaseComposable` `extraMetadata` (`PreviewSnapshotKind` plus the per-preview override
  tokens `PreviewFontScale` / `PreviewUiMode` / `PreviewDevice`). Used in the production source set (to tag previews) and on the test classpath.
- `framework` module defines the config DSL, the resolver that turns config + previews into the exact set of snapshot variants, the `SnapshotEngine`
  interface and the `AckeeSnapshotTests` Kotest base. Depends on `annotations`.
- `paparazzi` module provides the `SnapshotEngine` implementation backed by Paparazzi (`PaparazziSnapshotTests`).
- Additional snapshot engine implementations can be added by implementing the `SnapshotEngine` interface.

## Features

- Automated snapshot testing for Jetpack Compose components
- One test class spans many variants — no more one class per device/orientation/theme/strategy combination
- Component and full-screen rendering kinds
- Support for different device configurations (Phone/Tablet) and orientations (Portrait/Landscape)
- Dark/Light UI mode testing
- Font scale testing
- Per-preview exceptions without touching the library: inline `extraMetadata` tokens and named `profile`s (narrow **or** widen a single axis for one
  preview), plus a standing `exclude` predicate for axis couplings
- Fail-fast validation: untagged, over-restricted or name-colliding previews throw `SnapshotConfigException` at test-class construction
- Integration with popular testing tools:
    - Paparazzi for snapshot generation
    - Showkase for component discovery
    - Kotest for test execution

## Installation

Add the following configuration, depending on what you need. You should always use BOM to be sure to
get binary compatible dependencies. If you have a custom snapshots engine, you will need only 
`annotations` and `framework` dependencies. Additionally, you can add `paparazzi` as well for our 
Paparazzi engine implementation.

### Annotations & Framework
For `annotations` and `framework` artifacts you will need the following configuration:

```toml
[versions]
ackee-snapshots-bom = "SPECIFY_VERSION"
showkase = "SPECIFY_VERSION"

[dependencies]
ackee-snapshots-bom = { group = "io.github.ackeecz", name = "snapshots-bom", version.ref = "ackee-snapshots-bom" }
ackee-snapshots-annotations = { group = "io.github.ackeecz", name = "snapshots-annotations" }
ackee-snapshots-framework = { group = "io.github.ackeecz", name = "snapshots-framework" }

# Showkase is needed for annotating previews and passing a selected PreviewSnapshotKind
showkase-core = { module = "com.airbnb.android:showkase", version.ref = "showkase" }
showkase-processor = { module = "com.airbnb.android:showkase-processor", version.ref = "showkase" }
```

and specify dependencies

```kotlin
implementation(platform(libs.ackee.snapshots.bom))
implementation(libs.ackee.snapshots.annotations)
testImplementation(libs.ackee.snapshots.framework)

implementation(libs.showkase.core)
ksp(libs.showkase.processor)
```

> `framework` depends on `annotations`, and `paparazzi` depends on `framework`. The BOM pins a
> compatible set of all three, so you never specify their versions explicitly.

### Paparazzi
If you use `paparazzi` artifact, you will need the following additional configuration:

```toml
[versions]
# Ensure that this version is equal or greater than Paparazzi version in this project. Otherwise
# there might be incompatibility between Paparazzi runtime used in Ackee Snapshots and Paparazzi
# Gradle plugin used by your app.
paparazzi = "SPECIFY_VERSION"

[dependencies]
ackee-snapshots-paparazzi = { group = "io.github.ackeecz", name = "snapshots-paparazzi" }

[plugins]
paparazzi = { id = "app.cash.paparazzi", version.ref = "paparazzi" }
```

Apply the Paparazzi Gradle plugin in your module's `build.gradle.kts`:

```kotlin
plugins {
    id(libs.plugins.paparazzi)
}
```

and specify dependencies

```kotlin
testImplementation(libs.ackee.snapshots.paparazzi)
```

## Tagging previews

Every preview you want to snapshot is a `@ShowkaseComposable` whose `extraMetadata` carries **exactly one** snapshot-kind tag from `PreviewSnapshotKind`:

1. `PreviewSnapshotKind.Component` — rendered device-independent, at the minimal size that fits the content. Ideal for individual UI components.
2. `PreviewSnapshotKind.Screen` — rendered within the full frame of each configured device (device + orientation). Perfect for complete screens.

Showkase collects both `@Preview` and `@ShowkaseComposable` functions, but only the latter carries `extraMetadata`. Use `@Preview` to render the
composable in Android Studio and `@ShowkaseComposable` to opt it into snapshots and choose its kind:

```kotlin
@Preview
@ShowkaseComposable(extraMetadata = [PreviewSnapshotKind.Component])
@Composable
fun PrimaryButtonPreview() {
    SnapshotsSampleTheme {
        PrimaryButton(text = "Click me") { }
    }
}

@Preview
@ShowkaseComposable(extraMetadata = [PreviewSnapshotKind.Screen])
@Composable
fun HomeScreenPreview() {
    SnapshotsSampleTheme {
        HomeScreen()
    }
}
```

> Every preview passed to `previews(...)` must carry a `PreviewSnapshotKind` tag — an untagged preview fails test-class construction with a
> `SnapshotConfigException`. If your Showkase metadata also contains previews you don't want to snapshot (e.g. `@Preview`-only functions), filter them
> out before passing them in (see [Scoping previews](#scoping-previews)).

## Usage

Subclass `PaparazziSnapshotTests` and describe the snapshot matrix in the config block. A single class can list both kinds and every axis:

```kotlin
class SampleSnapshotTests : PaparazziSnapshotTests({
    // Required: the previews to snapshot. Every one must carry a PreviewSnapshotKind tag.
    previews(Showkase.getMetadata())

    // Required: wraps every rendered preview — typically your app theme, keyed off the UI mode.
    decorate { uiMode, content ->
        SnapshotsSampleTheme(darkTheme = uiMode == UiMode.DARK, dynamicColor = false) {
            content()
        }
    }

    // Optional: runs before each snapshot with the prepared rendering Context (default: no-op).
    // before { context -> /* seed a fake, set a locale, … */ }

    // Required: the variant matrix.
    variants {
        components()                                 // snapshot Component-tagged previews
        screens(Device.Pixel6.portrait, Device.Nexus10.landscape) // snapshot Screen-tagged previews on these devices
        uiModes(UiMode.LIGHT, UiMode.DARK)           // at least one required
        fontScales(FontScale.NORMAL, FontScale.LARGE) // at least one required
    }
})
```

For each preview, the framework renders the **Cartesian product** of the enabled kind(s), the applicable devices (screens only), `uiModes` and
`fontScales`, minus any `exclude`d cells and after applying per-preview overrides. The example above snapshots every component in
`{LIGHT, DARK} × {NORMAL, LARGE}` and every screen additionally across the two devices.

The DSL surface:

| Call | Where | Required | Purpose |
|---|---|---|---|
| `previews(metadata)` | config | yes | The Showkase previews to snapshot (pre-filter to scope). |
| `decorate { uiMode, content -> }` | config | yes | Wrapper around every preview (your theme), keyed off the `UiMode`. |
| `before { context -> }` | config | no | Setup before each snapshot, given the prepared `Context`. |
| `variants { }` | config | yes | Declares the matrix. |
| `components()` | variants | — | Enable Component-tagged previews. |
| `screens(vararg DeviceConfig)` | variants | — | Enable Screen-tagged previews on the given devices. |
| `uiModes(vararg UiMode)` | variants | yes (≥1) | UI modes every preview is rendered in. |
| `fontScales(vararg FontScale)` | variants | yes (≥1) | Font scales every preview is rendered at. |
| `exclude { variant -> Boolean }` | variants | no (0..n) | Drop every matching `SnapshotVariant` from the matrix. |
| `profile(key) { }` | variants | no (0..n) | Define a named per-preview override (see below). |

Ready-made device shortcuts live under the `Device` enum: `Device.Pixel6.portrait`, `Device.Pixel6.landscape`, `Device.Nexus10.portrait`,
`Device.Nexus10.landscape` — each a `DeviceConfig`.

### Excluding coupled cells

`exclude` removes cells from the Cartesian product and is re-applied **after** overrides, so axis couplings always hold. For example, to skip the
`DARK × LARGE` combination (you already cover `LARGE` in light mode and `DARK` in normal) while still covering both axes:

```kotlin
variants {
    components()
    screens(Device.Pixel6.portrait)
    uiModes(UiMode.LIGHT, UiMode.DARK)
    fontScales(FontScale.NORMAL, FontScale.LARGE)
    exclude { it.uiMode == UiMode.DARK && it.fontScale == FontScale.LARGE }
}
```

### Per-preview overrides

Sometimes a single preview needs a different set of axes than the rest of the class (a text-less component doesn't need font-scale variants; one screen
should only be captured on a phone). Overrides let that exception live **at the preview**, without a new test class. Each axis resolves independently as
`inline token ?: profile ?: class`, then `exclude` is re-applied. Overrides may **narrow** the class axes or **widen** them beyond what the class
declared (as long as `exclude` doesn't remove the new cell); a preview that resolves to **zero** variants is an error.

**Inline tokens** — tag the preview with an `extraMetadata` token to pin one axis for that preview only:

```kotlin
@Preview
@ShowkaseComposable(
    // This component is captured only at NORMAL font scale, in LIGHT mode — regardless of the class axes.
    extraMetadata = [PreviewSnapshotKind.Component, PreviewFontScale.Normal, PreviewUiMode.Light],
)
@Composable
fun StatusBadgePreview() { StatusBadge(text = "Active") }

@Preview
@ShowkaseComposable(
    // This screen is captured only on Pixel 6 portrait.
    extraMetadata = [PreviewSnapshotKind.Screen, PreviewDevice.Pixel6Portrait],
)
@Composable
fun LoginScreenPreview() { LoginScreen() }
```

Available tokens (all in the `annotations` module): `PreviewFontScale.{Small, Normal, Large}`, `PreviewUiMode.{Light, Dark}`,
`PreviewDevice.{Pixel6Portrait, Pixel6Landscape, Nexus10Portrait, Nexus10Landscape}`. Tagging multiple values of the same axis sets that axis to the
set (e.g. two `PreviewFontScale` tokens ⇒ both scales).

**Named profiles** — when several previews share the same exception, define it once as a `profile(key)` and reference the `key` from each preview. Keep
the key in a shared `const` so the tag and the profile can't drift apart:

```kotlin
object PreviewProfile {

    const val NoText = "noText"
}

// preview:
@Preview
@ShowkaseComposable(extraMetadata = [PreviewSnapshotKind.Component, PreviewProfile.NoText])
@Composable
fun AvatarPreview() { Avatar() }

// test class:
class SampleSnapshotTests : PaparazziSnapshotTests({
    previews(Showkase.getMetadata())
    decorate { uiMode, content -> SnapshotsSampleTheme(darkTheme = uiMode == UiMode.DARK) { content() } }
    variants {
        components()
        uiModes(UiMode.LIGHT, UiMode.DARK)
        fontScales(FontScale.NORMAL, FontScale.LARGE)
        // The text-less Avatar doesn't need font-scale variants:
        profile(PreviewProfile.NoText) { fontScales(FontScale.NORMAL) }
    }
})
```

A profile body (`ProfileOverrideScope`) may override any subset of `devices(...)`, `uiModes(...)`, `fontScales(...)`; axes it leaves unset fall through
to the class config. A preview may reference **at most one** profile, but may still add inline tokens on top of it — an inline token wins over the
profile on the same axis.

### Scoping previews

`previews(...)` takes a `ShowkaseElementsMetadata`. Pass the whole set (`Showkase.getMetadata()`) only if every discovered preview carries a
`PreviewSnapshotKind` tag; otherwise pre-filter. This is also how you scope a shared base class to one module or environment:

```kotlin
val snapshotPreviews = ShowkaseElementsMetadata(
    componentList = Showkase.getMetadata().componentList.filter {
        it.extraMetadata.contains(PreviewSnapshotKind.Component) ||
            it.extraMetadata.contains(PreviewSnapshotKind.Screen)
    },
)
```

## How snapshots are grouped

Paparazzi is a JUnit4 rule, so a single Paparazzi instance renders one fixed device configuration. The framework works around this for you: it groups
the resolved variants by `(kind, device, UI mode)` and opens a Kotest `context` per group, each backed by a **fresh** engine instance configured for
that group. You therefore no longer need a separate test class per device/orientation/theme — one class covers them all, and each group renders with
its own correctly-configured Paparazzi.

Golden file names encode every axis so no two variants ever collide:

```
<id>_<device>_<uiMode>_FontScale-<fontScale>
```

`<id>` is the preview's `group_componentName`; the `<device>` segment (e.g. `Pixel6-portrait`) is present for screens and omitted for
device-independent components. For example: `ArticleCard_LIGHT_FontScale-NORMAL` (component) and `SettingsScreen_Pixel6-portrait_DARK_FontScale-LARGE`
(screen).

## Migrating from the positional constructor

Earlier versions took a fixed positional constructor (one `strategy`, one `uiTheme`, a `fontScales` list, a pre-filtered preview list, a `before` and a
`theme`), so you wrote one class per device/orientation/theme/strategy combination. The config DSL collapses those into a single class. Renames to know:
`UiTheme` → `UiMode`, `theme` → `decorate`, the `SnapshotStrategy` sealed type → the `components()` / `screens(...)` DSL calls plus a separate device
axis, and `PreviewSnapshotStrategy` → `PreviewSnapshotKind`. Because the UI mode is now part of every golden name, **re-record all goldens** after
upgrading.

**(a) Light + dark with a theme→font-scale asymmetry → one class using `exclude`.** The old pattern rendered light in `{NORMAL, LARGE}` but dark only in
`{NORMAL}` across two classes. Now:

```kotlin
class ComponentsSnapshotTests : PaparazziSnapshotTests({
    previews(snapshotPreviews)
    decorate { uiMode, content -> AppTheme(dark = uiMode == UiMode.DARK) { content() } }
    variants {
        components()
        uiModes(UiMode.LIGHT, UiMode.DARK)
        fontScales(FontScale.NORMAL, FontScale.LARGE)
        exclude { it.uiMode == UiMode.DARK && it.fontScale == FontScale.LARGE }
    }
})
```

**(b) Multi-module project scoping previews to its own module → one shared base + thin per-module subclasses.** Put the shared config in a base class
that takes the module's metadata; each module supplies a pre-filtered `ShowkaseElementsMetadata`:

```kotlin
abstract class BaseSnapshotTests(previews: ShowkaseElementsMetadata) : PaparazziSnapshotTests({
    previews(previews)
    decorate { uiMode, content -> AppTheme(dark = uiMode == UiMode.DARK) { content() } }
    variants {
        components()
        screens(Device.Pixel6.portrait)
        uiModes(UiMode.LIGHT, UiMode.DARK)
        fontScales(FontScale.NORMAL)
    }
})

class FeatureProfileSnapshotTests : BaseSnapshotTests(profileModulePreviews)
```

**(c) Components and screens sharing setup → one class listing both kinds:**

```kotlin
class UiSnapshotTests : PaparazziSnapshotTests({
    previews(snapshotPreviews)
    decorate { uiMode, content -> AppTheme(dark = uiMode == UiMode.DARK) { content() } }
    variants {
        components()
        screens(Device.Pixel6.portrait, Device.Nexus10.landscape)
        uiModes(UiMode.LIGHT, UiMode.DARK)
        fontScales(FontScale.NORMAL, FontScale.LARGE)
    }
})
```

**(d) Per-preview font-scale opt-out → a `noText` profile.** Instead of a separate class (or a copied test-generation loop) for the handful of previews
that shouldn't vary by font scale, tag them and add a one-line profile — see [Named profiles](#per-preview-overrides) above.

## Working with Snapshots

### Generating Screenshots

To generate screenshots, run the Paparazzi record task:

```bash
./gradlew cleanRecordPaparazziDebug
```

This will generate screenshots for all annotated composables in your source set. The snapshots are stored in:

```
{module}/src/test/snapshots
```

### Verifying Screenshots

To verify that your UI components haven't changed unexpectedly, run:

```bash
./gradlew verifyPaparazziDebug
```

If there are any differences between the recorded and current snapshots, the test will fail and Paparazzi will generate a report showing the
differences.

## Project Structure

- **bom**: BOM module
- **annotations**: Preview tags and per-preview override tokens. Used in test and production source sets.
- **framework**: Config DSL, resolver, `SnapshotEngine` interface and Kotest test-generation infrastructure
- **paparazzi**: Paparazzi integration for snapshot generation
- **sample**: Example application demonstrating usage (see the per-concern `*SnapshotTests` classes under `sample/src/test`)

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Credits

Developed by [Ackee](https://www.ackee.cz) team with 💙.
