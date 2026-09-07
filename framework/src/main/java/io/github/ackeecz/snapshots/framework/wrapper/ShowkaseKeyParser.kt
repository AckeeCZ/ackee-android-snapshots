package io.github.ackeecz.snapshots.framework.wrapper

import com.airbnb.android.showkase.models.ShowkaseBrowserComponent

/**
 * Turns a Showkase component key into the codegen locations the component may have been generated
 * into. Several candidates exist only when an underscore or a nested class makes a split ambiguous.
 */
internal class ShowkaseKeyParser {

    fun candidates(component: ShowkaseBrowserComponent): List<ComponentLocation> {
        val group = component.group.withoutWhitespace()
        val name = component.componentName.withoutWhitespace()
        val style = component.styleName?.withoutWhitespace() ?: NULL_TOKEN
        val tail = tailRegex(group, name, style).find(component.componentKey) ?: return emptyList()
        val head = component.componentKey.substring(0, tail.range.first)
        val propertyName = { functionName: String ->
            propertyName(functionName, group, name, tail.groupValues[1].toInt(), component.styleName)
        }
        return head.underscoreIndices().flatMap { splitIndex ->
            val enclosingClass = head.substring(splitIndex + 1)
            val prefix = head.substring(0, splitIndex)
            if (enclosingClass == NULL_TOKEN) {
                topLevelCandidates(prefix, propertyName)
            } else {
                enclosedCandidates(prefix, enclosingClass, propertyName)
            }
        }
    }

    private fun topLevelCandidates(prefix: String, propertyName: (String) -> String) = prefix.underscoreIndices().map { splitIndex ->
        location(prefix.substring(0, splitIndex), prefix.substring(splitIndex + 1), propertyName)
    }

    private fun enclosedCandidates(prefix: String, enclosingClass: String, propertyName: (String) -> String): List<ComponentLocation> {
        if (!prefix.startsWith("${enclosingClass}_")) return emptyList()
        val functionName = prefix.substring(enclosingClass.length + 1)
        return packageCandidatesOf(enclosingClass).map { location(it, functionName, propertyName) }
    }

    // The enclosing class is a canonical name, so its trailing capitalized segments may be nested classes or packages.
    private fun packageCandidatesOf(enclosingClass: String): List<String> {
        val segments = enclosingClass.split('.')
        val classSegments = segments.asReversed().takeWhile { it.firstOrNull()?.isUpperCase() == true }.size
        return (1..classSegments).map { segments.dropLast(it).joinToString(".") }
    }

    private fun location(packageName: String, functionName: String, propertyName: (String) -> String) = ComponentLocation(
        packageName = packageName,
        propertyName = propertyName(functionName),
        functionName = functionName,
    )

    private fun propertyName(functionName: String, group: String, name: String, componentIndex: Int, styleName: String?): String {
        val index = if (componentIndex > 0) "_$componentIndex" else ""
        val style = styleName?.let { "_$it" } ?: ""
        return "${functionName}_${group}_$name$index$style".filter { it.isLetterOrDigit() }
    }

    private fun tailRegex(group: String, name: String, style: String) = Regex(
        "_${Regex.escape(group)}_${Regex.escape(name)}_(\\d+)_${Regex.escape(style)}(?:_\\d+)?$",
    )

    // Descending, so the longest package (the shortest function name) is the first candidate.
    private fun String.underscoreIndices(): List<Int> = indices.filter { this[it] == '_' }.reversed()

    private fun String.withoutWhitespace() = filterNot { it.isWhitespace() }

    private companion object {

        const val NULL_TOKEN = "null"
    }
}
