![ackee|Ackee Android Snapshots](imgs/cover.png)

# Ackee Android Snapshots

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.ackeecz/snapshots-annotations)](https://central.sonatype.com/artifact/io.github.ackeecz/snapshots-annotations)

## Overview

Ackee Android Snapshots is an opinionated framework designed for snapshot testing in Android projects, particularly focusing on Jetpack Compose UI
components. It leverages Paparazzi for rendering snapshots, Showkase for component discovery, and Kotest as the testing framework.

## Architecture

The framework is designed with extensibility in mind, using a modular architecture:

- `framework` module defines the base interfaces (`SnapshotEngine`, `SnapshotStrategy`) and common testing infrastructure
- `paparazzi` module provides a concrete implementation of the snapshot engine using Paparazzi
- `annotations` module contains shared code for preview annotations
- Additional snapshot engine implementations can be added by implementing the `SnapshotEngine` interface

## Features

- Automated snapshot testing for Jetpack Compose components
- Support for different device configurations (Phone/Tablet)
- Support for different device orientations (Portrait/Landscape)
- Dark/Light theme testing
- Font scale testing
- Component and full-screen testing strategies
- Integration with popular testing tools:
    - Paparazzi for snapshot generation
    - Showkase for component discovery
    - Kotest for test execution

## Installation

Add the following dependencies to your `libs.versions.toml`:

```toml
[versions]
ackee-snapshots-annotations = "SPECIFY_VERSION"
ackee-snapshots-framework = "SPECIFY_VERSION"
ackee-snapshots-paparazzi = "SPECIFY_VERSION"
compose-bom = "SPECIFY_VERSION"
showkase = "SPECIFY_VERSION"
paparazzi = "SPECIFY_VERSION"

[dependencies]
ackee-snapshots-framework = { group = "io.github.ackeecz", name = "snapshots-framework", version.ref = "ackee-snapshots-framework" }
ackee-snapshots-paparazzi = { group = "io.github.ackeecz", name = "snapshots-paparazzi", version.ref = "ackee-snapshots-paparazzi" }
ackee-snapshots-annotations = { group = "io.github.ackeecz", name = "snapshots-annotations", version.ref = "ackee-snapshots-annotations" }

# Required dependencies - versions need to be specified by the consumer
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version = "compose-bom" }
showkase-core = { module = "com.airbnb.android:showkase", version.ref = "showkase" }
showkase-processor = { module = "com.airbnb.android:showkase-processor", version.ref = "showkase" }

[plugins]
paparazzi = { id = "app.cash.paparazzi", version.ref = "paparazzi" }
```

Apply the Paparazzi Gradle plugin in your app's `build.gradle.kts`:

```kotlin
plugins {
    id("app.cash.paparazzi")
}
```

and specify dependencies

```kotlin
implementation(libs.ackee.snapshots.annotations)
implementation(libs.showkase.core)
ksp(libs.showkase.processor)

testImplementation(libs.ackee.snapshots.framework)
testImplementation(libs.ackee.snapshots.paparazzi)
```

## Versioning

The library uses a versioning scheme that reflects compatibility with its core dependencies:

### Framework Module

The framework module version includes the Compose BOM version as a suffix:
```
{base_version}-{compose_bom_version}
```

Example: `0.1.0-2024.02.00`

### Paparazzi Module
The Paparazzi module version includes the Paparazzi version as a suffix:
```
{base_version}-{paparazzi_version}
```

Example: `0.1.0-1.3.1`

### Annotations Module

The annotations module uses only the base version without any suffix:
```
{base_version}
```
Example: `0.1.0`

This versioning strategy helps users:
- Track compatibility with specific Compose and Paparazzi versions
- Choose the right artifact version for their setup
- Avoid version conflicts with their project's dependencies

Note: The library intentionally does not set Compose or Paparazzi versions to prevent version conflicts. You need to specify these versions in your
project to ensure compatibility with your setup.

## Configuration

The library supports two main testing strategies:

1. Component Testing (`SnapshotStrategy.Component`):
    - Components are rendered in the smallest possible size
    - Snapshots are not taken for different devices
    - Ideal for testing individual UI components

2. Screen Testing (`SnapshotStrategy.Screen`):
    - Screens are rendered within the full frame of the selected device configuration
    - Device configuration includes both device type and orientation
    - Snapshots are taken for all configured device configurations
    - Perfect for testing complete screens and layouts within a device frame

To determine on a Preview level which strategy to use the predefined
annotation metadata tag can be used along the `ShowkaseComposable` annotation. Showkase collects
both `@Preview` and `@ShowkaseComposable` methods, but only the latter can be used to specify the strategy.
In the following example `@Preview` is used to render the component in Android Studio and `@ShowkaseComposable`
for the snapshots.

```kotlin
@Preview
@ShowkaseComposable(extraMetadata = [PreviewSnapshotStrategy.Component])
@Composable
fun PrimaryButtonPreview() {
    SnapshotsSampleTheme {
        PrimaryButton(text = "Click me") { }
    }
}
```

## Limits

Since paparazzi is a JUnit4 rule implementation, it is not possible within the single test case to run multiple snapshots with different device
specifications hence only a single device may be used for a single test case. To take snapshots on multiple
devices specify a separate test case for each device. Check the sample below.

## Usage

1. Create snapshot test classes for different testing strategies:

```kotlin
val fontScales = listOf(FontScale.NORMAL, FontScale.LARGE)

val before = { _: Context ->
    // nothing
}

val theme: @Composable (UiTheme, @Composable () -> Unit) -> Unit = { uiTheme, content ->
    SnapshotsSampleTheme(
        darkTheme = uiTheme == UiTheme.DARK,
        dynamicColor = false
    ) {
        content()
    }
}

val screenPreviews = Showkase.getMetadata().componentList.filter {
    !it.extraMetadata.contains(PreviewSnapshotStrategy.Component)
}

val componentPreviews = Showkase.getMetadata().componentList.filter {
    it.extraMetadata.contains(PreviewSnapshotStrategy.Component)
}

class LightComponentsSnapshotTests : PaparazziSnapshotTests(
    before = before,
    fontScales = fontScales,
    showkasePreviews = componentPreviews,
    theme = theme,
    uiTheme = UiTheme.LIGHT,
    strategy = SnapshotStrategy.Component
)

class DarkComponentsSnapshotTests : PaparazziSnapshotTests(
    before = before,
    fontScales = emptyList(),
    showkasePreviews = componentPreviews,
    theme = theme,
    uiTheme = UiTheme.DARK,
    strategy = SnapshotStrategy.Component
)

class LightPortraitPhoneSnapshotTests : PaparazziSnapshotTests(
    before = before,
    fontScales = fontScales,
    showkasePreviews = screenPreviews,
    theme = theme,
    uiTheme = UiTheme.LIGHT,
    strategy = SnapshotStrategy.Screen(
        DeviceConfig(
            device = Device.PIXEL_6,
            orientation = DeviceOrientation.PORTRAIT
        )
    ),
)

class DarkPortraitPhoneSnapshotTests : PaparazziSnapshotTests(
    before = before,
    fontScales = emptyList(),
    showkasePreviews = screenPreviews,
    theme = theme,
    uiTheme = UiTheme.DARK,
    strategy = SnapshotStrategy.Screen(
        DeviceConfig(
            device = Device.PIXEL_6,
            orientation = DeviceOrientation.PORTRAIT
        )
    ),
)

class LightLandscapePhoneSnapshotTests : PaparazziSnapshotTests(
    before = before,
    fontScales = fontScales,
    showkasePreviews = screenPreviews,
    theme = theme,
    uiTheme = UiTheme.LIGHT,
    strategy = SnapshotStrategy.Screen(
        DeviceConfig(
            device = Device.PIXEL_6,
            orientation = DeviceOrientation.LANDSCAPE
        )
    ),
)

class DarkLandscapePhoneSnapshotTests : PaparazziSnapshotTests(
    before = before,
    fontScales = emptyList(),
    showkasePreviews = screenPreviews,
    theme = theme,
    uiTheme = UiTheme.DARK,
    strategy = SnapshotStrategy.Screen(
        DeviceConfig(
            device = Device.PIXEL_6,
            orientation = DeviceOrientation.LANDSCAPE
        )
    ),
)

class LightTabletSnapshotTests : PaparazziSnapshotTests(
    before = before,
    fontScales = fontScales,
    showkasePreviews = screenPreviews,
    theme = theme,
    uiTheme = UiTheme.LIGHT,
    strategy = SnapshotStrategy.Screen(
        DeviceConfig(
            device = Device.NEXUS_10,
            orientation = DeviceOrientation.LANDSCAPE
        )
    ),
)

class DarkTabletSnapshotTests : PaparazziSnapshotTests(
    before = before,
    fontScales = emptyList(),
    showkasePreviews = screenPreviews,
    theme = theme,
    uiTheme = UiTheme.DARK,
    strategy = SnapshotStrategy.Screen(
        DeviceConfig(
            device = Device.NEXUS_10,
            orientation = DeviceOrientation.LANDSCAPE
        )
    ),
)
```

2. Annotate your composables with Showkase and specify the strategy:

```kotlin
@Preview
@ShowkaseComposable(
    extraMetadata = [PreviewSnapshotStrategy.Component] // For component testing
)
@Composable
fun ButtonPreview() {
    AppTheme {
        Button(onClick = {}) {
            Text("Click me")
        }
    }
}

@Preview
@ShowkaseComposable(
    extraMetadata = [PreviewSnapshotStrategy.Screen] // For screen testing
)
@Composable
fun ScreenPreview() {
    AppTheme {
        HomeScreen()
    }
}
```

This setup will:

- Test UI components in their minimal size with both light/dark themes and font scales
- Test full screens on a phone (Pixel 6) with both themes and font scales
- Test full screens on a tablet (Nexus 10) with both themes and font scales

## Project Structure

- **annotations**: Shared code for preview annotations. Used in test and production source sets.
- **framework**: Core framework and testing infrastructure
- **paparazzi**: Paparazzi integration for snapshot generation
- **sample**: Example application demonstrating usage

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


## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Credits

Developed by [Ackee](https://www.ackee.cz) team with 💙.
