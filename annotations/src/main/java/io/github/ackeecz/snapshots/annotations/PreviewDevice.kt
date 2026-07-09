package io.github.ackeecz.snapshots.annotations

/**
 * Per-preview inline device override tokens. Tagging a screen preview with one (or more) of these
 * restricts (or widens) the devices it is snapshot on, overriding the profile and class config for
 * that preview only. Ignored for component previews.
 */
object PreviewDevice {

    const val Pixel6Portrait = "device=PIXEL_6_PORTRAIT"
    const val Pixel6Landscape = "device=PIXEL_6_LANDSCAPE"
    const val Nexus10Portrait = "device=NEXUS_10_PORTRAIT"
    const val Nexus10Landscape = "device=NEXUS_10_LANDSCAPE"
}
