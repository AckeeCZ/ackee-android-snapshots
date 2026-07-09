package io.github.ackeecz.snapshots.framework.dsl

import androidx.compose.runtime.Composable
import com.airbnb.android.showkase.models.ShowkaseBrowserComponent
import io.github.ackeecz.snapshots.framework.Device
import io.github.ackeecz.snapshots.framework.FontScale
import io.github.ackeecz.snapshots.framework.NO_OP_BEFORE
import io.github.ackeecz.snapshots.framework.ProfileOverride
import io.github.ackeecz.snapshots.framework.SnapshotConfig
import io.github.ackeecz.snapshots.framework.SnapshotConfigException
import io.github.ackeecz.snapshots.framework.UiMode
import io.github.ackeecz.snapshots.framework.componentTagged
import io.github.ackeecz.snapshots.framework.metadataOf
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

private val PIXEL_PORTRAIT = Device.Pixel6.portrait
private val NEXUS_LANDSCAPE = Device.Nexus10.landscape
private val customDecorate: @Composable (UiMode, @Composable () -> Unit) -> Unit = { _, content -> content() }

internal class SnapshotConfigScopeTest : FunSpec({

    fun buildConfig(block: SnapshotConfigScope.() -> Unit): SnapshotConfig =
        SnapshotConfigScopeImpl().apply(block).build()

    fun SnapshotConfigScope.requiredBase(preview: ShowkaseBrowserComponent = componentTagged()) {
        previews(metadataOf(preview))
        decorate(customDecorate)
    }

    fun configWith(variants: VariantsScope.() -> Unit): SnapshotConfig = buildConfig {
        requiredBase()
        variants(variants)
    }

    val minimalConfig = configWith {
        components()
        uiModes(UiMode.LIGHT)
        fontScales(FontScale.NORMAL)
    }

    test("previews, uiModes and fontScales are captured") {
        val preview = componentTagged(group = "G", name = "P")

        val config = buildConfig {
            requiredBase(preview)
            variants {
                components()
                uiModes(UiMode.LIGHT, UiMode.DARK)
                fontScales(FontScale.NORMAL)
            }
        }

        config.previews shouldBe listOf(preview)
        config.uiModes shouldBe listOf(UiMode.LIGHT, UiMode.DARK)
        config.fontScales shouldBe listOf(FontScale.NORMAL)
    }

    test("components() enables components and screens() captures the devices") {
        val config = configWith {
            components()
            screens(PIXEL_PORTRAIT, NEXUS_LANDSCAPE)
            uiModes(UiMode.LIGHT)
            fontScales(FontScale.NORMAL)
        }

        config.componentsEnabled shouldBe true
        config.devices shouldBe listOf(PIXEL_PORTRAIT, NEXUS_LANDSCAPE)
    }

    test("omitting components() and screens() leaves both disabled") {
        val config = configWith {
            uiModes(UiMode.LIGHT)
            fontScales(FontScale.NORMAL)
        }

        config.componentsEnabled shouldBe false
        config.devices shouldBe emptyList()
    }

    test("exclude predicates and profiles are captured") {
        val config = configWith {
            components()
            uiModes(UiMode.LIGHT)
            fontScales(FontScale.NORMAL)
            exclude { it.uiMode == UiMode.DARK }
            exclude { it.fontScale == FontScale.LARGE }
            profile("compact") { fontScales(FontScale.SMALL) }
            profile("tabletOnly") { devices(NEXUS_LANDSCAPE) }
        }

        config.excludes.size shouldBe 2
        config.profiles shouldBe mapOf(
            "compact" to ProfileOverride(fontScales = listOf(FontScale.SMALL)),
            "tabletOnly" to ProfileOverride(devices = listOf(NEXUS_LANDSCAPE)),
        )
    }

    test("omitting previews throws SnapshotConfigException") {
        shouldThrow<SnapshotConfigException> {
            buildConfig {
                variants {
                    components()
                    uiModes(UiMode.LIGHT)
                    fontScales(FontScale.NORMAL)
                }
            }
        }
    }

    test("omitting the variants block throws") {
        shouldThrow<SnapshotConfigException> {
            buildConfig {
                requiredBase()
            }
        }
    }

    test("empty uiModes / fontScales throws") {
        shouldThrow<SnapshotConfigException> {
            configWith {
                components()
                uiModes()
                fontScales(FontScale.NORMAL)
            }
        }
        shouldThrow<SnapshotConfigException> {
            configWith {
                components()
                uiModes(UiMode.LIGHT)
                fontScales()
            }
        }
    }

    test("before defaults to a no-op") {
        minimalConfig.before shouldBeSameInstanceAs NO_OP_BEFORE
    }

    test("decorate is captured") {
        val config = buildConfig {
            requiredBase()
            variants {
                components()
                uiModes(UiMode.LIGHT)
                fontScales(FontScale.NORMAL)
            }
        }

        config.decorate shouldBeSameInstanceAs customDecorate
    }

    test("omitting decorate throws SnapshotConfigException") {
        shouldThrow<SnapshotConfigException> {
            buildConfig {
                previews(metadataOf(componentTagged()))
                variants {
                    components()
                    uiModes(UiMode.LIGHT)
                    fontScales(FontScale.NORMAL)
                }
            }
        }
    }
})
