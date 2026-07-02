package io.github.ackeecz.snapshots.framework.dsl

import android.content.Context
import androidx.compose.runtime.Composable
import com.airbnb.android.showkase.models.ShowkaseBrowserComponent
import io.github.ackeecz.snapshots.framework.DeviceConfig
import io.github.ackeecz.snapshots.framework.FontScale
import io.github.ackeecz.snapshots.framework.ProfileOverride
import io.github.ackeecz.snapshots.framework.SnapshotConfig
import io.github.ackeecz.snapshots.framework.SnapshotConfigException
import io.github.ackeecz.snapshots.framework.SnapshotVariant
import io.github.ackeecz.snapshots.framework.UiMode

internal class VariantsScopeImpl : VariantsScope {

    private var componentsEnabled = false
    private var devices: List<DeviceConfig> = emptyList()
    private var uiModes: List<UiMode>? = null
    private var fontScales: List<FontScale>? = null
    private val excludes = mutableListOf<(SnapshotVariant) -> Boolean>()
    private val profiles = mutableMapOf<String, ProfileOverride>()

    override fun components() {
        componentsEnabled = true
    }

    override fun screens(vararg devices: DeviceConfig) {
        this.devices = devices.toList()
    }

    override fun uiModes(vararg modes: UiMode) {
        this.uiModes = modes.toList()
    }

    override fun fontScales(vararg scales: FontScale) {
        this.fontScales = scales.toList()
    }

    override fun exclude(predicate: (SnapshotVariant) -> Boolean) {
        excludes += predicate
    }

    override fun profile(key: String, block: ProfileOverrideScope.() -> Unit) {
        profiles[key] = ProfileOverrideScopeImpl().apply(block).build()
    }

    fun build(
        previews: List<ShowkaseBrowserComponent>,
        before: (Context) -> Unit,
        decorate: @Composable (UiMode, @Composable () -> Unit) -> Unit,
    ): SnapshotConfig {
        val uiModes = uiModes.required(axis = "uiModes")
        val fontScales = fontScales.required(axis = "fontScales")
        return SnapshotConfig(
            previews = previews,
            componentsEnabled = componentsEnabled,
            devices = devices,
            uiModes = uiModes,
            fontScales = fontScales,
            excludes = excludes.toList(),
            profiles = profiles.toMap(),
            before = before,
            decorate = decorate,
        )
    }

    private fun <T> List<T>?.required(axis: String): List<T> =
        this?.takeIf { it.isNotEmpty() }
            ?: throw SnapshotConfigException("variants { $axis(...) } requires at least one value")
}
