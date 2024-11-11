# Ackee Android Snapshots

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.ackeecz/snapshots-core)](https://central.sonatype.com/artifact/io.github.ackeecz/snapshots-core)

## Overview

Ackee Android Snapshots is an opinionated framework designed for snapshot testing in Android projects, particularly focusing on Jetpack Compose UI
components. It leverages Paparazzi for rendering snapshots, Showkase for component discovery, and Kotest as the testing framework.

## Architecture

The framework is designed with extensibility in mind, using a modular architecture:

- Core module defines the base interfaces (`SnapshotEngine`, `SnapshotStrategy`) and common testing infrastructure
- Paparazzi module provides a concrete implementation of the snapshot engine using Paparazzi
- Common module contains shared code for preview annotations
- Additional snapshot engine implementations can be added by implementing the `SnapshotEngine` interface

## Features

- Automated snapshot testing for Jetpack Compose components
- Support for different device configurations (Phone/Tablet)
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
ackee-snapshots = "SPECIFY_VERSION"

[dependencies]
ackee-snapshots-core = { group = "io.github.ackeecz", name = "snapshots-core", version.ref = "ackee-snapshots" }
ackee-snapshots-paparazzi = { group = "io.github.ackeecz", name = "snapshots-paparazzi", version.ref = "ackee-snapshots" }
ackee-snapshots-common = { group = "io.github.ackeecz", name = "snapshots-common", version.ref = "ackee-snapshots" }

# Required dependencies - versions need to be specified by the consumer
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version = "SPECIFY_VERSION" }
paparazzi = { group = "app.cash.paparazzi", name = "paparazzi", version = "SPECIFY_VERSION" }
```

Note: The library intentionally does not set Compose or Paparazzi versions to prevent version conflicts. You need to specify these versions in your
project to ensure compatibility with your setup.

Apply the Paparazzi Gradle plugin in your app's `build.gradle.kts`:

```kotlin
plugins {
    id("app.cash.paparazzi")
}
```

## Configuration

The library supports two main testing strategies:

1. Component Testing (`SnapshotStrategy.Component`):
    - Components are rendered in the smallest possible size
    - Snapshots are not taken for different devices
    - Ideal for testing individual UI components

2. Screen Testing (`SnapshotStrategy.Screen`):
    - Screens are rendered within the full frame of the selected device
    - Snapshots are taken for all configured devices
    - Perfect for testing complete screens and layouts within a device frame

## Usage

1. Create snapshot test classes for different testing strategies:

```kotlin
// Common configuration
val fontScales = listOf(FontScale.NORMAL, FontScale.LARGE)
val uiThemes = listOf(UiTheme.LIGHT, UiTheme.DARK)
val before: (Context) -> Unit = {
    // this is executed before each snapshot, it might be used to mock some data, eg. dates used
    // in previews.
}
// Theme wrapper
val theme: @Composable (UiTheme, @Composable () -> Unit) -> Unit = { uiTheme, content ->
    AppTheme(
        darkTheme = uiTheme == UiTheme.DARK,
    ) {
        content()
    }
}

// Filter previews based on strategy
val screenPreviews = Showkase.getMetadata().componentList.filter {
    !it.extraMetadata.contains(PreviewSnapshotStrategy.Component)
}

val componentPreviews = Showkase.getMetadata().componentList.filter {
    it.extraMetadata.contains(PreviewSnapshotStrategy.Component)
}

// Component snapshots
class ComponentsSnapshotTests : PaparazziSnapshotTests(
    before = before,
    fontScales = fontScales,
    showkasePreviews = componentPreviews,
    theme = theme,
    uiThemes = uiThemes,
    strategy = SnapshotStrategy.Component
)

// Phone screen snapshots
class PhoneSnapshotTests : PaparazziSnapshotTests(
    before = before,
    fontScales = fontScales,
    showkasePreviews = screenPreviews,
    theme = theme,
    uiThemes = uiThemes,
    strategy = SnapshotStrategy.Screen(Device.PIXEL_6)
)

// Tablet screen snapshots
class TabletSnapshotTests : PaparazziSnapshotTests(
    before = before,
    fontScales = fontScales,
    showkasePreviews = screenPreviews,
    theme = theme,
    uiThemes = uiThemes,
    strategy = SnapshotStrategy.Screen(Device.NEXUS_10)
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

- **core**: Core framework and testing infrastructure
- **paparazzi**: Paparazzi integration for snapshot generation
- **common**: Shared code for preview annotations and testing
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

## Requirements

- Android SDK 24+
- Kotlin 1.9+
- Jetpack Compose
- JDK 11+

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Credits

Developed by [Ackee](https://www.ackee.cz) team with 💙.
