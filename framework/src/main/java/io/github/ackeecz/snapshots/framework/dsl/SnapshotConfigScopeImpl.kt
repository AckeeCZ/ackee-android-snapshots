package io.github.ackeecz.snapshots.framework.dsl

import android.content.Context
import androidx.compose.runtime.Composable
import com.airbnb.android.showkase.models.ShowkaseBrowserComponent
import com.airbnb.android.showkase.models.ShowkaseElementsMetadata
import io.github.ackeecz.snapshots.framework.NO_OP_BEFORE
import io.github.ackeecz.snapshots.framework.SnapshotConfig
import io.github.ackeecz.snapshots.framework.SnapshotConfigException
import io.github.ackeecz.snapshots.framework.UiMode

internal class SnapshotConfigScopeImpl : SnapshotConfigScope {

    private var previews: List<ShowkaseBrowserComponent>? = null
    private var before: (Context) -> Unit = NO_OP_BEFORE
    private var decorate: (@Composable (UiMode, @Composable () -> Unit) -> Unit)? = null
    private var variantsScope: VariantsScopeImpl? = null

    override fun previews(metadata: ShowkaseElementsMetadata) {
        previews = metadata.componentList
    }

    override fun before(block: (Context) -> Unit) {
        before = block
    }

    override fun decorate(block: @Composable (UiMode, @Composable () -> Unit) -> Unit) {
        decorate = block
    }

    override fun variants(block: VariantsScope.() -> Unit) {
        variantsScope = VariantsScopeImpl().apply(block)
    }

    fun build(): SnapshotConfig {
        val previews = previews.orThrow("previews(...) is required")
        val decorate = decorate.orThrow("decorate { } is required")
        val variants = variantsScope.orThrow("variants { } block is required")
        return variants.build(previews = previews, before = before, decorate = decorate)
    }

    private fun <T> T?.orThrow(message: String): T = this ?: throw SnapshotConfigException(message)
}
