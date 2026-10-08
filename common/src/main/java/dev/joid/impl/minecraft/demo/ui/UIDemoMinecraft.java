package dev.joid.impl.minecraft.demo.ui;

import dev.joid.demo.DemoFont;
import dev.joid.demo.ui.UIDemo;
import dev.joid.impl.minecraft.lib.font.dto.ComponentText;
import dev.joid.impl.minecraft.lib.font.impl.minecraft.MinecraftFont;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.text.builder.Text;
import dev.joid.lib.draw.text.utils.TextMode;
import dev.joid.lib.font.dto.TextInfo;
import dev.joid.lib.ui.node.impl.design.shape.RectNode;
import dev.joid.lib.ui.node.impl.design.text.TextNode;
import dev.joid.lib.utils.align.Align;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class UIDemoMinecraft extends UIDemo {

	private static final Color INK         = new Color(153, 153, 153);
	private static final Color PANEL       = new Color(48, 48, 48);
	private static final Color PLACEHOLDER = new Color(221, 221, 221);

	@Override
	public void init() {
		final TextInfo caption = TextInfo.create(DemoFont.MONTSERRAT, 24, UIDemoMinecraft.INK);
		final TextInfo info = TextInfo.create(MinecraftFont.DEFAULT, MinecraftFont.SIZE * 3, Color.WHITE).shadow(new Color(63, 63, 63)).shadow(3F, 3F);
		final Component component = Component.literal("Hex ").withColor(0x12ABCD).append(Component.translatable("menu.game").withStyle(ChatFormatting.GOLD, ChatFormatting.UNDERLINE)).append(Component.literal(" plain"));

		RectNode
		.create(40, 40, 440, 240)
		.color(UIDemoMinecraft.PLACEHOLDER)
		.body(rect -> {
			RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
			TextNode.create(30, 30, 380, 0).text(Text.create("\u00A70Black \u00A71Blue \u00A72Green \u00A73Aqua \u00A74Red \u00A75Purple \u00A76Gold \u00A77Gray \u00A78Gray \u00A79Blue \u00A7aGreen \u00A7bAqua \u00A7cRed \u00A7dPink \u00A7eYellow \u00A7fWhite", info)).mode(TextMode.SPLIT).attach(rect);
			TextNode.create(220, 255).text(Text.create("Colors", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
		})
		.attach(this);

		RectNode
		.create(520, 40, 440, 240)
		.color(UIDemoMinecraft.PLACEHOLDER)
		.body(rect -> {
			RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
			TextNode.create(30, 30, 380, 0).text(Text.create("\u00A7lBold\u00A7r \u00A7oItalic\u00A7r \u00A7l\u00A7oBoth\u00A7r \u00A7nUnderline\u00A7r \u00A7mStrike\u00A7r \u00A7kSecret\u00A7r \u00A7e\u00A7l\u00A7nAll\u00A7r Reset", info)).mode(TextMode.SPLIT).attach(rect);
			TextNode.create(220, 255).text(Text.create("Formats", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
		})
		.attach(this);

		RectNode
		.create(1000, 40, 440, 240)
		.color(UIDemoMinecraft.PLACEHOLDER)
		.body(rect -> {
			RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
			TextNode.create(30, 30).text(Text.create("Default font", info)).attach(rect);
			TextNode.create(30, 75).text(Text.create("Enchanting table", info.copy().font(MinecraftFont.ALT))).attach(rect);
			TextNode.create(30, 120).text(Text.create("Illager runes", info.copy().font(MinecraftFont.ILLAGER))).attach(rect);
			TextNode.create(30, 165).text(Text.create("Uniform font", info.copy().font(MinecraftFont.UNIFORM))).attach(rect);
			TextNode.create(220, 255).text(Text.create("Fonts", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
		})
		.attach(this);

		RectNode
		.create(1480, 40, 400, 240)
		.color(UIDemoMinecraft.PLACEHOLDER)
		.body(rect -> {
			RectNode.create(20, 20, 360, 200).color(UIDemoMinecraft.PANEL).attach(rect);
			TextNode.create(30, 30, 340, 0).text(Text.create(ComponentText.of(component), info)).mode(TextMode.SPLIT).attach(rect);
			TextNode.create(200, 255).text(Text.create("Component", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
		})
		.attach(this);

		RectNode
		.create(40, 360, 440, 240)
		.color(UIDemoMinecraft.PLACEHOLDER)
		.body(rect -> {
			RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
			TextNode.create(30, 30, 380, 0).text(Text.create("\u00C9t\u00E9 \u00FCber \u00F1 \u2605 \u2665 \u2713 \u65E5\u672C\u8A9E \u0391\u03B2\u03B3", info)).mode(TextMode.SPLIT).attach(rect);
			TextNode.create(220, 255).text(Text.create("Unicode", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
		})
		.attach(this);

		RectNode
		.create(520, 360, 440, 240)
		.color(UIDemoMinecraft.PLACEHOLDER)
		.body(rect -> {
			RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
			TextNode.create(30, 30, 380, 0).text(Text.create("A long \u00A7asentence\u00A7r wraps on the width of its node and stays centered.", info, Align.CENTER)).mode(TextMode.SPLIT).attach(rect);
			TextNode.create(220, 255).text(Text.create("Wrapped", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
		})
		.attach(this);

		RectNode
		.create(1000, 360, 440, 240)
		.color(UIDemoMinecraft.PLACEHOLDER)
		.body(rect -> {
			RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
			TextNode.create(30, 30).text(Text.create("Scale 1", info.copy().fontSize(MinecraftFont.SIZE).shadow(1F, 1F))).attach(rect);
			TextNode.create(30, 50).text(Text.create("Scale 2", info.copy().fontSize(MinecraftFont.SIZE * 2).shadow(2F, 2F))).attach(rect);
			TextNode.create(30, 80).text(Text.create("Scale 4", info.copy().fontSize(MinecraftFont.SIZE * 4).shadow(4F, 4F))).attach(rect);
			TextNode.create(30, 135).text(Text.create("Scale 2.5", info.copy().fontSize(MinecraftFont.SIZE * 2.5F).shadow(2.5F, 2.5F))).attach(rect);
			TextNode.create(220, 255).text(Text.create("Sizes", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
		})
		.attach(this);

		RectNode
		.create(1480, 360, 400, 240)
		.color(UIDemoMinecraft.PLACEHOLDER)
		.body(rect -> {
			TextNode.create(20, 30, 360, 0).text(Text.create("\u00A7lDark\u00A7r \u00A7otext\u00A7r \u00A7nwithout\u00A7r shadow", info.copy().color(Color.BLACK).shadow(null))).mode(TextMode.SPLIT).attach(rect);
			TextNode.create(200, 255).text(Text.create("No shadow", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
		})
		.attach(this);
	}

}