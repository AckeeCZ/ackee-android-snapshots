# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## BOM [3.2.0-paparazzialpha05] - 2026-09-03

Adds support for androidx's `@PreviewWrapper`: a preview that declares a `PreviewWrapperProvider` —
directly or through a custom preview annotation — is now snapshot wrapped exactly as Android Studio
renders it, with no change to your `decorate { }`. The BOM re-pins the set; `annotations` is functionally
unchanged and is not re-released.

> ⚠️ Consumers must move to **Compose BOM 2026.08.00** (`androidx.compose.ui:ui-tooling-preview`
> **1.12.0**), where `@PreviewWrapper` may also be placed on custom preview annotations — 1.11.x
> declares the annotation but targets functions only. `framework` depends on the Compose BOM itself, so
> the whole 2026.08.00 platform (Material3 1.4.0 included) reaches your unit-test classpath
> transitively. Re-record your goldens afterwards (`./gradlew cleanRecordPaparazziDebug`); the newer
> Compose renders some pixels differently. Nothing else about the API changed, and previews without
> `@PreviewWrapper` render exactly as before.

### framework
- Added automatic `@PreviewWrapper` support. The wrapper is resolved once per preview at test-class
  construction (the annotation has `BINARY` retention, so it is read from the class files) and applied
  **innermost**, inside `decorate` and the font-scale density — a wrapper reading `MaterialTheme`
  therefore follows the UI-mode axis. A fresh wrapper instance is created per snapshot, mirroring Studio.
  Wrappers inherited from custom preview annotations are followed recursively; a function-level
  `@PreviewWrapper` wins over an inherited one, and two different inherited wrappers are a configuration
  error.
- Added public `PreviewWrappers` (`Disabled` / `Enabled(locate)`) and `PreviewFunction`, plus the
  `previewWrappers(...)` config-DSL entry (both `@ExperimentalSnapshotsApi`).
  `previewWrappers(PreviewWrappers.Disabled)` is the kill switch;
  `PreviewWrappers.Enabled { component -> … }` overrides the automatic preview lookup.
- Fail-fast validation extends to wrappers: a preview whose declaration site cannot be located, an
  ambiguous inherited wrapper, or a wrapper class that is missing / abstract / not a
  `PreviewWrapperProvider` / without a public zero-argument constructor throws `SnapshotConfigException`
  at construction, naming the preview and the remedy.
- Added `org.ow2.asm:asm` as a new runtime dependency (class-file reading); it reaches your unit-test
  classpath transitively.

### paparazzi
- No API or behavioral change; re-released at `3.2.0-paparazzialpha05` against the new `framework`.

### annotations
- No changes; stays at `3.1.0`.

### build & tooling (internal)
- Bumped Compose BOM `2026.06.01` → `2026.08.00` (Compose UI 1.12.0, Material3 1.4.0) and re-recorded the
  sample goldens. Added `:sample` previews and goldens covering wrapped, inherited-wrapper, `internal`,
  `object`-nested and `@PreviewParameter` previews.

## BOM [3.1.0-paparazzialpha05] - 2026-07-10

Dependency and build-toolchain update. No new features and no public API or behavioral changes to the
library itself, but the published artifacts now require a newer consumer toolchain (see below). The BOM
re-pins the whole set; `annotations` is functionally unchanged and is re-released only to keep the BOM
on a single base version.

> ⚠️ To upgrade, consumers must move their build to **AGP 9**, **Gradle 9**, **Kotlin 2.4** and
> **compileSdk 37**, and — for the Paparazzi engine — use **Paparazzi ≥ 2.0.0-alpha05**. Re-record your
> goldens afterwards (`./gradlew cleanRecordPaparazziDebug`); the newer Compose / LayoutLib renders some
> pixels differently.

### framework
- Recompiled against Kotlin 2.4, Compose BOM 2026.06.01 and newer AndroidX. No source-level public API
  change — the committed ABI dump moved only because Kotlin 2.4 re-mangles the name of a synthetic
  Compose accessor (an internal `ComposableSingletons` lambda), which consumers never reference.

### paparazzi
- Updated Paparazzi `2.0.0-alpha02` → `2.0.0-alpha05`; the `paparazzi` / `bom` version suffix moves to
  `-paparazzialpha05` accordingly. `PaparazziEngine` was adapted to Paparazzi's newer lifecycle
  (`teardown()` / `setup()`); no public API change.

### annotations
- No changes; re-released at `3.1.0` only to keep the BOM set on a single base version.

### build & tooling (internal)
- Migrated the convention plugins to the AGP 9 Android DSL; bumped Gradle to `9.6.1` and
  `compileSdk` / `targetSdk` to `37`. Added a Gradle dependency-updates check.
- Replaced the standalone Binary Compatibility Validator with the Kotlin Gradle Plugin's built-in ABI
  validation (`checkLegacyAbi` / `updateLegacyAbi`); the committed `.api` dump format is unchanged.
- Also updated Kotest, JUnit, Dokka and vanniktech-maven-publish.

## BOM [3.0.0-paparazzialpha02] - 2026-07-09

Major redesign of the snapshot API: a single **config DSL** on the test class replaces the old
positional constructor, so one class now spans many kinds, devices, UI modes and font scales instead
of one class per combination. Per-preview exceptions (inline `extraMetadata` tokens and named
`profile`s) and a standing `exclude` predicate live at the call site, so new axes and exceptions no
longer require new constructor parameters or copied library internals. See the README **Usage** and
**Migrating from the positional constructor** sections.

> ⚠️ Golden image names now encode every axis (`<id>_<device>_<uiMode>_FontScale-<scale>`), so **all
> consumers must re-record their goldens** (`./gradlew cleanRecordPaparazziDebug`) after upgrading.

### annotations
- **BREAKING**: Renamed `PreviewSnapshotStrategy` → `PreviewSnapshotKind` (constants `Component` /
  `Screen` unchanged).
- Added per-preview inline override tokens: `PreviewFontScale`, `PreviewUiMode` and `PreviewDevice`.
  Tag a preview with one (or more) to narrow or widen a single axis for that preview only.

### framework
- **BREAKING**: `AckeeSnapshotTests` (and the Paparazzi subclass) now take a single config DSL block
  (`SnapshotConfigScope` → `previews` / `before` / `decorate` / `variants`, with `VariantsScope` and
  `ProfileOverrideScope`) instead of the fixed positional parameter list.
- **BREAKING**: Renamed `UiTheme` → `UiMode`.
- **BREAKING**: Replaced the `SnapshotStrategy` sealed interface with a `SnapshotKind` enum
  (`Component` / `Screen`) plus a separate device axis. The `SnapshotEngine` SPI was reworked
  accordingly (`init(kind, device, uiMode, scope)` + `snapshot(goldenName, content)`), and a fresh
  engine is created per `(kind, device, uiMode)` group.
- **BREAKING**: Golden names now always encode every axis; existing goldens must be re-recorded.
- Added `Device.Pixel6` / `Device.Nexus10` `.portrait` / `.landscape` `DeviceConfig` shortcuts.
- Added per-preview overrides (inline tokens + named `profile`s, resolved `inline ?: profile ?: class`)
  and a standing `exclude { }` predicate re-applied after overrides. Misconfiguration (untagged,
  over-restricted or name-colliding previews) fails fast with `SnapshotConfigException`.
- `:framework` now depends on `:annotations` (to read the preview tags / override tokens). The BOM
  keeps the two artifacts aligned.

### paparazzi
- **BREAKING**: `PaparazziSnapshotTests` now takes the config DSL block; the positional constructor is
  removed. `PaparazziEngine` was reimplemented against the new SPI — one `Paparazzi` per group, driven
  by the standard per-test rule lifecycle.

## BOM [2.1.0-paparazzialpha02] - 2025-11-04

### framework

Update snapshots generation to not generate duplicate snapshots when testing UI theme and then
the font scales.

## BOM [2.0.0-paparazzialpha02] - 2025-09-16
Major breaking release that brings following changes:
- Versioning of the library was completely changed. There are no longer Compose and Paparazzi
suffixes in the artifact versions and clients are not forced to specify dependencies explicitly
(unless it is required by the basic setup itself as described in README).
- Library now releases BOM version that should be used instead of particular artifact versions.
- This release depends on the unstable alpha version of the Paparazzi library, because newest stable
version is no longer compatible with the newest Gradle versions. This is related only to `paparazzi`
artifact and BOM, which explicitly reflects this `paparazzi` versioning in its version as well.
Unstable Paparazzi library is reflected in the version name by `-paparazzialphaxx` suffix, which 
matches the current alpha suffix version of the Paparazzi. Once Paparazzi releases stable version 
and Ackee Snapshots update to it, the suffix will be removed. The base semantic version `x.x.x` of 
the library version reflects just the version of Ackee Snapshots itself, independently on Paparazzi
or any other dependency.
- Update dependencies, including Kotlin to 2.2.20 and Kotest to 6.0.3.

### paparazzi
- Paparazzi version increased to `2.0.0-alpha02`. Make sure you use at least this version for
Paparazzi Gradle plugin.
