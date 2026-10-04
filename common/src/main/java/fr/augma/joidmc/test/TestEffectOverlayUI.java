package fr.augma.joidmc.test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import be.zeldown.joid.lib.color.Color;
import be.zeldown.joid.lib.draw.text.builder.Text;
import be.zeldown.joid.lib.font.dto.text.TextInfo;
import be.zeldown.joid.lib.ui.core.UI;
import be.zeldown.joid.lib.ui.core.data.UIData;
import be.zeldown.joid.lib.ui.node.impl.design.shape.RectNode;
import be.zeldown.joid.lib.ui.node.impl.design.text.TextNode;
import be.zeldown.joid.lib.ui.node.impl.structure.container.ContainerNode;
import be.zeldown.joid.lib.ui.node.property.watch.WatchProperty;
import be.zeldown.joid.lib.utils.align.Align;
import be.zeldown.joid.lib.utils.signal.impl.iterable.ListSignal;
import fr.augma.joidmc.font.MinecraftFont;
import fr.augma.joidmc.screen.data.UIMCData;
import fr.augma.joidmc.screen.data.overlay.UIMCOverlay;
import fr.augma.joidmc.screen.data.overlay.render.ElementType;
import fr.augma.joidmc.screen.data.overlay.render.UIMCOverlayRender;
import fr.augma.joidmc.screen.node.ItemNode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

@UIMCData
@UIData(background = false, anchorX = Align.END, anchorY = Align.START)
@UIMCOverlay(render = @UIMCOverlayRender(type = ElementType.EFFECTS, cancel = true))
public final class TestEffectOverlayUI extends UI {

	private static final double WIDTH  = 300D;
	private static final double HEIGHT = 96D;
	private static final double MARGIN = 10D;

	private final ListSignal<Effect> effects = new ListSignal<>(new ArrayList<>());

	@Override
	public void init() {
		ContainerNode
		.create(1900D - TestEffectOverlayUI.WIDTH, 20, TestEffectOverlayUI.WIDTH, 1040)
		.watch(this.effects, WatchProperty.CLEAR_CHILDREN, WatchProperty.BODY)
		.body(column -> {
			final List<Effect> effectList = this.effects.getOrDefault();
			for (int index = 0; index < effectList.size(); index++) {
				final Effect effect = effectList.get(index);
				final ItemStack potion = new ItemStack(Items.POTION);
				potion.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.of(effect.holder().value().getColor()), List.of(), Optional.empty()));
				RectNode
				.create(0, index * (TestEffectOverlayUI.HEIGHT + TestEffectOverlayUI.MARGIN), TestEffectOverlayUI.WIDTH, TestEffectOverlayUI.HEIGHT)
				.color(new Color(18, 18, 24, 200))
				.border(Color.CYAN, 3D)
				.body(frame -> {
					ItemNode.create(12, 12, 72).stack(potion).attach(frame);
					TextNode
					.create(96, 18)
					.text(Text.create(effect.holder().value().getDisplayName().getString() + (effect.amplifier() > 0 ? " " + (effect.amplifier() + 1) : ""), TextInfo.create(MinecraftFont.MINECRAFT, 28F, Color.WHITE).shadow(), Align.START))
					.attach(frame);
					TextNode
					.create(96, 54)
					.text(Text.create(() -> {
						final LocalPlayer player = Minecraft.getInstance().player;
						final MobEffectInstance instance = player == null ? null : player.getEffect(effect.holder());
						return instance == null ? "" : MobEffectUtil.formatDuration(instance, 1F, player.level().tickRateManager().tickrate()).getString();
					}, TextInfo.create(MinecraftFont.MINECRAFT, 24F, new Color(170, 170, 170)).shadow(), Align.START))
					.attach(frame);
				})
				.attach(column);
			}
		})
		.attach(this);
	}

	@Override
	public void update() {
		final LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}

		final List<Effect> effectList = new ArrayList<>();
		for (final MobEffectInstance instance : player.getActiveEffects()) {
			if (instance.showIcon()) {
				effectList.add(new Effect(instance.getEffect(), instance.getAmplifier()));
			}
		}

		effectList.sort(Comparator.comparing(effect -> effect.holder().getRegisteredName()));
		this.effects.set(effectList);
	}

	private record Effect(Holder<MobEffect> holder, int amplifier) {}

}