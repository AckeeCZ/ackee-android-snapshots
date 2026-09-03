package io.github.ackeecz.snapshots.framework.wrapper

import com.airbnb.android.showkase.models.ShowkaseBrowserComponent
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private lateinit var underTest: ShowkaseKeyParser

internal class ShowkaseKeyParserTest : FunSpec({

    beforeEach {
        underTest = ShowkaseKeyParser()
    }

    fun propertyNamesFor(component: ShowkaseBrowserComponent) = underTest.candidates(component).map { it.propertyName }

    test("a plain component yields its package, property name and function name") {
        val component = fixtureComponent(key = "io.example.ui_CardPreview_null_Cards_Card_0_null")

        underTest.candidates(component) shouldBe listOf(ComponentLocation("io.example.ui", "CardPreviewCardsCard", "CardPreview"))
    }

    test("group and name keep only letters and digits in the property name") {
        val component = fixtureComponent(
            key = "io.example.ui_CardPreview_null_DefaultGroup_Article-Card_0_null",
            group = "Default Group",
            name = "Article-Card",
        )

        propertyNamesFor(component) shouldBe listOf("CardPreviewDefaultGroupArticleCard")
    }

    test("a style name is appended to the property name") {
        val component = fixtureComponent(key = "io.example.ui_CardPreview_null_Cards_Card_0_PRIMARY", styleName = "PRIMARY")

        propertyNamesFor(component) shouldBe listOf("CardPreviewCardsCardPRIMARY")
    }

    test("a component index above zero is part of the property name") {
        val component = fixtureComponent(key = "io.example.ui_CardPreview_null_Cards_Card_1_null")

        propertyNamesFor(component) shouldBe listOf("CardPreviewCardsCard1")
    }

    test("a preview-parameter key with a trailing element index parses like its base key") {
        val component = fixtureComponent(key = "io.example.ui_CardPreview_null_Cards_Card_0_null_1")

        underTest.candidates(component) shouldBe listOf(ComponentLocation("io.example.ui", "CardPreviewCardsCard", "CardPreview"))
    }

    test("a malformed key yields no candidates") {
        val component = fixtureComponent(key = "io.example.ui_CardPreview_null")

        underTest.candidates(component) shouldBe emptyList()
    }

    test("an underscore in the function name yields a candidate per split") {
        val component = fixtureComponent(key = "io.example.ui_Snake_CasePreview_null_Cards_Card_0_null")

        underTest.candidates(component) shouldBe listOf(
            ComponentLocation("io.example.ui_Snake", "CasePreviewCardsCard", "CasePreview"),
            ComponentLocation("io.example.ui", "SnakeCasePreviewCardsCard", "Snake_CasePreview"),
        )
    }

    test("an underscore in the package name yields a candidate per split") {
        val component = fixtureComponent(key = "io.my_app.ui_CardPreview_null_Cards_Card_0_null")

        underTest.candidates(component) shouldBe listOf(
            ComponentLocation("io.my_app.ui", "CardPreviewCardsCard", "CardPreview"),
            ComponentLocation("io.my", "appuiCardPreviewCardsCard", "app.ui_CardPreview"),
        )
    }

    test("an object-nested component yields the enclosing class's package") {
        val component = fixtureComponent(key = "io.example.ui.Gallery_ObjectPreview_io.example.ui.Gallery_Cards_Card_0_null")

        underTest.candidates(component) shouldBe listOf(ComponentLocation("io.example.ui", "ObjectPreviewCardsCard", "ObjectPreview"))
    }

    test("a nested enclosing class yields a candidate per package split") {
        val component = fixtureComponent(
            key = "io.example.ui.Outer.Inner_NestedPreview_io.example.ui.Outer.Inner_Cards_Card_0_null",
        )

        underTest.candidates(component).map { it.packageName } shouldBe listOf("io.example.ui.Outer", "io.example.ui")
    }

    test("an enclosed key whose prefix and enclosing class disagree yields no candidates") {
        val component = fixtureComponent(key = "io.example.ui.Other_ObjectPreview_io.example.ui.Gallery_Cards_Card_0_null")

        underTest.candidates(component) shouldBe emptyList()
    }
})
