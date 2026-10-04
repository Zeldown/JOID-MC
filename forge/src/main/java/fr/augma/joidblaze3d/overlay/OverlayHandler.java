package fr.augma.joidblaze3d.overlay;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.augma.joidblaze3d.Constants;
import fr.augma.joidblaze3d.screen.data.overlay.render.ElementType;
import fr.augma.joidblaze3d.screen.overlay.OverlayBridge;
import fr.augma.joidblaze3d.screen.overlay.OverlayLayerMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.overlay.ForgeLayer;
import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;

public final class OverlayHandler {

	private static final OverlayLayerMap                 LAYER_MAP = OverlayLayerMap.create()
			.map(ElementType.HELMET, ForgeLayeredDraw.CAMERA_OVERLAY)
			.map(ElementType.PORTAL, ForgeLayeredDraw.CAMERA_OVERLAY)
			.map(ElementType.CROSSHAIRS, ForgeLayeredDraw.CROSSHAIR)
			.map(ElementType.BOSSHEALTH, ForgeLayeredDraw.BOSS_OVERLAY)
			.map(ElementType.ARMOR, ForgeLayeredDraw.HEALTH_BAR)
			.map(ElementType.HEALTH, ForgeLayeredDraw.HEALTH_BAR)
			.map(ElementType.FOOD, ForgeLayeredDraw.HEALTH_BAR)
			.map(ElementType.AIR, ForgeLayeredDraw.HEALTH_BAR)
			.map(ElementType.HOTBAR, ForgeLayeredDraw.SPECTATOR_HOTBAR, ForgeLayeredDraw.ITEM_HOTBAR)
			.map(ElementType.EXPERIENCE, ForgeLayeredDraw.BACKGROUND, ForgeLayeredDraw.EXPERIENCE_LEVEL, ForgeLayeredDraw.CONTEXTUAL_INFO)
			.map(ElementType.TEXT, ForgeLayeredDraw.HOTBAR_MESSAGE)
			.map(ElementType.HEALTHMOUNT, ForgeLayeredDraw.VEHICLE_HEALTH)
			.map(ElementType.JUMPBAR, ForgeLayeredDraw.BACKGROUND, ForgeLayeredDraw.CONTEXTUAL_INFO)
			.map(ElementType.CHAT, ForgeLayeredDraw.CHAT_OVERLAY)
			.map(ElementType.PLAYER_LIST, ForgeLayeredDraw.TAB_LIST)
			.map(ElementType.EFFECTS, ForgeLayeredDraw.POTION_EFFECTS);
	private static final Map<Identifier, List<Identifier>> STACK_MAP = new LinkedHashMap<>();

	static {
		OverlayHandler.STACK_MAP.put(ForgeLayeredDraw.PRE_SLEEP_STACK, List.of(ForgeLayeredDraw.CAMERA_OVERLAY, ForgeLayeredDraw.CROSSHAIR, ForgeLayeredDraw.POTION_EFFECTS, ForgeLayeredDraw.BOSS_OVERLAY));
		OverlayHandler.STACK_MAP.put(ForgeLayeredDraw.HOTBAR_AND_DECOS, List.of(ForgeLayeredDraw.SPECTATOR_HOTBAR, ForgeLayeredDraw.ITEM_HOTBAR, ForgeLayeredDraw.HEALTH_BAR, ForgeLayeredDraw.VEHICLE_HEALTH, ForgeLayeredDraw.BACKGROUND, ForgeLayeredDraw.EXPERIENCE_LEVEL, ForgeLayeredDraw.CONTEXTUAL_INFO, ForgeLayeredDraw.SELECTED_ITEM_NAME, ForgeLayeredDraw.SPECTATOR_ACTION));
		OverlayHandler.STACK_MAP.put(ForgeLayeredDraw.POST_SLEEP_STACK, List.of(ForgeLayeredDraw.DEMO_OVERLAY, ForgeLayeredDraw.SCOREBOARD, ForgeLayeredDraw.HOTBAR_MESSAGE, ForgeLayeredDraw.TITLE_OVERLAY, ForgeLayeredDraw.CHAT_OVERLAY, ForgeLayeredDraw.TAB_LIST, ForgeLayeredDraw.SUBTITLE_OVERLAY));
		OverlayHandler.STACK_MAP.put(ForgeLayeredDraw.VANILLA_ROOT, List.of(ForgeLayeredDraw.SLEEP_OVERLAY, ForgeLayeredDraw.SUBTITLE_OVERLAY));
	}

	public static void register() {
		AddGuiOverlayLayersEvent.BUS.addListener((final AddGuiOverlayLayersEvent event) -> OverlayHandler.addLayers(event.getLayeredDraw()));
		ScreenEvent.Render.Pre.BUS.addListener((final ScreenEvent.Render.Pre event) -> OverlayBridge.inst().extractScreen(event.getGuiGraphics(), false));
		ScreenEvent.Render.Post.BUS.addListener((final ScreenEvent.Render.Post event) -> OverlayBridge.inst().extractScreen(event.getGuiGraphics(), true));
		ScreenEvent.MouseButtonPressed.Pre.BUS.addListener((final ScreenEvent.MouseButtonPressed.Pre event) -> OverlayBridge.inst().mousePressed(event.getButton()));
		ScreenEvent.MouseButtonReleased.Pre.BUS.addListener((final ScreenEvent.MouseButtonReleased.Pre event) -> OverlayBridge.inst().mouseReleased(event.getButton()));
		ScreenEvent.MouseDragged.Pre.BUS.addListener((final ScreenEvent.MouseDragged.Pre event) -> OverlayBridge.inst().mouseDragged());
		ScreenEvent.MouseScrolled.Pre.BUS.addListener((final ScreenEvent.MouseScrolled.Pre event) -> OverlayBridge.inst().mouseScrolled(event.getDeltaY()));
		ScreenEvent.KeyPressed.Pre.BUS.addListener((final ScreenEvent.KeyPressed.Pre event) -> OverlayBridge.inst().keyPressed(event.getInfo().key()));
		ScreenEvent.CharacterTyped.Pre.BUS.addListener((final ScreenEvent.CharacterTyped.Pre event) -> OverlayBridge.inst().charTyped(event.getInfo().codepoint()));
	}

	private static void addLayers(final ForgeLayeredDraw draw) {
		draw.addBelow(ForgeLayeredDraw.VANILLA_ROOT, OverlayHandler.getIdentifier(ElementType.ALL, false), ForgeLayeredDraw.PRE_SLEEP_STACK, (graphics, delta) -> OverlayBridge.inst().extract(graphics, ElementType.ALL, false, false));
		draw.add(ForgeLayeredDraw.VANILLA_ROOT, OverlayHandler.getIdentifier(ElementType.ALL, true), (graphics, delta) -> OverlayBridge.inst().extract(graphics, ElementType.ALL, true, false));
		for (final Map.Entry<ElementType, List<Identifier>> entry : OverlayHandler.LAYER_MAP.getLayerMap().entrySet()) {
			final ElementType type = entry.getKey();
			final Identifier first = entry.getValue().getFirst();
			final Identifier last = entry.getValue().getLast();
			draw.addBelow(OverlayHandler.getStack(first), OverlayHandler.getIdentifier(type, false), first, (graphics, delta) -> OverlayBridge.inst().extract(graphics, type, false, false));
			draw.addAbove(OverlayHandler.getStack(last), OverlayHandler.getIdentifier(type, true), last, (graphics, delta) -> OverlayBridge.inst().extract(graphics, type, true, false));
		}

		OverlayHandler.splitHealthBar(draw);
		for (final Map.Entry<Identifier, List<Identifier>> entry : OverlayHandler.STACK_MAP.entrySet()) {
			for (final Identifier layer : entry.getValue()) {
				draw.addConditionTo(entry.getKey(), layer, () -> !OverlayHandler.LAYER_MAP.isCancelled(layer));
			}
		}
	}

	private static void splitHealthBar(final ForgeLayeredDraw draw) {
		final ForgeLayeredDraw hotbar = draw.getChild(ForgeLayeredDraw.HOTBAR_AND_DECOS);
		final ForgeLayer layer = hotbar == null ? null : hotbar.getLayer(ForgeLayeredDraw.HEALTH_BAR);
		if (layer == null) {
			return;
		}

		draw.replace(ForgeLayeredDraw.HOTBAR_AND_DECOS, ForgeLayeredDraw.HEALTH_BAR, (graphics, delta) -> {
			if (OverlayHandler.isSplit()) {
				OverlayHandler.extractHealthBar(graphics);
				return;
			}

			layer.extract(graphics, delta);
		});
	}

	private static boolean isSplit() {
		return OverlayBridge.inst().isCancelled(ElementType.ARMOR) || OverlayBridge.inst().isCancelled(ElementType.HEALTH) || OverlayBridge.inst().isCancelled(ElementType.FOOD) || OverlayBridge.inst().isCancelled(ElementType.AIR);
	}

	private static void extractHealthBar(final GuiGraphicsExtractor graphics) {
		final Hud hud = Minecraft.getInstance().gui.hud;
		final Player player = hud.getCameraPlayer();
		if (player == null) {
			return;
		}

		final int health = Mth.ceil(player.getHealth());
		final int absorption = Mth.ceil(player.getAbsorptionAmount());
		final float maximum = Math.max((float) player.getAttributeValue(Attributes.MAX_HEALTH), health);
		final int rows = Mth.ceil((maximum + absorption) / 2F / 10F);
		final int rowHeight = Math.max(10 - (rows - 2), 3);
		final int left = graphics.guiWidth() / 2 - 91;
		final int right = graphics.guiWidth() / 2 + 91;
		final int line = graphics.guiHeight() - 39;
		final int offset = player.hasEffect(MobEffects.REGENERATION) ? hud.getGuiTicks() % Mth.ceil(maximum + 5F) : -1;
		final int mount = hud.getVehicleMaxHearts(hud.getPlayerVehicleWithHealth());
		int air = line - 10;
		if (!OverlayBridge.inst().isCancelled(ElementType.ARMOR)) {
			Hud.extractArmor(graphics, player, line, rows, rowHeight, left);
		}

		if (!OverlayBridge.inst().isCancelled(ElementType.HEALTH)) {
			hud.extractHearts(graphics, player, left, line, rowHeight, offset, maximum, health, health, absorption, false);
		}

		if (mount == 0) {
			if (!OverlayBridge.inst().isCancelled(ElementType.FOOD)) {
				hud.extractFood(graphics, player, line, right);
			}

			air -= 10;
		}

		if (!OverlayBridge.inst().isCancelled(ElementType.AIR)) {
			hud.extractAirBubbles(graphics, player, mount, air, right);
		}
	}

	private static Identifier getStack(final Identifier layer) {
		for (final Map.Entry<Identifier, List<Identifier>> entry : OverlayHandler.STACK_MAP.entrySet()) {
			if (entry.getValue().contains(layer)) {
				return entry.getKey();
			}
		}

		return ForgeLayeredDraw.VANILLA_ROOT;
	}

	private static Identifier getIdentifier(final ElementType type, final boolean post) {
		return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "overlay/" + type.name().toLowerCase(Locale.ROOT) + (post ? "_post" : "_pre"));
	}

}