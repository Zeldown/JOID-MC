package dev.joid.backend.minecraft.showcase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dev.joid.backend.minecraft.showcase.ui.IShowFocus;
import dev.joid.backend.minecraft.showcase.ui.ShowAnvil;
import dev.joid.backend.minecraft.showcase.ui.ShowBestiary;
import dev.joid.backend.minecraft.showcase.ui.ShowChest;
import dev.joid.backend.minecraft.showcase.ui.ShowFonts;
import dev.joid.backend.minecraft.showcase.ui.ShowHud;
import dev.joid.backend.minecraft.showcase.ui.ShowPause;
import dev.joid.backend.minecraft.showcase.ui.ShowQuests;
import dev.joid.internal.JOID;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.node.Node;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import com.mojang.datafixers.util.Pair;

import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ShowcaseScene {

	HUD(ShowcaseScene::hud),
	CHEST(ShowcaseScene::chest),
	PAUSE(ShowcaseScene::pause),
	ANVIL(ShowcaseScene::anvil),
	FONTS(ShowcaseScene::fonts),
	DESIGN(ShowcaseScene::design),
	LOADER(ShowcaseScene::loader),
	EXPLORE(ShowcaseScene::explore),
	BESTIARY(ShowcaseScene::bestiary);

	public static final String SEED = "1700";

	private static final double[] SPOT = {384.5D, -239.5D, 180D, 6D};

	private static boolean prepared;

	private final Runnable action;

	public void play() {
		if (this != ShowcaseScene.EXPLORE) {
			ShowcaseScene.prepare();
		}

		this.action.run();
	}

	private static void prepare() {
		if (ShowcaseScene.prepared) {
			return;
		}

		ShowcaseScene.prepared = true;
		final String spot = System.getProperty("joid.showcase.spot");
		final double[] place = spot == null ? ShowcaseScene.SPOT : List.of(spot.split(" ")).stream().mapToDouble(Double::parseDouble).toArray();
		ShowcaseDirector.command("tick unfreeze");
		ShowcaseDirector.command("gamemode survival @a");
		ShowcaseDirector.command("time set 4000");
		ShowcaseDirector.command("weather clear 1000000");
		ShowcaseDirector.command("effect clear @a");
		ShowcaseDirector.command("kill @e[type=minecraft:item]");
		ShowcaseDirector.command("xp set @a 27 levels");
		ShowcaseDirector.command("xp set @a 62 points");
		ShowcaseScene.teleport(place);
		ShowcaseDirector.runOnServer(player -> {
			final Inventory inventory = player.getInventory();
			inventory.clearContent();
			inventory.setItem(0, ShowcaseScene.enchant(player.level(), new ItemStack(Items.DIAMOND_SWORD), Enchantments.SHARPNESS, 5));
			inventory.setItem(1, ShowcaseScene.enchant(player.level(), new ItemStack(Items.DIAMOND_PICKAXE), Enchantments.EFFICIENCY, 4));
			inventory.setItem(2, new ItemStack(Items.IRON_AXE));
			inventory.setItem(3, new ItemStack(Items.BOW));
			inventory.setItem(4, new ItemStack(Items.COOKED_BEEF, 24));
			inventory.setItem(5, new ItemStack(Items.TORCH, 64));
			inventory.setItem(6, new ItemStack(Items.OAK_PLANKS, 64));
			inventory.setItem(7, new ItemStack(Items.WATER_BUCKET));
			inventory.setItem(8, new ItemStack(Items.MAP));
			inventory.setItem(9, new ItemStack(Items.ARROW, 48));
			inventory.setItem(10, new ItemStack(Items.COBBLESTONE, 64));
			inventory.setItem(11, new ItemStack(Items.IRON_INGOT, 21));
			inventory.setItem(14, new ItemStack(Items.REDSTONE, 37));
			inventory.setItem(18, new ItemStack(Items.SHIELD));
			inventory.setItem(22, new ItemStack(Items.GLASS, 32));
			inventory.setItem(27, new ItemStack(Items.WHEAT_SEEDS, 12));
			inventory.setItem(31, new ItemStack(Items.LAPIS_LAZULI, 18));
			inventory.setSelectedSlot(0);
		});
		ShowcaseDirector.runOnClient(() -> Minecraft.getInstance().player.getInventory().setSelectedSlot(0));
		ShowcaseDirector.waitForWorld();
		ShowcaseDirector.command("tick freeze");
		ShowcaseDirector.runOnServer(player -> {
			player.setHealth(15F);
			player.getFoodData().setFoodLevel(17);
			player.getFoodData().setSaturation(5F);
		});
		ShowcaseClock.freeze();
		ShowcaseDirector.runOnClient(() -> {
			Minecraft.getInstance().gui.toastManager().clear();
			ShowcaseScene.openHud();
		});
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
		final LocalPlayer player = ShowcaseDirector.computeOnClient(() -> Minecraft.getInstance().player);
		final float experience = player.experienceProgress;
		ShowcaseDirector.animate(24, progress -> player.experienceProgress = (float) (experience + (1D - experience) * progress));
		ShowcaseDirector.runOnClient(() -> {
			player.experienceLevel++;
			player.experienceProgress = 0F;
		});
		ShowcaseDirector.animate(12, progress -> player.experienceProgress = (float) (0.2D * progress));
		ShowcaseDirector.frames(8);
		ShowcaseDirector.animate(14, progress -> player.setHealth((float) (15D - 8D * progress)));
		ShowcaseDirector.frames(10);
		ShowcaseDirector.animate(24, progress -> {
			player.setHealth((float) (7D + 13D * progress));
			player.getFoodData().setFoodLevel((int) Math.round(17D - 4D * progress));
		});
		ShowcaseDirector.frames(30);
		ShowcaseDirector.end();
		ShowcaseDirector.runOnClient(() -> {
			player.experienceLevel    = player.experienceLevel - 1;
			player.experienceProgress = experience;
			player.setHealth(15F);
			player.getFoodData().setFoodLevel(17);
		});
	}

	private static void chest() {
		ShowcaseDirector.frames(3);
		ShowcaseDirector.begin("chest");
		ShowcaseDirector.frames(5);
		ShowcaseDirector.runOnServer(player -> {
			final SimpleContainer storage = new SimpleContainer(27);
			final ItemStack[] items = {
				ShowcaseScene.enchant(player.level(), new ItemStack(Items.NETHERITE_SWORD), Enchantments.SHARPNESS, 5),
				new ItemStack(Items.GOLDEN_APPLE, 6),
				new ItemStack(Items.ENDER_PEARL, 16),
				new ItemStack(Items.EMERALD, 48),
				new ItemStack(Items.DIAMOND, 12),
				new ItemStack(Items.ENCHANTED_GOLDEN_APPLE),
				new ItemStack(Items.EXPERIENCE_BOTTLE, 32),
				new ItemStack(Items.NAME_TAG),
				new ItemStack(Items.TOTEM_OF_UNDYING),
				new ItemStack(Items.IRON_INGOT, 64),
				new ItemStack(Items.GOLD_INGOT, 40),
				ShowcaseScene.enchant(player.level(), new ItemStack(Items.ENCHANTED_BOOK), Enchantments.MENDING, 1),
				ItemStack.EMPTY,
				new ItemStack(Items.BREAD, 32),
				ItemStack.EMPTY,
				new ItemStack(Items.AMETHYST_SHARD, 23),
				new ItemStack(Items.OAK_LOG, 64),
				new ItemStack(Items.SPYGLASS),
				new ItemStack(Items.COMPASS),
				ItemStack.EMPTY,
				new ItemStack(Items.CAKE),
				new ItemStack(Items.SADDLE),
				ItemStack.EMPTY,
				new ItemStack(Items.FIREWORK_ROCKET, 64),
				new ItemStack(Items.ELYTRA),
				ItemStack.EMPTY,
				new ItemStack(Items.HEART_OF_THE_SEA)
			};
			for (int index = 0; index < items.length; index++) {
				storage.setItem(index, items[index]);
			}
			player.openMenu(new SimpleMenuProvider((containerId, inventory, _) -> new ChestMenu(ShowcaseLauncher.CHEST, containerId, inventory, storage, 3), Component.translatable("container.chest")));
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
		ShowcaseDirector.runOnClient(() -> Minecraft.getInstance().player.closeContainer());
		ShowcaseDirector.frames(5);
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

	private static void fonts() {
		final ShowFonts ui = new ShowFonts();
		ShowcaseDirector.frames(3);
		ShowcaseDirector.begin("fonts");
		ShowcaseDirector.frames(5);
		ShowcaseDirector.runOnClient(() -> JOID.open(ui));
		ShowcaseDirector.frames(70);
		ShowcaseDirector.capture("fonts");
		ShowcaseDirector.frames(150);
		ShowcaseDirector.end();
		ShowcaseDirector.runOnClient(() -> JOID.close(ui));
		ShowcaseDirector.frames(20);
	}

	private static void anvil() {
		ShowcaseDirector.frames(3);
		ShowcaseDirector.begin("anvil");
		ShowcaseDirector.frames(5);
		ShowcaseDirector.runOnServer(player -> {
			final ItemStack sword = player.getInventory().removeItemNoUpdate(0);
			player.openMenu(new SimpleMenuProvider((containerId, inventory, _) -> {
				final AnvilMenu menu = new AnvilMenu(containerId, inventory, ContainerLevelAccess.NULL);
				menu.getSlot(AnvilMenu.INPUT_SLOT).set(sword);
				return menu;
			}, Component.translatable("container.repair")));
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
		ShowcaseDirector.glide(ShowcaseDirector.computeOnClient(() -> ui.getSlotNode(AnvilMenu.RESULT_SLOT)), 20);
		ShowcaseDirector.frames(20);
		ShowcaseDirector.click();
		ShowcaseDirector.frames(5);
		ShowcaseDirector.glide(ShowcaseDirector.computeOnClient(() -> ui.getSlotNode(30)), 22);
		ShowcaseDirector.frames(4);
		ShowcaseDirector.click();
		ShowcaseDirector.frames(30);
		ShowcaseDirector.end();
		ShowcaseDirector.hidePointer();
		ShowcaseDirector.runOnClient(() -> Minecraft.getInstance().player.closeContainer());
		ShowcaseDirector.frames(5);
		ShowcaseDirector.runOnServer(player -> player.getInventory().setItem(0, ShowcaseScene.enchant(player.level(), new ItemStack(Items.DIAMOND_SWORD), Enchantments.SHARPNESS, 5)));
		ShowcaseDirector.command("xp set @a 27 levels");
		ShowcaseDirector.command("xp set @a 62 points");
		ShowcaseDirector.frames(5);
	}

	private static void pause() {
		final ShowPause ui = new ShowPause();
		ShowcaseScene.open(ui, "pause", () -> {
			ShowcaseDirector.glide(ui.getButton(3), 15);
			ShowcaseDirector.frames(20);
		});
	}

	private static void design() {
		ShowcaseScene.hideHud(true);
		ShowcaseDirector.frames(5);
		ShowcaseDirector.capture("world");
		ShowcaseScene.open(new ShowQuests(), "design", () -> ShowcaseDirector.frames(15));
		ShowcaseScene.hideHud(false);
	}

	private static void loader() {
		final String brand = ClientBrandRetriever.getClientModName();
		final int card = brand.equals("fabric") ? 1 : brand.equals("neoforge") ? 4 : 6;
		final ShowBestiary ui = new ShowBestiary();
		ShowcaseDirector.runOnClient(() -> JOID.open(ui));
		ShowcaseDirector.frames(40);
		ShowcaseDirector.move(ShowcaseDirector.computeOnClient(() -> ShowcaseDirector.bounds(ui.getCard(card - 1)))[0] + 150D, 520D);
		ShowcaseDirector.begin("loader-" + brand);
		ShowcaseDirector.glide(ui.getCard(card), 15);
		ShowcaseDirector.frames(60);
		ShowcaseDirector.capture("loader-" + brand);
		ShowcaseDirector.frames(15);
		ShowcaseDirector.end();
		ShowcaseDirector.hidePointer();
		ShowcaseDirector.runOnClient(() -> JOID.close(ui));
		ShowcaseDirector.frames(10);
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
		ShowcaseDirector.runOnClient(() -> {
			if (Minecraft.getInstance().gui.hud.isHidden() != hidden) {
				Minecraft.getInstance().gui.hud.toggle();
			}
		});
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
		JOID.open(new ShowHud.Level());
		JOID.open(new ShowHud.Experience());
	}

	private static void closeHud() {
		JOID.close(JOID.getUi(ShowHud.class));
		JOID.close(JOID.getUi(ShowHud.Food.class));
		JOID.close(JOID.getUi(ShowHud.Level.class));
		JOID.close(JOID.getUi(ShowHud.Experience.class));
	}

	private static void explore() {
		ShowcaseDirector.command("gamemode spectator @a");
		ShowcaseDirector.command("time set 4000");
		final String spots = System.getProperty("joid.showcase.spots");
		if (spots != null) {
			final String[] list = spots.split(";");
			for (int index = 0; index < list.length; index++) {
				ShowcaseScene.teleport(List.of(list[index].trim().split(" ")).stream().mapToDouble(Double::parseDouble).toArray());
				ShowcaseDirector.waitForWorld();
				ShowcaseDirector.capture("spots/" + index);
			}
			return;
		}

		final String search = System.getProperty("joid.showcase.search");
		if (search != null) {
			final double[] area = List.of(search.split(" ")).stream().mapToDouble(Double::parseDouble).toArray();
			final List<double[]> points = new ArrayList<>();
			for (double x = area[0] - area[2]; x <= area[0] + area[2]; x += 6D) {
				for (double z = area[1] - area[2]; z <= area[1] + area[2]; z += 6D) {
					points.add(new double[] {x, z, ShowcaseScene.ground(x, z)});
				}
			}
			points.sort((first, second) -> Double.compare(second[2], first[2]));
			for (int index = 0; index < Math.min(9, points.size()); index++) {
				final double[] point = points.get(index);
				final double yaw = Math.toDegrees(Math.atan2(-(area[0] - point[0]), area[1] - point[1]));
				System.out.println("[JOID] Showcase search " + index + ": " + point[0] + " " + point[1] + " " + Math.round(yaw) + " 12 (ground " + point[2] + ")");
				ShowcaseScene.teleport(new double[] {point[0], point[1], yaw, 12D});
				ShowcaseDirector.waitForWorld();
				ShowcaseDirector.capture("search/" + index);
			}
			return;
		}

		ShowcaseDirector.waitForWorld();
		final List<double[]> places = ShowcaseDirector.computeOnServer(player -> {
			final ServerLevel level = (ServerLevel) player.level();
			final List<double[]> found = new ArrayList<>();
			final BlockPos origin = player.blockPosition();
			final BlockPos village = level.findNearestMapStructure(StructureTags.VILLAGE, origin, 100, false);
			if (village != null) {
				found.add(new double[] {village.getX(), village.getZ()});
			}

			for (final ResourceKey<Biome> biome : List.of(Biomes.CHERRY_GROVE, Biomes.RIVER, Biomes.FLOWER_FOREST, Biomes.MEADOW)) {
				final Pair<BlockPos, Holder<Biome>> nearest = level.findClosestBiome3d(holder -> holder.is(biome), origin, 6400, 32, 64);
				if (nearest != null) {
					found.add(new double[] {nearest.getFirst().getX(), nearest.getFirst().getZ()});
				}
			}
			return found;
		});
		for (int index = 0; index < places.size(); index++) {
			final double[] place = places.get(index);
			final int height = ShowcaseDirector.computeOnServer(player -> ((ServerLevel) player.level()).getChunk((int) place[0] >> 4, (int) place[1] >> 4).getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) place[0] & 15, (int) place[1] & 15));
			System.out.println("[JOID] Showcase spot " + index + ": " + place[0] + " " + (height + 6) + " " + place[1]);
			for (int yaw = 0; yaw < 360; yaw += 90) {
				ShowcaseDirector.command(String.format(Locale.ROOT, "tp @a %.1f %d %.1f %d 15", place[0], height + 6, place[1], yaw));
				ShowcaseDirector.waitForWorld();
				ShowcaseDirector.capture("explore/" + index + "-" + yaw);
			}
		}
	}

	private static void teleport(final double[] place) {
		ShowcaseDirector.command(String.format(Locale.ROOT, "tp @a %.2f %d %.2f %.1f %.1f", place[0], ShowcaseScene.ground(place[0], place[1]), place[1], place[2], place[3]));
	}

	private static int ground(final double x, final double z) {
		final int blockX = (int) Math.floor(x);
		final int blockZ = (int) Math.floor(z);
		return ShowcaseDirector.computeOnServer(player -> ((ServerLevel) player.level()).getChunk(blockX >> 4, blockZ >> 4).getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockX & 15, blockZ & 15));
	}

	private static ItemStack enchant(final Level level, final ItemStack stack, final ResourceKey<Enchantment> enchantment, final int value) {
		stack.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment), value);
		return stack;
	}

}