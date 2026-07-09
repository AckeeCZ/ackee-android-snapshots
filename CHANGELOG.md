# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## BOM [3.0.0-paparazzialpha02] - 2026-07-05

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
