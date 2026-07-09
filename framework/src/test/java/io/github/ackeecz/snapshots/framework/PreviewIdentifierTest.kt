package io.github.ackeecz.snapshots.framework

import com.airbnb.android.showkase.models.ShowkaseBrowserComponent
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private lateinit var underTest: PreviewIdentifier

internal class PreviewIdentifierTest : FunSpec({

    beforeEach {
        underTest = PreviewIdentifier()
    }

    test("single component in a group gets id group_componentName") {
        val previews = underTest.assignIds(listOf(component(group = "Buttons", name = "Primary")))

        previews.map { it.id } shouldBe listOf("Buttons_Primary")
    }

    test("different groups produce independent ids") {
        val previews = underTest.assignIds(
            listOf(
                component(group = "Buttons", name = "Primary"),
                component(group = "Cards", name = "Info"),
            ),
        )

        previews.map { it.id } shouldBe listOf("Buttons_Primary", "Cards_Info")
    }

    test("two components in the same group get _0 and _1 suffixes") {
        val previews = underTest.assignIds(
            listOf(
                component(group = "Buttons", name = "Primary", key = "a"),
                component(group = "Buttons", name = "Primary", key = "b"),
            ),
        )

        previews.map { it.id } shouldBe listOf("Buttons_Primary_0", "Buttons_Primary_1")
    }

    test("components sharing a group but not a name keep unsuffixed ids") {
        val previews = underTest.assignIds(
            listOf(
                component(group = "Buttons", name = "Primary"),
                component(group = "Buttons", name = "Secondary"),
            ),
        )

        previews.map { it.id } shouldBe listOf("Buttons_Primary", "Buttons_Secondary")
    }

    test("resolved previews are sorted by id ascending") {
        val previews = underTest.assignIds(
            listOf(
                component(group = "Zeta", name = "Widget"),
                component(group = "Alpha", name = "Widget"),
            ),
        )

        previews.map { it.id } shouldBe listOf("Alpha_Widget", "Zeta_Widget")
    }

    test("index suffix is assigned in stable input order") {
        val first = component(group = "Buttons", name = "Primary", key = "first")
        val second = component(group = "Buttons", name = "Primary", key = "second")

        val previews = underTest.assignIds(listOf(first, second))

        previews.map { it.component.componentKey } shouldBe listOf("first", "second")
        previews.map { it.id } shouldBe listOf("Buttons_Primary_0", "Buttons_Primary_1")
    }
})

private fun component(group: String, name: String, key: String = "$group:$name") =
    ShowkaseBrowserComponent(
        componentKey = key,
        group = group,
        componentName = name,
        componentKDoc = "",
        component = {},
    )
