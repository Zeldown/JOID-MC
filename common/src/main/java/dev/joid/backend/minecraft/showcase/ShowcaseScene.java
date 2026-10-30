package dev.joid.backend.minecraft.showcase;

import java.util.Arrays;

import dev.joid.backend.minecraft.showcase.ui.IShowFocus;
import dev.joid.backend.minecraft.showcase.ui.ShowAnvil;
import dev.joid.backend.minecraft.showcase.ui.ShowBestiary;
import dev.joid.backend.minecraft.showcase.ui.ShowChest;
import dev.joid.backend.minecraft.showcase.ui.ShowDaylight;
import dev.joid.backend.minecraft.showcase.ui.ShowFonts;
import dev.joid.backend.minecraft.showcase.ui.ShowHud;
import dev.joid.backend.minecraft.showcase.ui.ShowQuests;
import dev.joid.backend.minecraft.showcase.ui.ShowServers;
import dev.joid.backend.minecraft.showcase.ui.ShowVideo;
import dev.joid.lib.JOID;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.node.Node;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldSettings;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ShowcaseScene {

	HUD(ShowcaseScene::hud),
	CHEST(ShowcaseScene::chest),
	ANVIL(ShowcaseScene::anvil),
	FONTS(ShowcaseScene::fonts),
	VIDEO(ShowcaseScene::video),
	DESIGN(ShowcaseScene::design),
	SERVERS(ShowcaseScene::servers),
	BESTIARY(ShowcaseScene::bestiary),
	DAYLIGHT(ShowcaseScene::daylight);

	private static boolean prepared;

	private final Runnable action;

	public void play() {
		ShowcaseScene.prepare();
		this.action.run();
	}

	private static void prepare() {
		if (ShowcaseScene.prepared) {
			return;
		}

		ShowcaseScene.prepared = true;
		ShowcaseDirector.command("gamerule doDaylightCycle false");
		ShowcaseDirector.command("gamerule doMobSpawning false");
		ShowcaseDirector.command("gamerule naturalRegeneration false");
		ShowcaseDirector.command("time set 4000");
		ShowcaseDirector.command("weather clear 1000000");
		ShowcaseDirector.runOnServer(player -> {
			player.setGameType(WorldSettings.GameType.SURVIVAL);
			player.clearActivePotions();
			player.experienceLevel = 27;
			player.experience      = 62F / player.xpBarCap();
			final String spot = System.getProperty("joid.showcase.spot");
			final double[] place = spot == null ? new double[] {player.posX, player.posZ, 180D, 6D} : Arrays.stream(spot.split(" ")).mapToDouble(Double::parseDouble).toArray();
			final int ground = player.worldObj.getTopSolidOrLiquidBlock((int) Math.floor(place[0]), (int) Math.floor(place[1]));
			player.playerNetServerHandler.setPlayerLocation(place[0], ground + 0.01D, place[1], (float) place[2], (float) place[3]);
			final InventoryPlayer inventory = player.inventory;
			for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
				inventory.setInventorySlotContents(slot, null);
			}
			inventory.setInventorySlotContents(0, ShowcaseScene.enchant(new ItemStack(Items.diamond_sword), Enchantment.sharpness, 5));
			inventory.setInventorySlotContents(1, ShowcaseScene.enchant(new ItemStack(Items.diamond_pickaxe), Enchantment.efficiency, 4));
			inventory.setInventorySlotContents(2, new ItemStack(Items.iron_axe));
			inventory.setInventorySlotContents(3, new ItemStack(Items.bow));
			inventory.setInventorySlotContents(4, new ItemStack(Items.cooked_beef, 24));
			inventory.setInventorySlotContents(5, new ItemStack(Blocks.torch, 64));
			inventory.setInventorySlotContents(6, new ItemStack(Blocks.planks, 64));
			inventory.setInventorySlotContents(7, new ItemStack(Items.water_bucket));
			inventory.setInventorySlotContents(8, new ItemStack(Items.map));
			inventory.setInventorySlotContents(9, new ItemStack(Items.arrow, 48));
			inventory.setInventorySlotContents(10, new ItemStack(Blocks.cobblestone, 64));
			inventory.setInventorySlotContents(11, new ItemStack(Items.iron_ingot, 21));
			inventory.setInventorySlotContents(14, new ItemStack(Items.redstone, 37));
			inventory.setInventorySlotContents(18, new ItemStack(Items.iron_chestplate));
			inventory.setInventorySlotContents(22, new ItemStack(Blocks.glass, 32));
			inventory.setInventorySlotContents(27, new ItemStack(Items.wheat_seeds, 12));
			inventory.setInventorySlotContents(31, new ItemStack(Items.dye, 18, 4));
			inventory.currentItem = 0;
			player.inventoryContainer.detectAndSendChanges();
		});
		ShowcaseDirector.runOnClient(() -> Minecraft.getMinecraft().thePlayer.inventory.currentItem = 0);
		ShowcaseDirector.waitForWorld();
		ShowcaseDirector.runOnServer(player -> {
			player.setHealth(15F);
			player.getFoodStats().setFoodLevel(17);
			player.getFoodStats().setFoodSaturationLevel(5F);
		});
		ShowcaseClock.freeze();
		ShowcaseDirector.runOnClient(ShowcaseScene::openHud);
		ShowcaseDirector.hidePointer();
		ShowcaseDirector.frames(30);
	}

	private static void hud() {
		ShowcaseDirector.runOnClient(ShowcaseScene::closeHud);
		ShowcaseDirector.frames(3);
		ShowcaseDirector.begin("hud");
		ShowcaseDirector.frames(10);
		ShowcaseDirector.runOnClient(ShowcaseScene::openHud);
		ShowcaseDirector.frames(40);
		final EntityClientPlayerMP player = ShowcaseDirector.computeOnClient(() -> Minecraft.getMinecraft().thePlayer);
		final float experience = player.experience;
		ShowcaseDirector.animate(24, progress -> player.experience = (float) (experience + (1D - experience) * progress));
		ShowcaseDirector.runOnClient(() -> {
			player.experienceLevel++;
			player.experience = 0F;
		});
		ShowcaseDirector.animate(12, progress -> player.experience = (float) (0.2D * progress));
		ShowcaseDirector.frames(8);
		ShowcaseDirector.animate(14, progress -> player.setHealth((float) (15D - 8D * progress)));
		ShowcaseDirector.frames(10);
		ShowcaseDirector.animate(24, progress -> {
			player.setHealth((float) (7D + 13D * progress));
			player.getFoodStats().setFoodLevel((int) Math.round(17D - 4D * progress));
		});
		ShowcaseDirector.frames(30);
		ShowcaseDirector.end();
		ShowcaseDirector.runOnClient(() -> {
			player.experienceLevel = player.experienceLevel - 1;
			player.experience      = experience;
			player.setHealth(15F);
			player.getFoodStats().setFoodLevel(17);
		});
	}

	private static void chest() {
		ShowcaseDirector.frames(3);
		ShowcaseDirector.begin("chest");
		ShowcaseDirector.frames(5);
		ShowcaseDirector.runOnServer(player -> {
			final ItemStack book = new ItemStack(Items.enchanted_book);
			Items.enchanted_book.addEnchantment(book, new EnchantmentData(Enchantment.protection, 4));
			final ItemStack[] items = {
				ShowcaseScene.enchant(new ItemStack(Items.diamond_sword), Enchantment.sharpness, 5),
				new ItemStack(Items.golden_apple, 6),
				new ItemStack(Items.ender_pearl, 16),
				new ItemStack(Items.emerald, 48),
				new ItemStack(Items.diamond, 12),
				new ItemStack(Items.golden_apple, 1, 1),
				new ItemStack(Items.experience_bottle, 32),
				new ItemStack(Items.name_tag),
				new ItemStack(Items.nether_star),
				new ItemStack(Items.iron_ingot, 64),
				new ItemStack(Items.gold_ingot, 40),
				book,
				null,
				new ItemStack(Items.bread, 32),
				null,
				new ItemStack(Items.quartz, 23),
				new ItemStack(Blocks.log, 64),
				new ItemStack(Items.clock),
				new ItemStack(Items.compass),
				null,
				new ItemStack(Items.cake),
				new ItemStack(Items.saddle),
				null,
				new ItemStack(Items.fireworks, 64),
				new ItemStack(Items.diamond_chestplate),
				null,
				new ItemStack(Items.ender_eye)
			};
			for (int index = 0; index < items.length; index++) {
				ShowcaseLauncher.STORAGE.setInventorySlotContents(index, items[index]);
			}
			ShowcaseLauncher.CHEST.open(player);
		});
		ShowcaseDirector.waitFor("the showcase chest", () -> JOID.isOpen(ShowChest.class), 60);
		ShowcaseDirector.frames(25);
		ShowcaseDirector.move(1700D, 760D);
		ShowcaseDirector.glide(ShowcaseScene.slot(0), 20);
		ShowcaseDirector.frames(20);
		ShowcaseDirector.capture("chest");
		ShowcaseDirector.click();
		ShowcaseDirector.frames(5);
		final double[] target = ShowcaseDirector.computeOnClient(() -> ShowcaseDirector.bounds(ShowcaseScene.slotNode(14)));
		ShowcaseDirector.glide(target[0] + target[2] * 0.85D, target[1] + target[3] * 0.8D, 23);
		ShowcaseDirector.capture("chest-drag");
		ShowcaseDirector.frames(5);
		ShowcaseDirector.click();
		ShowcaseDirector.frames(10);
		ShowcaseDirector.glide(ShowcaseScene.slot(3), 18);
		ShowcaseDirector.frames(25);
		ShowcaseDirector.end();
		ShowcaseDirector.hidePointer();
		ShowcaseDirector.runOnClient(() -> Minecraft.getMinecraft().thePlayer.closeScreen());
		ShowcaseDirector.frames(5);
	}

	private static void anvil() {
		ShowcaseDirector.frames(3);
		ShowcaseDirector.begin("anvil");
		ShowcaseDirector.frames(5);
		ShowcaseDirector.runOnServer(player -> {
			final ItemStack sword = player.inventory.getStackInSlot(0);
			player.inventory.setInventorySlotContents(0, null);
			ShowcaseLauncher.ANVIL.open(player);
			player.openContainer.getSlot(ShowAnvil.INPUT_SLOT).putStack(sword);
			player.openContainer.detectAndSendChanges();
		});
		ShowcaseDirector.waitFor("the showcase anvil", () -> JOID.isOpen(ShowAnvil.class), 60);
		final ShowAnvil ui = ShowcaseDirector.computeOnClient(() -> JOID.getUi(ShowAnvil.class));
		ShowcaseDirector.frames(25);
		ShowcaseDirector.move(1700D, 900D);
		final double[] field = ShowcaseDirector.computeOnClient(() -> ShowcaseDirector.bounds(ui.getFocus()));
		ShowcaseDirector.glide(field[0] + field[2] * 84D / 780D, field[1] + field[3] / 2D, 20);
		ShowcaseDirector.frames(10);
		ShowcaseDirector.click();
		ShowcaseDirector.frames(2);
		ShowcaseDirector.click();
		ShowcaseDirector.frames(12);
		ShowcaseDirector.type("Frostbite", 3);
		ShowcaseDirector.frames(15);
		ShowcaseDirector.capture("anvil");
		ShowcaseDirector.glide(ShowcaseDirector.computeOnClient(() -> ui.getSlotNode(ShowAnvil.RESULT_SLOT)), 20);
		ShowcaseDirector.frames(20);
		ShowcaseDirector.click();
		ShowcaseDirector.frames(5);
		ShowcaseDirector.glide(ShowcaseDirector.computeOnClient(() -> ui.getSlotNode(30)), 22);
		ShowcaseDirector.frames(4);
		ShowcaseDirector.click();
		ShowcaseDirector.frames(30);
		ShowcaseDirector.end();
		ShowcaseDirector.hidePointer();
		ShowcaseDirector.runOnClient(() -> Minecraft.getMinecraft().thePlayer.closeScreen());
		ShowcaseDirector.frames(5);
		ShowcaseDirector.runOnServer(player -> {
			player.inventory.setInventorySlotContents(0, ShowcaseScene.enchant(new ItemStack(Items.diamond_sword), Enchantment.sharpness, 5));
			player.experienceLevel = 27;
			player.experience      = 62F / player.xpBarCap();
			player.inventoryContainer.detectAndSendChanges();
		});
		ShowcaseDirector.frames(5);
	}

	private static void fonts() {
		final ShowFonts ui = new ShowFonts();
		ShowcaseDirector.frames(3);
		ShowcaseDirector.begin("fonts");
		ShowcaseDirector.frames(5);
		ShowcaseDirector.runOnClient(() -> JOID.open(ui));
		ShowcaseDirector.frames(135);
		ShowcaseDirector.capture("fonts");
		ShowcaseDirector.frames(85);
		ShowcaseDirector.end();
		ShowcaseDirector.runOnClient(() -> JOID.close(ui));
		ShowcaseDirector.frames(20);
	}

	private static void video() {
		final ShowVideo ui = new ShowVideo();
		ShowcaseDirector.frames(3);
		ShowcaseDirector.begin("video");
		ShowcaseDirector.frames(5);
		ShowcaseDirector.runOnClient(() -> JOID.open(ui));
		ShowcaseDirector.waitFor("the showcase video", () -> ui.getPlayer().getDuration() > 0D, 300);
		ShowcaseDirector.runOnClient(() -> ui.getPlayer().seek(40D));
		ShowcaseDirector.sleep(3000);
		ShowcaseDirector.frames(35);
		final double[] button = ShowcaseDirector.computeOnClient(() -> ShowcaseDirector.bounds(ui.getFocus()));
		ShowcaseDirector.move(1700D, 900D);
		ShowcaseDirector.glide(button[0] + button[2], button[1] + button[3] + 15D, 16);
		ShowcaseDirector.glide(button[0] + button[2] / 2D, button[1] + button[3] / 2D, 8);
		ShowcaseDirector.frames(10);
		ShowcaseDirector.click();
		ShowcaseDirector.frames(24);
		ShowcaseDirector.click();
		ShowcaseDirector.frames(12);
		final double[] timeline = ShowcaseDirector.computeOnClient(() -> ShowcaseDirector.bounds(ui.getTimeline()));
		ShowcaseDirector.glide(timeline[0] + timeline[2] * 0.3D, timeline[1] + timeline[3] / 2D, 25);
		ShowcaseDirector.frames(20);
		ShowcaseDirector.capture("video");
		ShowcaseDirector.glide(timeline[0] + timeline[2] * 0.75D, timeline[1] + timeline[3] / 2D, 35);
		ShowcaseDirector.frames(20);
		ShowcaseDirector.glide(1700D, 900D, 15);
		ShowcaseDirector.frames(5);
		ShowcaseDirector.end();
		ShowcaseDirector.hidePointer();
		ShowcaseDirector.runOnClient(() -> JOID.close(ui));
		ShowcaseDirector.frames(20);
	}

	private static void design() {
		ShowcaseScene.hideHud(true);
		ShowcaseDirector.frames(5);
		ShowcaseDirector.capture("world");
		ShowcaseScene.open(new ShowQuests(), "design", () -> ShowcaseDirector.frames(15));
		ShowcaseScene.hideHud(false);
	}

	private static void servers() {
		final ShowServers ui = new ShowServers();
		ShowcaseScene.open(ui, "servers", () -> {
			final double[] top = ShowcaseDirector.computeOnClient(() -> ShowcaseDirector.bounds(ui.getRow(0)));
			ShowcaseDirector.press();
			ShowcaseDirector.frames(4);
			ShowcaseDirector.glide(top[0] + top[2] / 2D, top[1] + top[3] / 2D, 30);
			ShowcaseDirector.frames(6);
			ShowcaseDirector.release();
			ShowcaseDirector.frames(15);
			for (int notch = 0; notch < 6; notch++) {
				ShowcaseDirector.scroll(-1D);
				ShowcaseDirector.frames(3);
			}
			ShowcaseDirector.frames(10);
			for (int notch = 0; notch < 6; notch++) {
				ShowcaseDirector.scroll(1D);
				ShowcaseDirector.frames(3);
			}
			ShowcaseDirector.frames(15);
		});
	}

	private static void bestiary() {
		final ShowBestiary ui = new ShowBestiary();
		ShowcaseScene.open(ui, "bestiary", () -> {
			ShowcaseDirector.glide(ui.getCard(1), 15);
			ShowcaseDirector.frames(25);
			ShowcaseDirector.glide(ui.getCard(4), 15);
			ShowcaseDirector.frames(25);
			ShowcaseDirector.glide(ui.getCard(6), 15);
			ShowcaseDirector.frames(25);
		});
	}

	private static void daylight() {
		final ShowDaylight ui = new ShowDaylight();
		ShowcaseScene.hideHud(true);
		ShowcaseDirector.frames(3);
		ShowcaseDirector.begin("daylight");
		ShowcaseDirector.frames(5);
		ShowcaseDirector.runOnClient(() -> JOID.open(ui));
		ShowcaseDirector.frames(30);
		ShowcaseDirector.animate(66, progress -> ui.getTime().set(Math.round((4000D + 10500D * progress) / 50D) * 50D));
		ShowcaseDirector.frames(10);
		ShowcaseDirector.capture("daylight");
		final double[] slider = ShowcaseDirector.computeOnClient(() -> ShowcaseDirector.bounds(ui.getFocus()));
		ShowcaseDirector.move(1700D, 900D);
		ShowcaseDirector.glide(slider[0] + slider[2] * (18D + 14500D / 48000D * 468D) / 504D, slider[1] + slider[3] / 2D, 22);
		ShowcaseDirector.frames(6);
		ShowcaseDirector.press();
		ShowcaseDirector.frames(3);
		ShowcaseDirector.glide(slider[0] + slider[2] * (18D + 30000D / 48000D * 468D) / 504D, slider[1] + slider[3] / 2D, 50);
		ShowcaseDirector.frames(6);
		ShowcaseDirector.release();
		ShowcaseDirector.frames(30);
		ShowcaseDirector.end();
		ShowcaseDirector.hidePointer();
		ShowcaseDirector.runOnClient(() -> JOID.close(ui));
		ShowcaseDirector.command("time set 4000");
		ShowcaseScene.hideHud(false);
		ShowcaseDirector.frames(20);
	}

	private static void open(final UI ui, final String name, final Runnable script) {
		ShowcaseDirector.frames(3);
		ShowcaseDirector.begin(name);
		ShowcaseDirector.frames(5);
		ShowcaseDirector.runOnClient(() -> JOID.open(ui));
		ShowcaseDirector.frames(35);
		ShowcaseDirector.move(1700D, 900D);
		ShowcaseDirector.glide(ShowcaseDirector.computeOnClient(((IShowFocus) ui)::getFocus), 20);
		ShowcaseDirector.frames(20);
		ShowcaseDirector.capture(name);
		script.run();
		ShowcaseDirector.end();
		ShowcaseDirector.hidePointer();
		ShowcaseDirector.runOnClient(() -> JOID.close(ui));
		ShowcaseDirector.frames(20);
	}

	private static void hideHud(final boolean hidden) {
		ShowcaseDirector.runOnClient(() -> Minecraft.getMinecraft().gameSettings.hideGUI = hidden);
	}

	private static Node slot(final int index) {
		return ShowcaseDirector.computeOnClient(() -> ShowcaseScene.slotNode(index));
	}

	private static Node slotNode(final int index) {
		return JOID.getUi(ShowChest.class).getSlotNode(index);
	}

	private static void openHud() {
		JOID.open(new ShowHud());
		JOID.open(new ShowHud.Food());
		JOID.open(new ShowHud.Experience());
	}

	private static void closeHud() {
		JOID.close(JOID.getUi(ShowHud.class));
		JOID.close(JOID.getUi(ShowHud.Food.class));
		JOID.close(JOID.getUi(ShowHud.Experience.class));
	}

	private static ItemStack enchant(final ItemStack stack, final Enchantment enchantment, final int level) {
		stack.addEnchantment(enchantment, level);
		return stack;
	}

}