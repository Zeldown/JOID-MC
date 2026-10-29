package dev.joid.backend.minecraft.demo.ui;

import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.demo.DemoFont;
import dev.joid.demo.ui.UIDemo;
import dev.joid.lib.JOID;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.text.Text;
import dev.joid.lib.draw.text.TextMode;
import dev.joid.lib.font.TextInfo;
import dev.joid.lib.input.key.resolver.KeyResolver;
import dev.joid.lib.ui.node.impl.layout.ContainerNode;
import dev.joid.lib.ui.node.impl.shape.RectNode;
import dev.joid.lib.ui.node.impl.text.TextNode;
import dev.joid.lib.ui.node.property.OverflowProperty;
import dev.joid.lib.ui.shortcut.Shortcut;
import dev.joid.lib.utils.align.Align;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

@UIDataMinecraft(pause = false)
public class UIDemoMinecraft extends UIDemo {

	private static final Color INK         = new Color(153, 153, 153).lock();
	private static final Color PANEL       = new Color(48, 48, 48).lock();
	private static final Color PLACEHOLDER = new Color(221, 221, 221).lock();

	@Override
	public void init() {
		final TextInfo caption = TextInfo.create(DemoFont.MONTSERRAT, 24).color(UIDemoMinecraft.INK);
		final TextInfo info = TextInfo.create(DemoFont.MONTSERRAT, 24).color(Color.WHITE).shadowTint(0.25F).shadow(3F, 3F);
		final Minecraft minecraft = Minecraft.getMinecraft();

		ContainerNode
		.create(0, 0, 1920, 1080)
		.overflow(OverflowProperty.SCROLL)
		.body(container -> {
			RectNode
			.create(40, 40, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				TextNode.create(30, 30, 380, 0).text(Text.create("§0Black §1Blue §2Green §3Aqua §4Red §5Purple §6Gold §7Gray §8Gray §9Blue §aGreen §bAqua §cRed §dPink §eYellow §fWhite", info)).mode(TextMode.SPLIT).attach(rect);
				TextNode.create(220, 255).text(Text.create("Colors", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(520, 40, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				TextNode.create(30, 30, 380, 0).text(Text.create("§lBold§r §oItalic§r §l§oBoth§r §nUnderline§r §mStrike§r §kSecret§r §e§l§nAll§r Reset", info)).mode(TextMode.SPLIT).attach(rect);
				TextNode.create(220, 255).text(Text.create("Formats", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(1000, 40, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				TextNode.create(30, 30, 380, 0).text(Text.create("A long §asentence§r wraps on the width of its node and stays centered.", info).alignX(Align.CENTER)).mode(TextMode.SPLIT).attach(rect);
				TextNode.create(220, 255).text(Text.create("Wrapped", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(1480, 40, 400, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 360, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				RectNode
				.create(150, 70, 100, 100)
				.color(UIDemoMinecraft.INK)
				.hoveredColor(UIDemoMinecraft.PLACEHOLDER)
				.body(button -> {
					TextNode.create(50, 50).text(Text.create("Play", info).alignX(Align.CENTER)).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(button);
				})
				.onClick(event -> minecraft.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("note.pling"), 1F)))
				.attach(rect);
				TextNode.create(200, 255).text(Text.create("Vanilla sound", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(40, 360, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				TextNode.create(30, 30).text(Text.create(() -> "GUI scale " + new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight).getScaleFactor(), info)).attach(rect);
				TextNode.create(30, 60).text(Text.create(() -> String.format("Interface scale %.2f", super.getView().getInterfaceScale()), info)).attach(rect);
				TextNode.create(30, 90).text(Text.create(() -> "Active " + super.getScale().active() + ", limited " + super.getScale().limited(), info)).attach(rect);
				RectNode
				.create(30, 140, 180, 60)
				.color(UIDemoMinecraft.INK)
				.hoveredColor(UIDemoMinecraft.PLACEHOLDER)
				.body(toggle -> {
					TextNode.create(90, 30).text(Text.create("Active", info).alignX(Align.CENTER)).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(toggle);
				})
				.onClick(event -> super.getScale().active(!super.getScale().active()))
				.attach(rect);
				RectNode
				.create(230, 140, 180, 60)
				.color(UIDemoMinecraft.INK)
				.hoveredColor(UIDemoMinecraft.PLACEHOLDER)
				.body(toggle -> {
					TextNode.create(90, 30).text(Text.create("Limit 0.75", info).alignX(Align.CENTER)).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(toggle);
				})
				.onClick(event -> super.getScale().limited(!super.getScale().limited()).limit(0.75D))
				.attach(rect);
				TextNode.create(220, 255).text(Text.create("Interface scale", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(520, 360, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				TextNode.create(30, 30, 380, 0).text(Text.create(() -> I18n.format("joid.demo.minecraft.keybind", GameSettings.getKeyDisplayString(minecraft.gameSettings.keyBindInventory.getKeyCode())), info)).mode(TextMode.SPLIT).attach(rect);
				TextNode.create(30, 150).text(Text.create(() -> "JOID key " + KeyResolver.resolve(minecraft.gameSettings.keyBindInventory), info)).attach(rect);
				TextNode.create(220, 255).text(Text.create("Key bind", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(1000, 360, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				TextNode.create(30, 30).text(Text.create(() -> "Pause screen " + (minecraft.currentScreen != null && minecraft.currentScreen.doesGuiPauseGame()), info)).attach(rect);
				TextNode.create(30, 80).text(Text.create(() -> "Game paused " + minecraft.isGamePaused(), info)).attach(rect);
				TextNode.create(220, 255).text(Text.create("Screen data", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);
		})
		.attach(this);

		Shortcut.create(minecraft.gameSettings.keyBindInventory).onTrigger(event -> JOID.close(this)).attach(this);
	}

}