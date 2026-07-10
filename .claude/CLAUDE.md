# CLAUDE.md — ackee-android-snapshots

## Project Overview

Published, opinionated framework for **snapshot testing Jetpack Compose UI** on Android.
Composables are rendered with Paparazzi (on the JVM — no device/emulator), discovered via
Showkase, and driven by Kotest. Consumers tag their previews, subclass a provided test base, and
get snapshots across devices, orientations, themes, and font scales.

- Root package: `io.github.ackeecz.snapshots`
- Maven coordinates: `io.github.ackeecz:snapshots-*` (see `lib.properties`)
- Build tooling: convention plugins in `build-logic/`, Gradle Version Catalog (`gradle/libs.versions.toml`)
- **Public API is the deliverable** — every `.api` dump under `<module>/api/` is part of the contract (see API / ABI Validation).

## Module Structure

```
:annotations   — Preview tags (PreviewSnapshotKind) + per-preview override tokens (PreviewFontScale/PreviewUiMode/PreviewDevice). Used in prod + test source sets. Published.
:framework     — Core: config DSL + resolver + SnapshotEngine SPI + AckeeSnapshotTests (Kotest) test-gen infra. Depends on :annotations. Published.
:paparazzi     — Paparazzi-backed SnapshotEngine impl + PaparazziSnapshotTests. Published.
:bom           — BOM (snapshots-bom); pins a compatible set of artifact versions. Published.
:sample        — Example app + the reference snapshot tests. NOT published; excluded from API validation.
build-logic/   — Included build: convention plugins + release/verification tasks. Largest module; has its own test suite.
```

## Architecture & Extensibility

The library is an **engine abstraction over a snapshot backend**.

- **`SnapshotEngine`** (`:framework`) is the extension point: `init(kind, device, uiMode, scope)`,
  `context`, `snapshot(goldenName, content)`. The framework groups resolved variants by
  `(kind, device, uiMode)` and creates a **fresh engine per group** (via an `engineFactory`), so
  `init` runs exactly once per instance and backends need no cross-group state. To add a new backend,
  implement `SnapshotEngine` — **`PaparazziEngine` (`:paparazzi`) is the reference impl.** Keep the
  split: `:framework` stays backend-agnostic; anything Paparazzi-specific lives in `:paparazzi`.
- **`AckeeSnapshotTests : FunSpec`** is the test-generation core: it takes a **config DSL** block
  (`SnapshotConfigScope`), resolves it (`SnapshotResolver`) into the exact set of variants, groups them
  by `(kind, device, uiMode)` into Kotest `context`s and registers one `test(...)` per variant.
  `PaparazziSnapshotTests` is the Paparazzi-wired convenience subclass consumers extend.
- **Render kind + device axis**: `SnapshotKind` (enum), chosen per preview by its tag —
  - `Component` — rendered at minimal size (Paparazzi `SHRINK`), device-independent.
  - `Screen` — rendered in the full device frame (Paparazzi `NORMAL`), once per configured `DeviceConfig`.
  The device is a **separate axis** (not baked into the kind): enable it via `screens(vararg DeviceConfig)`.
- **Config model**: `DeviceConfig(Device, DeviceOrientation)`, `Device` (PIXEL_6, NEXUS_10) with
  `Device.Pixel6`/`Device.Nexus10` `.portrait`/`.landscape` `DeviceConfig` shortcuts, `UiMode`
  (LIGHT/DARK → NightMode), `FontScale`. `SnapshotVariant(kind, device?, uiMode, fontScale)` is the
  resolved coordinate and the `exclude` predicate receiver.
- **Selection is data, not types**: consumers tag a `@ShowkaseComposable` with
  `extraMetadata = [PreviewSnapshotKind.Component]` (from `:annotations`); the DSL's `components()` /
  `screens(...)` enable the kinds and the resolver filters previews by their tag. Per-preview
  exceptions are inline override tokens (`PreviewFontScale`/`PreviewUiMode`/`PreviewDevice`) or a named
  `profile`, resolved `inline ?: profile ?: class` with `exclude` re-applied. Untagged / over-restricted
  / name-colliding previews throw `SnapshotConfigException` at construction.

## Build & Compiler Configuration

Android-only — **not** Kotlin Multiplatform. Modules apply `com.android.library` /
`com.android.application` via the convention plugins below.

- SDK levels and the JVM/Java target are single-sourced in `build-logic/.../util/Constants.kt` —
  read them there, never assume.
- `allWarningsAsErrors = true` — even a deprecation warning fails the build.
- `freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"`.
- **Experimental API**: the config DSL (`SnapshotConfigScope`/`VariantsScope`/`ProfileOverrideScope`)
  and `SnapshotEngine` are marked `@ExperimentalSnapshotsApi` (marker in `:annotations`, opt-in level
  `WARNING`). Every module auto-opts-in via `optIn.add(...)` in `KotlinConventionPlugin`, so
  internal/sample code needs no `@OptIn` and `allWarningsAsErrors` stays green; **consumers must opt
  in** (per-site `@OptIn` or module-wide `optIn`). Applying the marker does *not* move the `.api`
  dumps (the ABI dump records the marker's own class in `annotations.api`, not annotation usages) — so
  widening the experimental surface is not an ABI change. To mark a new declaration experimental, add
  `@ExperimentalSnapshotsApi`; the module already sees the marker (`:annotations` is a universal dep).
- Compose is enabled per module via the `ackeecz.snapshots.compose` convention plugin.
- **No `explicitApi()` in the library modules** (only `build-logic` runs strict, via kotlin-dsl).
  Visibility is *not* compiler-enforced, so mark implementation types `internal` deliberately — the
  ABI dump is the safety net, not the compiler.

## API / ABI Validation

Public API is tracked with the **Kotlin Gradle Plugin's built-in ABI validation** (`abiValidation`,
enabled per library module in `AndroidLibraryConventionPlugin`), using its **legacy dump** so the
committed `.api` format is unchanged.

- Dumps live at `<module>/api/<module>.api` and are committed. Any change to a `public` declaration
  must be reflected there.
- `:sample` is excluded: `abiValidation` is enabled only for library modules (it defaults to disabled),
  so `:sample`'s `checkLegacyAbi` is a no-op.
- **AGP 9 caveat (temporary):** AGP's built-in Kotlin does *not* wire `abiValidation`, so the project
  keeps the `org.jetbrains.kotlin.android` plugin with `android.builtInKotlin=false` +
  `android.newDsl=false` (see the TODOs in `gradle.properties` / `gradle/libs.versions.toml`). When AGP
  wires ABI validation for built-in Kotlin, drop those two flags + the kotlin-android plugin and
  re-enable built-in Kotlin. `checkLegacyAbi`/`updateLegacyAbi` are themselves deprecated in favor of
  the new-format `checkKotlinAbi`/`updateKotlinAbi`; we stay on the legacy dump to keep the committed
  `.api` format.

Workflow when public API changes:

1. `./gradlew updateLegacyAbi` to regenerate dumps.
2. Commit the dumps with the code change.

`checkLegacyAbi` fails the build (and `preMergeRequestCheck` / CI) when the committed dump is stale. If
the dump didn't move, the change wasn't public.

## Convention Plugins (`build-logic/`)

| Plugin ID | Applied to | Purpose |
|---|---|---|
| `ackeecz.snapshots.android.library` | annotations, framework, paparazzi | Android library + shared Android/Kotlin/Detekt conventions |
| `ackeecz.snapshots.android.application` | sample | Android app + shared Android/Kotlin conventions |
| `ackeecz.snapshots.compose` | framework, paparazzi, sample | Compose compiler plugin + `buildFeatures.compose` |
| `ackeecz.snapshots.publishing` | published modules + bom | vanniktech Maven publish + Dokka + release/verify tasks |
| `ackeecz.snapshots.preflightchecks` | root | Registers `preMergeRequestCheck` / `prePublishCheck` |

The Android / Kotlin / Detekt conventions are internal plugins applied *transitively* by the
library/application plugins — they are not applied directly in module build scripts.

## Testing

- **Kotest `FunSpec` everywhere** — both the snapshot infra and the build-logic suite. JUnit 5
  platform is the runner.
- **Snapshot tests** extend `PaparazziSnapshotTests` and pass a config DSL block. Paparazzi is a
  JUnit4 rule (one fixed device per instance), but the framework groups variants by
  `(kind, device, uiMode)` and creates a fresh engine per group, so **one class spans many devices /
  UI modes / font scales** — no more one class per combination. `:sample` has one per-concern
  `*SnapshotTests` class per feature (default matrix, exclude, profile / inline overrides, widening,
  precedence); see those and `README.md`.
- Golden images live in `sample/src/test/snapshots/images/`. Record with
  `./gradlew cleanRecordPaparazziDebug`; verify with `./gradlew verifyPaparazziDebug`. Commit golden
  changes with the code that changed them.
- **Goldens are stored in Git LFS** — `.gitattributes` tracks `*.png` (repo-wide) with the LFS filter.
  Contributors need `git-lfs` installed (`git lfs install`) so checkouts smudge pointers back into real
  PNGs; otherwise `verifyPaparazziDebug` compares against pointer stubs. CI checkouts set `lfs: true`
  (see `.github/workflows/`).
- Test class naming: `*SnapshotTests`.
- **`build-logic` tests** (the real unit-test suite) are the model for non-snapshot tests: Kotest
  `FunSpec`, `withData` for data-driven cases, **hand-written test doubles only** (suffix `*Stub`) —
  no MockK; project fixtures via `buildProject()` / `Factories` under `build-logic/src/test/.../testutil`.
- **`:framework` has its own unit-test suite** (`framework/src/test`, Kotest `FunSpec`, hand-written
  doubles — no MockK): the resolver, DSL building/validation, id assignment, token parsing, naming,
  dedup/validation errors, the token→enum mapping, and `AckeeSnapshotTests` wiring (via a
  `FakeSnapshotEngine`). `:paparazzi` unit-tests its pure mappings (`PaparazziMappingTest`).
  `:annotations` has no unit tests. Rendering itself is exercised end-to-end through the `:sample`
  snapshots — add rendering / e2e coverage there.

## Verification & Publishing

**Before every PR**, run the preflight gate (see `CONTRIBUTING.MD`):

```
./gradlew preMergeRequestCheck
```

It runs, across the relevant projects: `detekt` + `assembleRelease` + library `testDebugUnitTest` +
sample `verifyPaparazziDebug` + `checkLegacyAbi` + build-logic `:test`. It is kept **in sync with CI**
(`.github/actions/basic-preflight-check`, `.github/workflows/`) — if you change what it runs, update
CI too (the task source comments say so explicitly).

Release procedure lives in `RELEASING.md`. Mental model:

- Every artifact has an independent version in `lib.properties` (`ANNOTATIONS_VERSION`,
  `FRAMEWORK_VERSION`, `PAPARAZZI_VERSION`, `BOM_VERSION`); the BOM pins a compatible set.
- `checkIfUpdateNeededSinceCurrentTag` — lists artifacts changed since the last tag.
- `verifyPublishing` — fails if an internal-only change forces co-releasing dependents (protects
  binary compatibility between artifacts linked against the same internal code).
- `verifyBomVersion` — fails if the BOM version doesn't match the pushed git tag.
- `prePublishCheck` — `preMergeRequestCheck` + `verifyPublishing` + `verifyBomVersion`; run before
  pushing a tag (synced with `deploy.yml`).
- Publishing (vanniktech + Dokka) **probes Maven Central (`repo1.maven.org`) first**: 404 → publish,
  2xx → skip (already published), any other status → fail. Re-pushing the same tag publishes nothing;
  to re-publish, bump the version — never force. Signing needs GPG secrets provided by CI only; local
  publishing fails signing, which is expected.
- **Paparazzi is currently on an unstable release**, so the `paparazzi` and `bom` artifacts carry a
  `-paparazzialpha…` version suffix. When Paparazzi goes stable, drop the suffix (and this note) per
  `RELEASING.md`.

### Android/KMP Verification Tasks

- Assemble: `assembleDebug`
- Detekt: `detektDebug`
- Unit tests: `testDebugUnitTest`
- Snapshot tests: `verifyPaparazziDebug`

## Code Style

- Blank line after a type-body opening brace, before the first member — applies to `class`,
  `interface`, `sealed interface`, `object` / `data object`, `enum class`. **Does not** apply to
  lambda/DSL bodies (e.g. `FunSpec({ … })`, `apply { }`).
- Max line length **150** (Detekt, `detekt-config.yml`, run with `detekt-formatting`). Some rules are
  relaxed there (`TooManyFunctions`, `TooGenericExceptionCaught`, `UnnecessaryAbstractClass` off).
- No wildcard imports.
- Idioms in use: `sealed interface` + `data object` / `data class` for closed unions, `enum class`
  for finite sets, `internal` for impl types. Main modules keep sources under `src/main/java/…`
  (Kotlin files in a `java` dir); `build-logic` uses `src/main/kotlin/…`.

## Plans

At the end of each plan, give me a list of unresolved questions to answer, if any. Make the questions
extremely concise. Sacrifice grammar for the sake of concision. Use the AskUserQuestion tool.
