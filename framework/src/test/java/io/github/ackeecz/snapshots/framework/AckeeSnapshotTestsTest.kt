package io.github.ackeecz.snapshots.framework

import io.github.ackeecz.snapshots.framework.dsl.SnapshotConfigScope
import io.github.ackeecz.snapshots.framework.dsl.SnapshotConfigScopeImpl
import io.github.ackeecz.snapshots.framework.wrapper.FakePreviewWrapperFactory
import io.github.ackeecz.snapshots.framework.wrapper.FakePreviewWrapperResolver
import io.github.ackeecz.snapshots.framework.wrapper.RecordingWrapperResolverFactory
import io.github.ackeecz.snapshots.framework.wrapper.WrapperResolution
import io.kotest.common.KotestInternal
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

private const val WRAPPED_KEY = "wrapped-key"
private const val UNWRAPPED_KEY = "unwrapped-key"
private const val WRAPPED_ID = "A_One"
private const val UNWRAPPED_ID = "B_Two"

@OptIn(KotestInternal::class)
internal class AckeeSnapshotTestsTest : FunSpec({

    val configBlock: SnapshotConfigScope.() -> Unit = {
        previews(
            metadataOf(
                componentTagged(group = "A", name = "One", key = WRAPPED_KEY),
                screenTagged(group = "B", name = "Two", key = UNWRAPPED_KEY),
            ),
        )
        decorate { _, content -> content() }
        variants {
            components()
            screens(Device.Pixel6.portrait, Device.Nexus10.landscape)
            uiModes(UiMode.LIGHT, UiMode.DARK)
            fontScales(FontScale.NORMAL, FontScale.LARGE)
        }
    }

    fun resolverWith(wrapperFactory: FakePreviewWrapperFactory) = SnapshotResolver(
        wrapperResolverFactory = RecordingWrapperResolverFactory(
            FakePreviewWrapperResolver(
                mapOf(
                    WRAPPED_KEY to WrapperResolution.Wrapped(wrapperFactory),
                    UNWRAPPED_KEY to WrapperResolution.Unwrapped,
                ),
            ),
        ),
    )

    val resolved = resolverWith(FakePreviewWrapperFactory()).resolve(SnapshotConfigScopeImpl().apply(configBlock).build())
    val expectedGroups = resolved.map { Triple(it.variant.kind, it.variant.device, it.variant.uiMode) }.distinct()
    val expectedGroupNames = expectedGroups.map { (_, device, uiMode) -> "${snapshotTargetLabel(device)}_$uiMode" }
    val expectedNames = resolved.map { it.name }

    fun wiringSpec(
        engineFactory: () -> SnapshotEngine,
        wrapperFactory: FakePreviewWrapperFactory = FakePreviewWrapperFactory(),
    ): AckeeSnapshotTests = object : AckeeSnapshotTests(engineFactory, configBlock, resolverWith(wrapperFactory)) {}

    suspend fun launchSpec(spec: Spec): CollectingTestEngineListener {
        val collector = CollectingTestEngineListener()
        TestEngineLauncher()
            .withListener(collector)
            .withSpecRefs(SpecRef.Function({ spec }, spec::class, spec::class.java.name))
            .execute()
        return collector
    }

    suspend fun launchRecordingSpec(): FakeSnapshotEngineFactory {
        val factory = FakeSnapshotEngineFactory()
        launchSpec(wiringSpec(factory))
        return factory
    }

    test("one context is opened per (kind, device, uiMode) group") {
        val rootTests = wiringSpec(FakeSnapshotEngineFactory()).tests()

        rootTests.map { it.name.name } shouldContainExactlyInAnyOrder expectedGroupNames
    }

    test("a fresh engine is created and initialised once per group") {
        val factory = launchRecordingSpec()

        factory.engines.size shouldBe expectedGroups.size
        factory.engines.map { it.initCalls.size } shouldBe List(expectedGroups.size) { 1 }
    }

    test("engine.init is called once per group with that group's kind, device and uiMode") {
        val factory = launchRecordingSpec()

        factory.allInitCalls shouldContainExactlyInAnyOrder expectedGroups
    }

    test("one test per ResolvedSnapshot is registered under its group's context") {
        val collector = launchSpec(wiringSpec(FakeSnapshotEngineFactory()))

        collector.names shouldContainAll expectedNames
    }

    test("each test invokes engine.snapshot with the resolver's golden name") {
        val factory = launchRecordingSpec()

        factory.allSnapshotNames shouldContainExactlyInAnyOrder expectedNames
    }

    context("wrapper lifecycle per snapshot") {

        suspend fun launchWithWrapper(): FakePreviewWrapperFactory {
            val wrapperFactory = FakePreviewWrapperFactory()
            launchSpec(wiringSpec(FakeSnapshotEngineFactory(), wrapperFactory))
            return wrapperFactory
        }

        // Counted by preview id, not by `wrapper != null`: an expectation derived from the wrapper attachment
        // would drift along with any bug in it, and so could never contradict the production code.
        val wrappedVariantCount = resolved.count { it.name.startsWith(WRAPPED_ID) }
        val unwrappedVariantCount = resolved.count { it.name.startsWith(UNWRAPPED_ID) }

        test("a wrapped preview creates one wrapper instance per registered test") {
            launchWithWrapper().created shouldHaveSize wrappedVariantCount
        }

        test("every created wrapper is a distinct instance") {
            launchWithWrapper().created.toSet() shouldHaveSize wrappedVariantCount
        }

        test("an unwrapped preview never touches a wrapper factory") {
            val created = launchWithWrapper().created

            created shouldHaveSize wrappedVariantCount
            resolved.count { it.wrapper == null } shouldBe unwrappedVariantCount
        }
    }
})
