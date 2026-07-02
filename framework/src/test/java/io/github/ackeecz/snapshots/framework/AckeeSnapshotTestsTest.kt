package io.github.ackeecz.snapshots.framework

import io.github.ackeecz.snapshots.framework.dsl.SnapshotConfigScope
import io.github.ackeecz.snapshots.framework.dsl.SnapshotConfigScopeImpl
import io.kotest.common.KotestInternal
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder

@OptIn(KotestInternal::class)
internal class AckeeSnapshotTestsTest : FunSpec({

    val configBlock: SnapshotConfigScope.() -> Unit = {
        previews(
            metadataOf(
                componentTagged(group = "A", name = "One"),
                screenTagged(group = "B", name = "Two"),
            ),
        )
        decorate { _, content -> content() }
        variants {
            components()
            screens(Device.Pixel6.portrait, Device.Nexus10.landscape)
            uiModes(UiMode.LIGHT, UiMode.DARK)
            fontScales(FontScale.NORMAL)
        }
    }

    val resolved = SnapshotResolver().resolve(SnapshotConfigScopeImpl().apply(configBlock).build())
    val expectedGroups = resolved.map { Triple(it.variant.kind, it.variant.device, it.variant.uiMode) }.distinct()
    val expectedGroupNames = expectedGroups.map { (_, device, uiMode) -> "${snapshotTargetLabel(device)}_$uiMode" }
    val expectedNames = resolved.map { it.name }

    fun wiringSpec(engine: SnapshotEngine): AckeeSnapshotTests = object : AckeeSnapshotTests(engine, configBlock) {}

    fun launchSpec(spec: Spec): CollectingTestEngineListener {
        val collector = CollectingTestEngineListener()
        TestEngineLauncher()
            .withListener(collector)
            .withSpecRefs(SpecRef.Function({ spec }, spec::class, spec::class.java.name))
            .launch()
        return collector
    }

    test("one context is opened per (kind, device, uiMode) group") {
        val rootTests = wiringSpec(FakeSnapshotEngine()).rootTests()

        rootTests.map { it.name.name } shouldContainExactlyInAnyOrder expectedGroupNames
    }

    test("engine.init is called once per group with that group's kind, device and uiMode") {
        val engine = FakeSnapshotEngine()

        launchSpec(wiringSpec(engine))

        engine.initCalls shouldContainExactlyInAnyOrder expectedGroups
    }

    test("one test per ResolvedSnapshot is registered under its group's context") {
        val collector = launchSpec(wiringSpec(FakeSnapshotEngine()))

        collector.names shouldContainAll expectedNames
    }

    test("each test invokes engine.snapshot with the resolver's golden name") {
        val engine = FakeSnapshotEngine()

        launchSpec(wiringSpec(engine))

        engine.snapshotNames shouldContainExactlyInAnyOrder expectedNames
    }
})
