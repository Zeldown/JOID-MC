package dev.joid.backend.minecraft.demo.ui;

import java.util.UUID;

import dev.joid.backend.minecraft.lib.font.impl.minecraft.MinecraftFont;
import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.block.BlockNode;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.entity.EntityNode;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.item.ItemNode;
import dev.joid.demo.DemoFont;
import dev.joid.demo.ui.UIDemo;
import dev.joid.internal.JOID;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.text.TextMode;
import dev.joid.lib.draw.text.builder.Text;
import dev.joid.lib.font.TextInfo;
import dev.joid.lib.render.transform.Rotation;
import dev.joid.lib.render.transform.Scale;
import dev.joid.lib.render.transform.Vector;
import dev.joid.lib.render.transform.operation.RotateTransformOperation;
import dev.joid.lib.render.transform.operation.ScaleTransformOperation;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.ui.core.data.UIData;
import dev.joid.lib.ui.node.effect.NodeEffect.NodeEffectScope;
import dev.joid.lib.ui.node.effect.impl.CircleNodeEffect;
import dev.joid.lib.ui.node.effect.impl.TransformNodeEffect;
import dev.joid.lib.ui.node.impl.design.resource.ResourceNode;
import dev.joid.lib.ui.node.impl.design.shape.RectNode;
import dev.joid.lib.ui.node.impl.design.text.TextNode;
import dev.joid.lib.ui.node.impl.structure.container.ContainerNode;
import dev.joid.lib.ui.node.property.overflow.OverflowProperty;
import dev.joid.lib.utils.align.Align;

import com.mojang.authlib.GameProfile;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

@UIData(background = false)
@UIDataMinecraft(pause = false, title = "joid.demo.minecraft")
public class UIDemoMinecraft extends UIDemo {

	private static final Color INK         = new Color(153, 153, 153);
	private static final Color PANEL       = new Color(48, 48, 48);
	private static final Color PLACEHOLDER = new Color(221, 221, 221);

	@Override
	public void init() {
		final TextInfo caption = TextInfo.create(DemoFont.MONTSERRAT, 24, UIDemoMinecraft.INK);
		final TextInfo info = TextInfo.create(MinecraftFont.DEFAULT, MinecraftFont.SIZE * 3, Color.WHITE).shadowTint(0.25F).shadow(3F, 3F);
		final Component component = Component.literal("Hex ").withColor(0x12ABCD).append(Component.translatable("menu.game").withStyle(ChatFormatting.GOLD, ChatFormatting.UNDERLINE)).append(Component.literal(" plain"));
		final ItemStack damaged = new ItemStack(Items.DIAMOND_PICKAXE);
		final ItemStack pearl = new ItemStack(Items.ENDER_PEARL, 16);
		final PlayerSkin skin = PlayerSkin.insecure(new ClientAsset.ResourceTexture(Identifier.fromNamespaceAndPath("joid", "demo/skin"), Identifier.fromNamespaceAndPath("joid", "demo/textures/skin.png")), null, null, PlayerModelType.WIDE);
		damaged.setDamageValue(1100);

		ContainerNode
		.create(0, 0, 1920, 1080)
		.overflow(OverflowProperty.SCROLL)
		.body(container -> {
			RectNode
			.create(40, 40, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				TextNode.create(30, 30, 380, 0).text(Text.create("\u00A70Black \u00A71Blue \u00A72Green \u00A73Aqua \u00A74Red \u00A75Purple \u00A76Gold \u00A77Gray \u00A78Gray \u00A79Blue \u00A7aGreen \u00A7bAqua \u00A7cRed \u00A7dPink \u00A7eYellow \u00A7fWhite", info)).mode(TextMode.SPLIT).attach(rect);
				TextNode.create(220, 255).text(Text.create("Colors", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(520, 40, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				TextNode.create(30, 30, 380, 0).text(Text.create("\u00A7lBold\u00A7r \u00A7oItalic\u00A7r \u00A7l\u00A7oBoth\u00A7r \u00A7nUnderline\u00A7r \u00A7mStrike\u00A7r \u00A7kSecret\u00A7r \u00A7e\u00A7l\u00A7nAll\u00A7r Reset", info)).mode(TextMode.SPLIT).attach(rect);
				TextNode.create(220, 255).text(Text.create("Formats", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

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
			.attach(container);

			RectNode
			.create(1480, 40, 400, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 360, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				TextNode.create(30, 30, 340, 0).text(Text.create(component, info)).mode(TextMode.SPLIT).attach(rect);
				TextNode.create(200, 255).text(Text.create("Chat text", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(40, 360, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				TextNode.create(30, 30, 380, 0).text(Text.create("\u00C9t\u00E9 \u00FCber \u00F1 \u2605 \u2665 \u2713 \u65E5\u672C\u8A9E \u0391\u03B2\u03B3", info)).mode(TextMode.SPLIT).attach(rect);
				TextNode.create(220, 255).text(Text.create("Unicode", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(520, 360, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				TextNode.create(30, 30, 380, 0).text(Text.create("A long \u00A7asentence\u00A7r wraps on the width of its node and stays centered.", info, Align.CENTER)).mode(TextMode.SPLIT).attach(rect);
				TextNode.create(220, 255).text(Text.create("Wrapped", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
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
				TextNode.create(220, 255).text(Text.create("Sizes", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(1480, 360, 400, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				TextNode.create(20, 30, 360, 0).text(Text.create("\u00A7lDark\u00A7r \u00A7otext\u00A7r \u00A7nwithout\u00A7r shadow", info.copy().color(Color.BLACK).shadowTint(null))).mode(TextMode.SPLIT).attach(rect);
				TextNode.create(200, 255).text(Text.create("No shadow", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(40, 680, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				ResourceNode.create(40, 40, 360, 0).resource(Resource.of("minecraft:textures/gui/title/minecraft.png")).attach(rect);
				ResourceNode.create(40, 140, 64, 64).resource(Resource.of(Identifier.withDefaultNamespace("textures/block/diamond_block.png"))).attach(rect);
				TextNode.create(220, 255).text(Text.create("Textures", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(520, 680, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				ResourceNode.create(40, 40, 64, 64).resource(Resource.of(Sheets.ITEMS_MAPPER.defaultNamespaceApply("diamond"))).attach(rect);
				ResourceNode.create(40, 128, 64, 64).resource(Resource.of(Sheets.BLOCKS_MAPPER.defaultNamespaceApply("stone"))).attach(rect);
				ResourceNode.create(140, 40, 240, 48).resource(Resource.of(new SpriteId(Sheets.GUI_SHEET, Identifier.withDefaultNamespace("widget/button")))).attach(rect);
				ResourceNode.create(140, 120, 72, 72).resource(Resource.of(new SpriteId(Sheets.GUI_SHEET, Identifier.withDefaultNamespace("container/slot")))).attach(rect);
				TextNode.create(220, 255).text(Text.create("Sprites", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(1000, 680, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				ResourceNode.create(40, 56, 128, 128).resource(Resource.of(Sheets.BLOCKS_MAPPER.defaultNamespaceApply("fire_0"))).attach(rect);
				ResourceNode.create(240, 56, 128, 128).resource(Resource.of(Sheets.BLOCKS_MAPPER.defaultNamespaceApply("sea_lantern"))).attach(rect);
				TextNode.create(220, 255).text(Text.create("Animated sprites", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(1480, 680, 400, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 360, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				ResourceNode.create(40, 56, 128, 128).resource(Resource.of("minecraft:textures/block/sea_lantern.png")).attach(rect);
				ResourceNode.create(232, 56, 128, 128).resource(Resource.of("joid:demo/textures/pulse.png")).attach(rect);
				TextNode.create(200, 255).text(Text.create(".mcmeta", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(40, 1000, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				ItemNode.create(40, 80, 80, 80).stack(new ItemStack(Items.APPLE)).attach(rect);
				ItemNode.create(130, 80, 80, 80).stack(new ItemStack(Items.DIAMOND_SWORD)).glint(true).attach(rect);
				ItemNode.create(220, 80, 80, 80).stack(damaged).attach(rect);
				ItemNode.create(310, 80, 80, 80).stack(new ItemStack(Items.COBBLESTONE, 64)).attach(rect);
				TextNode.create(220, 255).text(Text.create("Items", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
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
					ItemNode.create(10, 10, 80, 80).stack(pearl).tooltip(true).attach(slot);
				})
				.onClick((_, _, _, _) -> Minecraft.getInstance().player.getCooldowns().addCooldown(pearl, 100))
				.attach(rect);
				RectNode
				.create(250, 70, 100, 100)
				.color(UIDemoMinecraft.INK)
				.hoveredColor(UIDemoMinecraft.PLACEHOLDER)
				.body(slot -> {
					ItemNode.create(10, 10, 80, 80).stack(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE)).tooltip(true).attach(slot);
				})
				.attach(rect);
				TextNode.create(220, 255).text(Text.create("Click & hover", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(1000, 1000, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				BlockNode.create(40, 80, 80, 80).block(Blocks.GRASS_BLOCK.defaultBlockState()).attach(rect);
				BlockNode.create(130, 80, 80, 80).block(Blocks.CHEST.defaultBlockState()).attach(rect);
				BlockNode.create(220, 80, 80, 80).block(Blocks.GLASS.defaultBlockState()).attach(rect);
				BlockNode.create(310, 80, 80, 80).block(Blocks.FURNACE.defaultBlockState()).rotationYaw(() -> System.currentTimeMillis() % 3600L / 10D).attach(rect);
				TextNode.create(220, 255).text(Text.create("Blocks", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
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
					ItemNode.create(-20, -20, 120, 120).stack(new ItemStack(Items.GRASS_BLOCK)).attach(mask);
				})
				.attach(rect);
				ItemNode
				.create(160, 80, 80, 80)
				.stack(new ItemStack(Items.GOLDEN_CARROT, 32))
				.self(item -> item.effect(TransformNodeEffect.create(new RotateTransformOperation(30D, Rotation.ROLL, Vector.create(() -> item.getX() + 40D, () -> item.getY() + 40D)))))
				.attach(rect);
				ItemNode
				.create(300, 100, 40, 40)
				.stack(damaged)
				.self(item -> item.effect(TransformNodeEffect.create(new ScaleTransformOperation(Scale.create(2.5D, 2.5D, 1D), Vector.create(() -> item.getX() + 20D, () -> item.getY() + 20D)))))
				.attach(rect);
				TextNode.create(200, 255).text(Text.create("Transforms", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(40, 1320, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				EntityNode.create(40, 40, 80, 160).type(EntityTypes.ZOMBIE).attach(rect);
				EntityNode.create(130, 40, 80, 160).type(EntityTypes.CREEPER).rotationYaw(() -> System.currentTimeMillis() % 3600L / 10D).attach(rect);
				EntityNode.create(220, 80, 80, 80).type(EntityTypes.PIG).rotationYaw(-30D).attach(rect);
				EntityNode.create(310, 100, 80, 80).type(EntityTypes.CHICKEN).rotationPitch(20D).attach(rect);
				TextNode.create(220, 255).text(Text.create("Mobs", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(520, 1320, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				EntityNode.create(80, 40, 120, 160).profile(new GameProfile(new UUID(0L, 15L), "Steve")).attach(rect);
				EntityNode.create(240, 40, 120, 160).skin(skin).attach(rect);
				TextNode.create(220, 255).text(Text.create("Skins", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(1000, 1320, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				EntityNode.create(40, 40, 80, 160).entity(() -> Minecraft.getInstance().player).attach(rect);
				RectNode
				.create(160, 60, 120, 120)
				.effect(CircleNodeEffect.create().scope(NodeEffectScope.CHILDREN))
				.body(mask -> {
					EntityNode.create(-60, 5, 240, 480).entity(() -> Minecraft.getInstance().player).attach(mask);
				})
				.attach(rect);
				EntityNode
				.create(310, 40, 80, 160)
				.entity(() -> Minecraft.getInstance().player)
				.self(entity -> entity.effect(TransformNodeEffect.create(new RotateTransformOperation(30D, Rotation.ROLL, Vector.create(() -> entity.getX() + 40D, () -> entity.getY() + 80D)))))
				.attach(rect);
				TextNode.create(220, 255).text(Text.create("Local player", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
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
					EntityNode.create(60, 10, 80, 160).entity(() -> Minecraft.getInstance().player).followMouse(true).attach(slot);
					EntityNode.create(180, 10, 80, 160).type(EntityTypes.VILLAGER).followMouse(true).attach(slot);
				})
				.attach(rect);
				TextNode.create(200, 255).text(Text.create("Mouse look", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
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
					ResourceNode.create(10, 10, 80, 80).resource(Resource.of(Sheets.BLOCKS_MAPPER.defaultNamespaceApply("note_block"))).attach(slot);
				})
				.onClick((_, _, _, _) -> Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_PLING, 1F)))
				.attach(rect);
				TextNode.create(220, 255).text(Text.create("Vanilla sound", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			RectNode
			.create(520, 1640, 440, 240)
			.color(UIDemoMinecraft.PLACEHOLDER)
			.body(rect -> {
				RectNode.create(20, 20, 400, 200).color(UIDemoMinecraft.PANEL).attach(rect);
				TextNode.create(30, 30).text(Text.create(() -> "GUI scale " + Minecraft.getInstance().getWindow().getGuiScale(), info)).attach(rect);
				TextNode.create(30, 60).text(Text.create(() -> String.format("Interface scale %.2f", super.getView().getInterfaceScale()), info)).attach(rect);
				TextNode.create(30, 90).text(Text.create(() -> "Active " + super.getScale().active() + ", limited " + super.getScale().limited(), info)).attach(rect);
				RectNode
				.create(30, 140, 180, 60)
				.color(UIDemoMinecraft.INK)
				.hoveredColor(UIDemoMinecraft.PLACEHOLDER)
				.body(toggle -> {
					TextNode.create(90, 30).text(Text.create("Active", info, Align.CENTER)).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(toggle);
				})
				.onClick((_, _, _, _) -> super.getScale().setActive(!super.getScale().active()))
				.attach(rect);
				RectNode
				.create(230, 140, 180, 60)
				.color(UIDemoMinecraft.INK)
				.hoveredColor(UIDemoMinecraft.PLACEHOLDER)
				.body(toggle -> {
					TextNode.create(90, 30).text(Text.create("Limit 0.75", info, Align.CENTER)).anchorX(Align.CENTER).anchorY(Align.CENTER).attach(toggle);
				})
				.onClick((_, _, _, _) -> super.getScale().setLimited(!super.getScale().limited()).setLimit(0.75D))
				.attach(rect);
				TextNode.create(220, 255).text(Text.create("Interface scale", caption, Align.CENTER)).anchorX(Align.CENTER).attach(rect);
			})
			.attach(container);

			ContainerNode.create(0, 1880, 1920, 80).attach(container);
		})
		.attach(this);

		super.keybind(() -> JOID.close(this), Minecraft.getInstance().options.keyInventory);
	}

}