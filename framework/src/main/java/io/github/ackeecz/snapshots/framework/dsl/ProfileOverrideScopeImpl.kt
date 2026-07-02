package io.github.ackeecz.snapshots.framework.dsl

import io.github.ackeecz.snapshots.framework.DeviceConfig
import io.github.ackeecz.snapshots.framework.FontScale
import io.github.ackeecz.snapshots.framework.ProfileOverride
import io.github.ackeecz.snapshots.framework.UiMode

internal class ProfileOverrideScopeImpl : ProfileOverrideScope {

    private var devices: List<DeviceConfig>? = null
    private var uiModes: List<UiMode>? = null
    private var fontScales: List<FontScale>? = null

    override fun devices(vararg devices: DeviceConfig) {
        this.devices = devices.toList()
    }

    override fun uiModes(vararg modes: UiMode) {
        this.uiModes = modes.toList()
    }

    override fun fontScales(vararg scales: FontScale) {
        this.fontScales = scales.toList()
    }

    fun build(): ProfileOverride {
        return ProfileOverride(
            devices = devices,
            uiModes = uiModes,
            fontScales = fontScales,
        )
    }
}
