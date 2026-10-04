package fr.augma.joidmc.test;

import be.zeldown.joid.lib.color.Color;
import be.zeldown.joid.lib.draw.text.builder.Text;
import be.zeldown.joid.lib.font.dto.text.TextInfo;
import be.zeldown.joid.lib.ui.core.UI;
import be.zeldown.joid.lib.ui.core.data.UIData;
import be.zeldown.joid.lib.ui.node.impl.design.progress.ProgressNode;
import be.zeldown.joid.lib.ui.node.impl.design.shape.RectNode;
import be.zeldown.joid.lib.ui.node.impl.design.text.TextNode;
import be.zeldown.joid.lib.ui.node.property.watch.WatchProperty;
import be.zeldown.joid.lib.utils.align.Align;
import be.zeldown.joid.lib.utils.signal.Signal;
import be.zeldown.joid.lib.utils.signal.impl.primitive.IntegerSignal;
import fr.augma.joidmc.font.MinecraftFont;
import fr.augma.joidmc.screen.data.UIMCData;
import fr.augma.joidmc.screen.data.overlay.UIMCOverlay;
import fr.augma.joidmc.screen.data.overlay.render.ElementType;
import fr.augma.joidmc.screen.data.overlay.render.UIMCOverlayRender;
import fr.augma.joidmc.screen.node.ItemNode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

@UIMCData
@UIData(background = false, anchorY = Align.END)
@UIMCOverlay(render = @UIMCOverlayRender(type = ElementType.HOTBAR, cancel = true, hide = {ElementType.HEALTH, ElementType.ARMOR, ElementType.FOOD, ElementType.AIR, ElementType.EXPERIENCE, ElementType.JUMPBAR, ElementType.HEALTHMOUNT}))
public final class TestHotbarOverlayUI extends UI {

	private static final double SLOT = 80D;

	private final IntegerSignal   selected   = new IntegerSignal();
	private final Signal<Stat>    health     = new Signal<>();
	private final Signal<Stat>    armor      = new Signal<>();
	private final Signal<Stat>    food       = new Signal<>();
	private final Signal<Stat>    mount      = new Signal<>();
	private final Signal<Stat>    air        = new Signal<>();
	private final Signal<Float>   experience = new Signal<>();
	private final Signal<Float>   jump       = new Signal<>();
	private final Signal<Integer> level      = new Signal<>();

	@Override
	public void init() {
		RectNode
		.create(960D - Inventory.getSelectionSize() * TestHotbarOverlayUI.SLOT / 2D - 6D, 986, Inventory.getSelectionSize() * TestHotbarOverlayUI.SLOT + 12D, 92)
		.color(new Color(18, 18, 24, 200))
		.border(Color.CYAN, 3D)
		.body(bar -> {
			RectNode
			.create(6, 6, TestHotbarOverlayUI.SLOT, TestHotbarOverlayUI.SLOT)
			.color(new Color(255, 255, 255, 40))
			.border(Color.WHITE, 4D)
			.watch(this.selected, WatchProperty.NONE)
			.onWatch((final RectNode highlight, final Signal<?> signal, final WatchProperty... properties) -> highlight.x(6D + this.selected.getOrDefault() * TestHotbarOverlayUI.SLOT))
			.attach(bar);
			for (int index = 0; index < Inventory.getSelectionSize(); index++) {
				final int slot = index;
				ItemNode
				.create(14D + slot * TestHotbarOverlayUI.SLOT, 14, 64)
				.stack(() -> Minecraft.getInstance().player == null ? ItemStack.EMPTY : Minecraft.getInstance().player.getInventory().getItem(slot))
				.attach(bar);
			}
		})
		.attach(this);

		ProgressNode
		.create(596, 952, 728, 24)
		.background(new Color(18, 18, 24, 200))
		.foreground(new Color(128, 255, 32))
		.visible(this.experience)
		.watch(this.experience, WatchProperty.NONE)
		.onWatch((final ProgressNode bar, final Signal<?> signal, final WatchProperty... properties) -> bar.progress(this.experience.isPresent() ? this.experience.getOrDefault() : 0F))
		.body(bar -> {
			TextNode
			.create(bar.dw(2D), 2)
			.text(Text.create("", TextInfo.create(MinecraftFont.MINECRAFT, 20F, Color.WHITE).shadow(), Align.CENTER))
			.watch(this.level, WatchProperty.NONE)
			.onWatch((final TextNode text, final Signal<?> signal, final WatchProperty... properties) -> text.getText().text(this.level.isPresent() ? String.valueOf(this.level.getOrDefault()) : ""))
			.anchorX(Align.CENTER)
			.attach(bar);
		})
		.attach(this);

		ProgressNode
		.create(596, 952, 728, 24)
		.background(new Color(18, 18, 24, 200))
		.foreground(new Color(240, 170, 40))
		.visible(this.jump)
		.watch(this.jump, WatchProperty.NONE)
		.onWatch((final ProgressNode bar, final Signal<?> signal, final WatchProperty... properties) -> bar.progress(this.jump.isPresent() ? this.jump.getOrDefault() : 0F))
		.attach(this);

		ProgressNode
		.create(596, 916, 320, 36)
		.background(new Color(18, 18, 24, 200))
		.foreground(new Color(210, 40, 40))
		.visible(this.health)
		.watch(this.health, WatchProperty.NONE)
		.onWatch((final ProgressNode bar, final Signal<?> signal, final WatchProperty... properties) -> {
			final Stat stat = this.health.getOrDefault();
			if (stat != null) {
				bar.progress(stat.value() / stat.maximum());
				bar.getChild(0, TextNode.class).getText().text((int) Math.ceil(stat.value()) + " / " + (int) stat.maximum());
			}
		})
		.body(bar -> {
			TextNode
			.create(bar.dw(2D), 6)
			.text(Text.create("", TextInfo.create(MinecraftFont.MINECRAFT, 24F, Color.WHITE).shadow(), Align.CENTER))
			.anchorX(Align.CENTER)
			.attach(bar);
		})
		.attach(this);

		ProgressNode
		.create(596, 872, 320, 36)
		.background(new Color(18, 18, 24, 200))
		.foreground(new Color(160, 170, 185))
		.visible(this.armor)
		.watch(this.armor, WatchProperty.NONE)
		.onWatch((final ProgressNode bar, final Signal<?> signal, final WatchProperty... properties) -> {
			final Stat stat = this.armor.getOrDefault();
			if (stat != null) {
				bar.progress(stat.value() / stat.maximum());
				bar.getChild(0, TextNode.class).getText().text(String.valueOf((int) stat.value()));
			}
		})
		.body(bar -> {
			TextNode
			.create(bar.dw(2D), 6)
			.text(Text.create("", TextInfo.create(MinecraftFont.MINECRAFT, 24F, Color.WHITE).shadow(), Align.CENTER))
			.anchorX(Align.CENTER)
			.attach(bar);
		})
		.attach(this);
		ProgressNode
		.create(1004, 916, 320, 36)
		.background(new Color(18, 18, 24, 200))
		.foreground(new Color(205, 130, 45))
		.visible(this.food)
		.watch(this.food, WatchProperty.NONE)
		.onWatch((final ProgressNode bar, final Signal<?> signal, final WatchProperty... properties) -> {
			final Stat stat = this.food.getOrDefault();
			if (stat != null) {
				bar.progress(stat.value() / stat.maximum());
				bar.getChild(0, TextNode.class).getText().text((int) stat.value() + " / " + (int) stat.maximum());
			}
		})
		.body(bar -> {
			TextNode
			.create(bar.dw(2D), 6)
			.text(Text.create("", TextInfo.create(MinecraftFont.MINECRAFT, 24F, Color.WHITE).shadow(), Align.CENTER))
			.anchorX(Align.CENTER)
			.attach(bar);
		})
		.attach(this);
		ProgressNode
		.create(1004, 916, 320, 36)
		.background(new Color(18, 18, 24, 200))
		.foreground(new Color(230, 80, 110))
		.visible(this.mount)
		.watch(this.mount, WatchProperty.NONE)
		.onWatch((final ProgressNode bar, final Signal<?> signal, final WatchProperty... properties) -> {
			final Stat stat = this.mount.getOrDefault();
			if (stat != null) {
				bar.progress(stat.value() / stat.maximum());
				bar.getChild(0, TextNode.class).getText().text((int) Math.ceil(stat.value()) + " / " + (int) stat.maximum());
			}
		})
		.body(bar -> {
			TextNode
			.create(bar.dw(2D), 6)
			.text(Text.create("", TextInfo.create(MinecraftFont.MINECRAFT, 24F, Color.WHITE).shadow(), Align.CENTER))
			.anchorX(Align.CENTER)
			.attach(bar);
		})
		.attach(this);
		ProgressNode
		.create(1004, 872, 320, 36)
		.background(new Color(18, 18, 24, 200))
		.foreground(new Color(70, 150, 250))
		.visible(this.air)
		.watch(this.air, WatchProperty.NONE)
		.onWatch((final ProgressNode bar, final Signal<?> signal, final WatchProperty... properties) -> {
			final Stat stat = this.air.getOrDefault();
			if (stat != null) {
				bar.progress(stat.value() / stat.maximum());
				bar.getChild(0, TextNode.class).getText().text((int) stat.value() / 20 + "s");
			}
		})
		.body(bar -> {
			TextNode
			.create(bar.dw(2D), 6)
			.text(Text.create("", TextInfo.create(MinecraftFont.MINECRAFT, 24F, Color.WHITE).shadow(), Align.CENTER))
			.anchorX(Align.CENTER)
			.attach(bar);
		})
		.attach(this);
	}

	@Override
	public void update() {
		final Minecraft minecraft = Minecraft.getInstance();
		final LocalPlayer player = minecraft.player;
		if (player == null) {
			return;
		}

		final boolean survival = minecraft.gameMode != null && minecraft.gameMode.canHurtPlayer();
		final boolean jumping = player.jumpableVehicle() != null;
		final LivingEntity vehicle = player.getVehicle() instanceof final LivingEntity living ? living : null;
		this.selected.set(player.getInventory().getSelectedSlot());
		this.health.set(survival ? new Stat(player.getHealth(), player.getMaxHealth()) : null);
		this.armor.set(survival && player.getArmorValue() > 0 ? new Stat(player.getArmorValue(), 20F) : null);
		this.food.set(survival && vehicle == null ? new Stat(player.getFoodData().getFoodLevel(), 20F) : null);
		this.mount.set(survival && vehicle != null ? new Stat(vehicle.getHealth(), vehicle.getMaxHealth()) : null);
		this.air.set(survival && (player.isEyeInFluid(FluidTags.WATER) || player.getAirSupply() < player.getMaxAirSupply()) ? new Stat(Math.max(0, player.getAirSupply()), player.getMaxAirSupply()) : null);
		this.experience.set(survival && !jumping ? player.experienceProgress : null);
		this.jump.set(jumping ? player.getJumpRidingScale() : null);
		this.level.set(survival && !jumping && player.experienceLevel > 0 ? player.experienceLevel : null);
	}

	private record Stat(float value, float maximum) {}

}