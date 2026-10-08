package dev.joid.impl.joidmc.demo.ui.guiscale;

import dev.joid.demo.ui.UIDemo;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.text.builder.Text;
import dev.joid.lib.font.dto.TextInfo;
import dev.joid.lib.ui.node.impl.design.shape.RectNode;
import dev.joid.lib.ui.node.impl.design.text.TextNode;
import dev.joid.lib.utils.align.Align;
import dev.joid.impl.joidmc.lib.font.impl.minecraft.MinecraftFont;
import dev.joid.impl.joidmc.lib.ui.core.data.UIMCData;

import net.minecraft.client.Minecraft;

@UIMCData(guiScale = false)
public final class UIDemoGuiScaleFixed extends UIDemo {

	@Override
	public void init() {
		RectNode
		.create(660, 390, 600, 300)
		.color(new Color(18, 18, 24, 220))
		.borderColor(Color.CYAN)
		.borderStroke(3D)
		.body(rect -> {
			TextNode
			.create(rect.dw(2D), 40)
			.text(Text.create("guiScale false", TextInfo.create(MinecraftFont.MINECRAFT, 30F, Color.CYAN), Align.CENTER))
			.anchorX(Align.CENTER)
			.attach(rect);
			TextNode
			.create(rect.dw(2D), 130)
			.text(Text.create(() -> "Minecraft " + Minecraft.getInstance().getWindow().getGuiScale(), TextInfo.create(MinecraftFont.MINECRAFT, 26F, Color.WHITE), Align.CENTER))
			.anchorX(Align.CENTER)
			.attach(rect);
			TextNode
			.create(rect.dw(2D), 190)
			.text(Text.create(() -> "Interface " + Math.round(this.getBridge().getInterfaceScale(this) * 100D) / 100D, TextInfo.create(MinecraftFont.MINECRAFT, 26F, Color.WHITE), Align.CENTER))
			.anchorX(Align.CENTER)
			.attach(rect);
		})
		.attach(this);
	}

}