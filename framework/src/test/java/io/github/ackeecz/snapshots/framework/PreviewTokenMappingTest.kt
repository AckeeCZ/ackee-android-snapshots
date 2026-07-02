package io.github.ackeecz.snapshots.framework

import io.github.ackeecz.snapshots.annotations.PreviewFontScale
import io.github.ackeecz.snapshots.annotations.PreviewUiMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val parser = ExtraMetadataParser()

private fun parse(token: String) = parser.parse(listOf(token), profileKeys = emptySet())

private fun stringConstantsOf(holder: Class<*>): List<String> = holder.declaredFields
    .filter { it.type == String::class.java }
    .map { it.get(null) as String }

internal class PreviewTokenMappingTest : FunSpec({

    test("every PreviewFontScale constant maps to a FontScale") {
        stringConstantsOf(PreviewFontScale::class.java).forEach { token ->
            parse(token).inlineFontScales?.size shouldBe 1
        }
    }

    test("every PreviewUiMode constant maps to a UiMode") {
        stringConstantsOf(PreviewUiMode::class.java).forEach { token ->
            parse(token).inlineUiModes?.size shouldBe 1
        }
    }

    test("every FontScale is reachable from a PreviewFontScale constant") {
        val reachable = stringConstantsOf(PreviewFontScale::class.java)
            .flatMap { parse(it).inlineFontScales.orEmpty() }
            .toSet()

        reachable shouldBe FontScale.entries.toSet()
    }

    test("every UiMode is reachable from a PreviewUiMode constant") {
        val reachable = stringConstantsOf(PreviewUiMode::class.java)
            .flatMap { parse(it).inlineUiModes.orEmpty() }
            .toSet()

        reachable shouldBe UiMode.entries.toSet()
    }
})
