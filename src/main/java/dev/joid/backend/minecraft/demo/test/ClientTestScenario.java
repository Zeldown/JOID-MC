package dev.joid.backend.minecraft.demo.test;

import dev.joid.backend.minecraft.demo.test.scenario.ContainerScenarios;
import dev.joid.backend.minecraft.demo.test.scenario.HostScenarios;
import dev.joid.backend.minecraft.demo.test.scenario.ObjectScenarios;
import dev.joid.backend.minecraft.demo.test.scenario.OverlayScenarios;
import dev.joid.backend.minecraft.demo.test.scenario.RenderScenarios;
import dev.joid.backend.minecraft.demo.test.scenario.ScreenScenarios;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ClientTestScenario {

	CONTRACTS(ClientTestStage.TITLE, RenderScenarios::verifyContracts),
	SNAPSHOTS(ClientTestStage.TITLE, RenderScenarios::verifySnapshots),
	KEY_BIND_LABELS(ClientTestStage.TITLE, HostScenarios::verifyKeyBindLabels),
	ENTITIES_WITHOUT_WORLD(ClientTestStage.TITLE, ObjectScenarios::verifyEntitiesWithoutWorld),
	BACKEND(ClientTestStage.WORLD, HostScenarios::verifyBackend),
	STENCIL(ClientTestStage.WORLD, HostScenarios::verifyStencil),
	AUDIO_CONTEXT(ClientTestStage.WORLD, HostScenarios::verifyAudioContext),
	KEY_BIND(ClientTestStage.WORLD, HostScenarios::verifyKeyBind),
	DEBUG_KEY(ClientTestStage.WORLD, HostScenarios::verifyDebugKey),
	DEMO_CHOICE(ClientTestStage.WORLD, ScreenScenarios::verifyDemoChoice),
	HOST_BINDINGS(ClientTestStage.WORLD, HostScenarios::verifyHostBindings),
	DEMO_CAPTURES(ClientTestStage.WORLD, ScreenScenarios::verifyDemoCaptures),
	MINECRAFT_DATA(ClientTestStage.WORLD, ScreenScenarios::verifyMinecraftData),
	SCREEN_TITLE(ClientTestStage.WORLD, ScreenScenarios::verifyScreenTitle),
	RESOURCES(ClientTestStage.WORLD, ObjectScenarios::verifyResources),
	RELOAD(ClientTestStage.WORLD, ObjectScenarios::verifyReload),
	ITEMS(ClientTestStage.WORLD, ObjectScenarios::verifyItems),
	ITEM_COOLDOWN(ClientTestStage.WORLD, ObjectScenarios::verifyItemCooldown),
	ENTITIES(ClientTestStage.WORLD, ObjectScenarios::verifyEntities),
	SOUND(ClientTestStage.WORLD, ObjectScenarios::verifySound),
	SCALE(ClientTestStage.WORLD, ScreenScenarios::verifyScale),
	AUDIO_MASTER(ClientTestStage.WORLD, HostScenarios::verifyAudioMaster),
	AUDIO_INTERFACE(ClientTestStage.WORLD, HostScenarios::verifyAudioInterface),
	FONT_WIDTHS(ClientTestStage.WORLD, RenderScenarios::verifyFontWidths),
	BITMAP_FONT(ClientTestStage.WORLD, RenderScenarios::verifyBitmapFont),
	DEPTH(ClientTestStage.WORLD, RenderScenarios::verifyDepth),
	BACKGROUND(ClientTestStage.WORLD, ScreenScenarios::verifyBackground),
	SUBTITLES(ClientTestStage.WORLD, ScreenScenarios::verifySubtitles),
	TOOLTIP(ClientTestStage.WORLD, ScreenScenarios::verifyTooltip),
	GAME_OBJECTS(ClientTestStage.WORLD, ObjectScenarios::verifyGameObjects),
	DEMOS(ClientTestStage.WORLD, ScreenScenarios::verifyDemos),
	OVERLAYS(ClientTestStage.WORLD, OverlayScenarios::verifyOverlays),
	SCREEN_INPUT(ClientTestStage.WORLD, OverlayScenarios::verifyScreenInput),
	RELOAD_KEYS(ClientTestStage.WORLD, OverlayScenarios::verifyReloadKeys),
	CONTAINER(ClientTestStage.WORLD, ContainerScenarios::verifyContainer),
	CONTAINER_SERVER(ClientTestStage.WORLD, ContainerScenarios::verifyContainerServer),
	BLOCK_LIGHTING(ClientTestStage.WORLD, ObjectScenarios::verifyBlockLighting),
	NESTED_RASTER(ClientTestStage.WORLD, RenderScenarios::verifyNestedRaster),
	NESTED_FEATURE_RASTER(ClientTestStage.WORLD, RenderScenarios::verifyNestedFeatureRaster),
	CURSOR(ClientTestStage.WORLD, ScreenScenarios::verifyCursor),
	FRAME_COUNTER(ClientTestStage.WORLD, OverlayScenarios::verifyFrameCounter),
	ENTITY_BOUNDS(ClientTestStage.WORLD, ObjectScenarios::verifyEntityBounds),
	SHOWCASE_CLOCK(ClientTestStage.WORLD, HostScenarios::verifyShowcaseClock),
	WARNINGS(ClientTestStage.WORLD, HostScenarios::verifyWarnings);

	private final ClientTestStage stage;
	private final ClientTestStep  step;

}