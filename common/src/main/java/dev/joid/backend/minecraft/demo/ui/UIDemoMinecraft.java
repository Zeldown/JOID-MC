package dev.joid.backend.minecraft.demo.ui;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import dev.joid.backend.minecraft.lib.draw.block.DrawBlock;
import dev.joid.backend.minecraft.lib.draw.entity.DrawEntity;
import dev.joid.backend.minecraft.lib.draw.item.DrawItem;
import dev.joid.backend.minecraft.lib.font.impl.minecraft.MinecraftFont;
import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.block.BlockNode;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.entity.EntityNode;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.item.ItemNode;
import dev.joid.demo.DemoFont;
import dev.joid.demo.ui.UIDemo;
import dev.joid.lib.JOID;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.text.Text;
import dev.joid.lib.draw.text.TextMode;
import dev.joid.lib.font.TextInfo;
import dev.joid.lib.input.key.resolver.KeyResolver;
import dev.joid.lib.render.transform.Rotation;
import dev.joid.lib.render.transform.Scale;
import dev.joid.lib.render.transform.Transformation;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.effect.NodeEffect.NodeEffectScope;
import dev.joid.lib.ui.node.effect.impl.CircleNodeEffect;
import dev.joid.lib.ui.node.effect.impl.TransformNodeEffect;
import dev.joid.lib.ui.node.impl.layout.ContainerNode;
import dev.joid.lib.ui.node.impl.resource.ResourceNode;
import dev.joid.lib.ui.node.impl.shape.RectNode;
import dev.joid.lib.ui.node.impl.text.TextNode;
import dev.joid.lib.ui.node.property.OverflowProperty;
import dev.joid.lib.ui.shortcut.Shortcut;
import dev.joid.lib.utils.align.Align;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

@UIDataMinecraft(pause = false)
public class UIDemoMinecraft extends UIDemo {

	private static final Color INK         = new Color(153, 153, 153).lock();
	private static final Color PANEL       = new Color(48, 48, 48).lock();
	private static final Color PLACEHOLDER = new Color(221, 221, 221).lock();

	private static final DynamicTexture CHECKER = new DynamicTexture(2, 2);

	static {
		UIDemoMinecraft.CHECKER.getTextureData()[0] = UIDemoMinecraft.PLACEHOLDER.getRGB();
		UIDemoMinecraft.CHECKER.getTextureData()[1] = UIDemoMinecraft.INK.getRGB();
		UIDemoMinecraft.CHECKER.getTextureData()[2] = UIDemoMinecraft.INK.getRGB();
		UIDemoMinecraft.CHECKER.getTextureData()[3] = UIDemoMinecraft.PLACEHOLDER.getRGB();
		UIDemoMinecraft.CHECKER.updateDynamicTexture();
	}

	@Override
	public void init() {
		final TextInfo caption = TextInfo.create(DemoFont.MONTSERRAT, 24).color(UIDemoMinecraft.INK);
		final TextInfo info = TextInfo.create(MinecraftFont.DEFAULT, MinecraftFont.SIZE * 3).color(Color.WHITE).shadowTint(0.25F).shadow(3F, 3F);
		final IChatComponent component = new ChatComponentText("").appendSibling(new ChatComponentText("Gold ").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD))).appendSibling(new ChatComponentTranslation("menu.game").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.AQUA).setUnderlined(true))).appendText(" plain");
		final ItemStack damaged = new ItemStack(Items.diamond_pickaxe);
		final ItemStack star = new ItemStack(Items.nether_star);
		final ResourceLocation skin = new ResourceLocation("joid", "demo/textures/skin.png");
		final Minecraft minecraft = Minecraft.getMinecraft();

		damaged.setItemDamage(1100);

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
				TextNode.create(30, 30).text(Text.create("Default font", info)).attach(rect);
				TextNode.create(30, 75).text(Text.create("Enchanting table", info.copy().font(MinecraftFont.ALT))).attach(rect);
				TextNode.create(220, 255).text(Text.create("Fonts", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(1480, 40, 400, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 360, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				TextNode.create(30, 30, 340, 0).text(Text.create(component, info)).mode(TextMode.SPLIT).attach(rect);
				TextNode.create(200, 255).text(Text.create("Chat text", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(40, 360, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				TextNode.create(30, 30, 380, 0).text(Text.create("Été über ñ ★ ♥ ✓ 日本語 Αβγ", info)).mode(TextMode.SPLIT).attach(rect);
				TextNode.create(220, 255).text(Text.create("Unicode", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(520, 360, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				TextNode.create(30, 30, 380, 0).text(Text.create("A long §asentence§r wraps on the width of its node and stays centered.", info).alignX(Align.CENTER)).mode(TextMode.SPLIT).attach(rect);
				TextNode.create(220, 255).text(Text.create("Wrapped", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(1000, 360, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				TextNode.create(30, 30).text(Text.create("Scale 1", info.copy().fontSize(MinecraftFont.SIZE).shadow(1F, 1F))).attach(rect);
				TextNode.create(30, 50).text(Text.create("Scale 2", info.copy().fontSize(MinecraftFont.SIZE * 2).shadow(2F, 2F))).attach(rect);
				TextNode.create(30, 80).text(Text.create("Scale 4", info.copy().fontSize(MinecraftFont.SIZE * 4).shadow(4F, 4F))).attach(rect);
				TextNode.create(30, 135).text(Text.create("Scale 2.5", info.copy().fontSize(MinecraftFont.SIZE * 2.5F).shadow(2.5F, 2.5F))).attach(rect);
				TextNode.create(220, 255).text(Text.create("Sizes", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(1480, 360, 400, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				TextNode.create(20, 30, 360, 0).text(Text.create("§lDark§r §otext§r §nwithout§r shadow", info.copy().color(Color.BLACK).shadowTint(null))).mode(TextMode.SPLIT).attach(rect);
				TextNode.create(200, 255).text(Text.create("No shadow", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(40, 680, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				ResourceNode.create(40, 40, 360, 0).resource(Resource.of("minecraft:textures/gui/title/minecraft.png")).attach(rect);
				ResourceNode.create(40, 140, 64, 64).resource(Resource.of(new ResourceLocation("textures/blocks/diamond_block.png"))).attach(rect);
				ResourceNode.create(140, 140, 64, 64).resource(Resource.of(minecraft.getTextureManager().getTexture(TextureMap.locationBlocksTexture))).attach(rect);
				ResourceNode.create(240, 140, 64, 64).resource(Resource.of(UIDemoMinecraft.CHECKER)).attach(rect);
				TextNode.create(220, 255).text(Text.create("Textures", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(520, 680, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				ResourceNode.create(40, 40, 64, 64).resource(Resource.of(Items.diamond.getIconFromDamage(0))).attach(rect);
				ResourceNode.create(40, 128, 64, 64).resource(Resource.of(Blocks.stone.getIcon(0, 0))).attach(rect);
				ResourceNode.create(140, 40, 64, 64).resource(Resource.of(Blocks.crafting_table.getIcon(1, 0))).attach(rect);
				ResourceNode.create(140, 128, 64, 64).resource(Resource.of(ItemArmor.func_94602_b(0))).attach(rect);
				TextNode.create(220, 255).text(Text.create("Sprites", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(1000, 680, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				ResourceNode.create(40, 56, 128, 128).resource(Resource.of(Blocks.fire.getFireIcon(0))).attach(rect);
				ResourceNode.create(240, 56, 128, 128).resource(Resource.of(Blocks.water.getIcon(0, 0))).attach(rect);
				TextNode.create(220, 255).text(Text.create("Animated sprites", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(1480, 680, 400, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 360, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				ResourceNode.create(40, 56, 128, 128).resource(Resource.of("minecraft:textures/blocks/water_still.png")).attach(rect);
				ResourceNode.create(232, 56, 128, 128).resource(Resource.of("joid:demo/textures/pulse.png")).attach(rect);
				TextNode.create(200, 255).text(Text.create(".mcmeta", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(40, 1000, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				ItemNode.create(40, 80, 80, 80).stack(new ItemStack(Items.apple)).attach(rect);
				ItemNode.create(130, 80, 80, 80).stack(new ItemStack(Items.diamond_sword)).glint(true).attach(rect);
				ItemNode.create(220, 80, 80, 80).stack(damaged).attach(rect);
				ItemNode.create(310, 80, 80, 80).stack(new ItemStack(Blocks.cobblestone, 64)).attach(rect);
				TextNode.create(220, 255).text(Text.create("Items", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(520, 1000, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				RectNode
				.create(90, 70, 100, 100)
				.color(UIDemoMinecraft.INK)
				.hoveredColor(UIDemoMinecraft.PLACEHOLDER)
				.body(slot -> {
					ItemNode.create(10, 10, 80, 80).stack(new ItemStack(Items.ender_pearl, 16)).tooltip(true).attach(slot);
				})
				.attach(rect);
				RectNode
				.create(250, 70, 100, 100)
				.color(UIDemoMinecraft.INK)
				.hoveredColor(UIDemoMinecraft.PLACEHOLDER)
				.body(slot -> {
					ItemNode.create(10, 10, 80, 80).stack(new ItemStack(Items.golden_apple, 1, 1)).tooltip(true).attach(slot);
				})
				.attach(rect);
				TextNode.create(220, 255).text(Text.create("Hover", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(1000, 1000, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				BlockNode.create(40, 80, 80, 80).block(Blocks.grass).attach(rect);
				BlockNode.create(130, 80, 80, 80).block(Blocks.chest).attach(rect);
				BlockNode.create(220, 80, 80, 80).block(Blocks.glass).attach(rect);
				BlockNode.create(310, 80, 80, 80).block(Blocks.furnace).metadata(3).rotationYaw(() -> System.currentTimeMillis() % 3600L / 10D).attach(rect);
				TextNode.create(220, 255).text(Text.create("Blocks", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(1480, 1000, 400, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 360, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				RectNode
				.create(40, 80, 80, 80)
				.effect(CircleNodeEffect.create().scope(NodeEffectScope.CHILDREN))
				.body(mask -> {
					ItemNode.create(-20, -20, 120, 120).stack(new ItemStack(Blocks.grass)).attach(mask);
				})
				.attach(rect);
				ItemNode
				.create(160, 80, 80, 80)
				.stack(new ItemStack(Items.golden_carrot, 32))
				.effect(TransformNodeEffect.create(Transformation.create().rotate(30D, Rotation.ROLL)))
				.attach(rect);
				ItemNode
				.create(300, 100, 40, 40)
				.stack(damaged)
				.effect(TransformNodeEffect.create(Transformation.create().scale(Scale.create(2.5D, 2.5D, 1D))))
				.attach(rect);
				TextNode.create(200, 255).text(Text.create("Transforms", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(40, 1320, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				EntityNode.create(40, 40, 80, 160).type(EntityZombie.class).attach(rect);
				EntityNode.create(130, 40, 80, 160).type(EntityCreeper.class).rotationYaw(() -> System.currentTimeMillis() % 3600L / 10D).attach(rect);
				EntityNode.create(220, 80, 80, 80).type(EntityPig.class).rotationYaw(-30D).attach(rect);
				EntityNode.create(310, 100, 80, 80).type(EntityChicken.class).rotationPitch(20D).attach(rect);
				TextNode.create(220, 255).text(Text.create("Mobs", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(520, 1320, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				EntityNode.create(80, 40, 120, 160).profile(new GameProfile(new UUID(0L, 15L), "Steve")).attach(rect);
				EntityNode.create(240, 40, 120, 160).skin(skin).attach(rect);
				TextNode.create(220, 255).text(Text.create("Skins", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(1000, 1320, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				EntityNode.create(40, 40, 80, 160).entity(() -> minecraft.thePlayer).attach(rect);
				RectNode
				.create(160, 60, 120, 120)
				.effect(CircleNodeEffect.create().scope(NodeEffectScope.CHILDREN))
				.body(mask -> {
					EntityNode.create(-60, 5, 240, 480).entity(() -> minecraft.thePlayer).attach(mask);
				})
				.attach(rect);
				EntityNode
				.create(310, 40, 80, 160)
				.entity(() -> minecraft.thePlayer)
				.effect(TransformNodeEffect.create(Transformation.create().rotate(30D, Rotation.ROLL)))
				.attach(rect);
				TextNode.create(220, 255).text(Text.create("Local player", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(1480, 1320, 400, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 360, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				RectNode
				.create(40, 30, 320, 180)
				.color(UIDemoMinecraft.INK)
				.hoveredColor(UIDemoMinecraft.PLACEHOLDER)
				.body(slot -> {
					EntityNode.create(60, 10, 80, 160).entity(() -> minecraft.thePlayer).followMouse(true).attach(slot);
					EntityNode.create(180, 10, 80, 160).type(EntityVillager.class).followMouse(true).attach(slot);
				})
				.attach(rect);
				TextNode.create(200, 255).text(Text.create("Mouse look", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(40, 1640, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				RectNode
				.create(170, 70, 100, 100)
				.color(UIDemoMinecraft.INK)
				.hoveredColor(UIDemoMinecraft.PLACEHOLDER)
				.body(slot -> {
					ResourceNode.create(10, 10, 80, 80).resource(Resource.of(Blocks.noteblock.getIcon(0, 0))).attach(slot);
				})
				.onClick(event -> minecraft.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("note.pling"), 1F)))
				.attach(rect);
				TextNode.create(220, 255).text(Text.create("Vanilla sound", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(520, 1640, 440, 240)
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
			.create(1000, 1640, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				TextNode.create(30, 30, 380, 0).text(Text.create(() -> I18n.format("joid.demo.minecraft.keybind", GameSettings.getKeyDisplayString(minecraft.gameSettings.keyBindInventory.getKeyCode())), info)).mode(TextMode.SPLIT).attach(rect);
				TextNode.create(30, 150).text(Text.create(() -> "JOID key " + KeyResolver.resolve(minecraft.gameSettings.keyBindInventory), info)).attach(rect);
				TextNode.create(220, 255).text(Text.create("Key bind", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(1480, 1640, 400, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 360, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				TextNode.create(30, 30).text(Text.create(() -> "Pause screen " + (minecraft.currentScreen != null && minecraft.currentScreen.doesGuiPauseGame()), info)).attach(rect);
				TextNode.create(30, 80).text(Text.create(() -> "Game paused " + minecraft.isGamePaused(), info)).attach(rect);
				TextNode.create(200, 255).text(Text.create("Screen data", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(40, 1960, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode
				.create(20, 20, 400, 200)
				.color(UIDemoMinecraft.PANEL)
				.onDraw(event -> {
					final Node panel = event.getNode();
					DrawItem.inst().drawItem(panel.getX() + 20D, panel.getY() + 60D, 80D, star);
					DrawItem.inst().drawItemCount(panel.getX() + 20D, panel.getY() + 60D, 80D, star, "x3");
					DrawBlock.inst().drawBlock(panel.getX() + 140D, panel.getY() + 60D, 80D, Blocks.bookshelf, 0, 30D, 20D);
					DrawEntity.inst().drawPlayer(panel.getX() + 280D, panel.getY() + 20D, 80D, 160D, skin, -30D, 0D);
				})
				.attach(rect);
				TextNode.create(220, 255).text(Text.create("Draw calls", caption).alignX(Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			ContainerNode.create(0, 2200, 1920, 80).attach(container);
		})
		.attach(this);

		Shortcut.create(minecraft.gameSettings.keyBindInventory).onTrigger(event -> JOID.close(this)).attach(this);
	}

}