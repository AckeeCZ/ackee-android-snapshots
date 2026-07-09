package io.github.ackeecz.snapshots.framework

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

internal class VariantNameTest : FunSpec({

    test("component variant name omits the device and is id_uiMode_FontScale.value") {
        val name = variantName(
            id = "Buttons_Primary",
            variant = SnapshotVariant(
                kind = SnapshotKind.Component,
                device = null,
                uiMode = UiMode.LIGHT,
                fontScale = FontScale.NORMAL,
            ),
        )

        name shouldBe "Buttons_Primary_LIGHT_FontScale-NORMAL"
    }

    test("screen variant name uses the source-mirroring device label") {
        val name = variantName(
            id = "Home_Screen",
            variant = SnapshotVariant(
                kind = SnapshotKind.Screen,
                device = DeviceConfig(device = Device.PIXEL_6, orientation = DeviceOrientation.PORTRAIT),
                uiMode = UiMode.LIGHT,
                fontScale = FontScale.LARGE,
            ),
        )

        name shouldBe "Home_Screen_Pixel6-portrait_LIGHT_FontScale-LARGE"
    }

    test("light and dark of the same preview produce distinct names") {
        fun name(uiMode: UiMode) = variantName(
            id = "Card_Info",
            variant = SnapshotVariant(
                kind = SnapshotKind.Component,
                device = null,
                uiMode = uiMode,
                fontScale = FontScale.NORMAL,
            ),
        )

        name(UiMode.LIGHT) shouldBe "Card_Info_LIGHT_FontScale-NORMAL"
        name(UiMode.DARK) shouldBe "Card_Info_DARK_FontScale-NORMAL"
        name(UiMode.LIGHT) shouldNotBe name(UiMode.DARK)
    }
})
