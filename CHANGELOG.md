# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### annotations
### framework
### paparazzi



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
