package dev.joid.backend.minecraft.demo.test.scenario;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import dev.joid.backend.minecraft.demo.test.ClientTestBounds;
import dev.joid.backend.minecraft.demo.test.ClientTestContext;
import dev.joid.backend.minecraft.demo.test.ClientTestDemos;
import dev.joid.backend.minecraft.demo.test.ClientTestImages;
import dev.joid.backend.minecraft.demo.test.IClientTestPilot;
import dev.joid.backend.minecraft.demo.test.object.ClientTestBlock;
import dev.joid.backend.minecraft.demo.test.object.ClientTestEntity;
import dev.joid.backend.minecraft.demo.test.object.ClientTestItem;
import dev.joid.backend.minecraft.demo.test.object.ClientTestTexture;
import dev.joid.lib.bridge.render.texture.ITexture;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.text.Text;
import dev.joid.lib.font.IFont;
import dev.joid.lib.font.TextInfo;
import dev.joid.lib.resource.Resource;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.impl.resource.ResourceNode;
import dev.joid.lib.ui.node.impl.shape.RectNode;
import dev.joid.lib.ui.node.impl.text.TextNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ObjectScenarios {

	public static void verifyEntitiesWithoutWorld(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		context.waitFor("the title screen", pilot::isTitleScreen, 600);
		final Node profile = context.compute(() -> pilot.createEntityNode(200, 200, 300, 600, ClientTestEntity.PLAYER_PROFILE, -30D, 0D));
		final Node custom = context.compute(() -> pilot.createEntityNode(700, 200, 300, 600, ClientTestEntity.PLAYER_SKIN, 0D, 20D));
		final Node mob = context.compute(() -> pilot.createEntityNode(1200, 200, 300, 600, ClientTestEntity.ZOMBIE, 0D, 0D));
		context.open(ObjectScenarios.ui(profile, custom, mob));
		context.waitTicks(40);
		final List<int[]> entities = context.compute(() -> Arrays.asList(ClientTestBounds.of(profile), ClientTestBounds.of(custom), ClientTestBounds.of(mob)));
		final BufferedImage image = context.capture("joid-entities-title");
		ClientTestImages.expectDrawn(context, "player with a default skin without a world", image, entities.get(0));
		ClientTestImages.expectDrawn(context, "player with a custom skin without a world", image, entities.get(1));
		context.check(!context.test(() -> pilot.hasEntity(mob)), "an entity type was created without a world");
		context.escape();
		context.waitFor("the title screen", pilot::isTitleScreen, 30);
	}

	public static void verifyResources(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		context.openOver(ClientTestDemos.create(ClientTestDemos.MINECRAFT));
		final List<BufferedImage> captures = new ArrayList<>();
		final List<String> changes = new ArrayList<>();
		captures.add(context.capture("joid-demo-minecraft-resources-0"));
		do {
			context.waitTicks(3);
			captures.add(context.capture("joid-demo-minecraft-resources-" + captures.size()));
			changes.clear();
			for (final int x : new int[] {1040, 1240, 1520, 1712}) {
				if (!ObjectScenarios.isChanged(captures, new int[] {x, 736, 128, 128})) {
					changes.add("the animated sprite at " + x + ", 736 did not change between the captures");
				}
			}
		} while (!changes.isEmpty() && captures.size() < 40);

		changes.forEach(context::fail);
		ClientTestImages.expectChange(context, "static sprite diamond", captures, new int[] {560, 720, 64, 64}, true);
		ClientTestImages.expectTexture(context, "sprite of the diamond item", captures.get(0), 560, 720, context.compute(() -> pilot.readTexture(ClientTestTexture.DIAMOND_ITEM)));
		ClientTestImages.expectTexture(context, "decoded diamond block", captures.get(0), 80, 820, context.compute(() -> pilot.readTexture(ClientTestTexture.DIAMOND_BLOCK)));
		context.closeScreens();
	}

	public static void verifyReload(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		final boolean openGl = pilot.getGraphicsBackend().equals("opengl");
		context.openOver(ClientTestDemos.create(ClientTestDemos.MINECRAFT));
		ObjectScenarios.reload(context);
		final ITexture decoded = context.compute(() -> Resource.of(pilot.getTexturePath(ClientTestTexture.ANIMATED_BLOCK)).getTexture());
		final int textures = openGl ? context.compute(pilot::countTextures) : 0;
		ObjectScenarios.reload(context);
		ObjectScenarios.reload(context);
		final int reloaded = openGl ? context.compute(pilot::countTextures) : 0;
		context.check(decoded != null && context.test(() -> pilot.isDeleted(decoded)), "the texture decoded before the reloads was not deleted: " + decoded);
		context.check(reloaded <= textures, "the reloads leaked OpenGL textures, the count went from " + textures + " to " + reloaded);
		final BufferedImage image = context.capture("joid-demo-minecraft-reloaded");
		ClientTestImages.expectTexture(context, "sprite of the diamond item after the reloads", image, 560, 720, context.compute(() -> pilot.readTexture(ClientTestTexture.DIAMOND_ITEM)));
		ClientTestImages.expectTexture(context, "decoded diamond block after the reloads", image, 80, 820, context.compute(() -> pilot.readTexture(ClientTestTexture.DIAMOND_BLOCK)));
		System.out.println("[JOID] " + (openGl ? textures + " OpenGL textures before and after the reloads" : "Texture count skipped outside OpenGL"));
		context.closeScreens();
	}

	public static void verifyItems(final ClientTestContext context) {
		context.openOver(ClientTestDemos.create(ClientTestDemos.MINECRAFT));
		context.scroll(20);
		final List<int[]> items = context.compute(() -> ClientTestBounds.all(ClientTestDemos.MINECRAFT, ClientTestDemos.ITEM_NODE));
		final List<int[]> blocks = context.compute(() -> ClientTestBounds.all(ClientTestDemos.MINECRAFT, ClientTestDemos.BLOCK_NODE));
		final BufferedImage first = context.capture("joid-demo-minecraft-items-a");
		context.waitTicks(7);
		final BufferedImage second = context.capture("joid-demo-minecraft-items-b");
		for (int index = 0; index < items.size(); index++) {
			ClientTestImages.expectDrawn(context, "item " + index, first, items.get(index));
		}

		for (int index = 0; index < blocks.size(); index++) {
			ClientTestImages.expectDrawn(context, "block " + index, first, blocks.get(index));
		}

		ClientTestImages.expectChange(context, "plain apple", Arrays.asList(first, second), items.get(0), true);
		ClientTestImages.expectChange(context, "glint of the enchanted sword", Arrays.asList(first, second), items.get(1), false);
		ClientTestImages.expectChange(context, "rotating furnace", Arrays.asList(first, second), blocks.get(3), false);
		final int[] apple = items.get(5);
		final int[] tooltip = {apple[0] + apple[2] / 2 + 12, apple[1] + apple[3] / 2 - 30, 160, 40};
		context.moveTo(apple);
		context.waitTicks(5);
		final BufferedImage hovered = context.capture("joid-demo-minecraft-tooltip");
		ClientTestImages.expectChange(context, "vanilla tooltip of the golden apple", Arrays.asList(second, hovered), tooltip, false);
		context.moveMouse(960D, 540D);
		context.guiScale(2);
		context.waitTicks(40);
		context.capture("joid-demo-minecraft-items-gui-scale-2");
		context.guiScale(0);
		context.closeScreens();
	}

	public static void verifyItemCooldown(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		context.openOver(ClientTestDemos.create(ClientTestDemos.MINECRAFT));
		context.scroll(20);
		final int[] pearl = context.compute(() -> ClientTestBounds.all(ClientTestDemos.MINECRAFT, ClientTestDemos.ITEM_NODE)).get(4);
		final BufferedImage before = context.capture("joid-demo-minecraft-cooldown-before");
		context.click(pearl);
		context.waitTicks(10);
		final BufferedImage cooldown = context.capture("joid-demo-minecraft-cooldown");
		context.check(context.test(() -> pilot.isOnCooldown(ClientTestItem.ENDER_PEARL)), "clicking the ender pearl did not start its cooldown");
		ClientTestImages.expectChange(context, "cooldown overlay of the ender pearl", Arrays.asList(before, cooldown), pearl, false);
		context.moveMouse(960D, 540D);
		context.closeScreens();
	}

	public static void verifyEntities(final ClientTestContext context) {
		context.openOver(ClientTestDemos.create(ClientTestDemos.MINECRAFT));
		context.scroll(20);
		final List<int[]> entities = context.compute(() -> ClientTestBounds.all(ClientTestDemos.MINECRAFT, ClientTestDemos.ENTITY_NODE));
		final int[] player = entities.get(9);
		final int[] villager = entities.get(10);
		context.moveMouse(player[0] - 200D, player[1] + 20D);
		context.waitTicks(5);
		final BufferedImage left = context.capture("joid-demo-minecraft-entities-a");
		context.moveMouse(villager[0] + villager[2] + 80D, villager[1] + 20D);
		context.waitTicks(5);
		final BufferedImage right = context.capture("joid-demo-minecraft-entities-b");
		context.moveMouse(960D, 540D);
		final List<String> labels = Arrays.asList("zombie", "creeper", "pig", "chicken", "default skin", "custom skin", "local player", "masked local player", "rotated local player", "local player following the mouse", "villager following the mouse");
		context.check(entities.size() == labels.size(), "UIDemoMinecraft shows " + entities.size() + " entities instead of " + labels.size());
		for (int index = 0; index < Math.min(entities.size(), labels.size()); index++) {
			ClientTestImages.expectDrawn(context, labels.get(index), left, entities.get(index));
		}

		ClientTestImages.expectChange(context, "zombie", Arrays.asList(left, right), entities.get(0), true);
		ClientTestImages.expectChange(context, "rotating creeper", Arrays.asList(left, right), entities.get(1), false);
		ClientTestImages.expectChange(context, "player with a default skin", Arrays.asList(left, right), entities.get(4), true);
		ClientTestImages.expectChange(context, "head of the player following the mouse", Arrays.asList(left, right), new int[] {player[0], player[1], player[2], player[3] / 3}, false);
		ClientTestImages.expectChange(context, "head of the villager following the mouse", Arrays.asList(left, right), new int[] {villager[0], villager[1], villager[2], villager[3] / 3}, false);
		context.guiScale(2);
		context.waitTicks(40);
		context.capture("joid-demo-minecraft-entities-gui-scale-2");
		context.guiScale(0);
		context.closeScreens();
	}

	public static void verifySound(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		context.openOver(ClientTestDemos.create(ClientTestDemos.MINECRAFT));
		context.scroll(60);
		final List<int[]> resources = context.compute(() -> ClientTestBounds.all(ClientTestDemos.MINECRAFT, ResourceNode.class.getName()));
		context.run(pilot::startSoundCapture);
		context.click(resources.get(resources.size() - 1));
		context.waitTicks(5);
		context.capture("joid-demo-minecraft-sound");
		context.moveMouse(960D, 540D);
		final List<String> sounds = context.compute(pilot::stopSoundCapture);
		final String expected = context.compute(pilot::getNoteBlockSound);
		context.check(sounds.equals(Collections.singletonList(expected)), "clicking the note block played " + sounds + " instead of " + expected + " alone");
		context.closeScreens();
	}

	public static void verifyGameObjects(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		final IFont font = (IFont) ClientTestDemos.constant(ClientTestDemos.FONT, "DEFAULT");
		final List<Node> nodes = context.compute(() -> Arrays.asList(
				pilot.createItemNode(100, 100, 160, ClientTestItem.DIAMOND_SWORD, false),
				pilot.createBlockNode(400, 100, 160, ClientTestBlock.GRASS),
				pilot.createEntityNode(700, 60, 160, 240, ClientTestEntity.PIG, 0D, 0D),
				pilot.createEntityNode(1000, 60, 160, 240, ClientTestEntity.PLAYER_SKIN, 0D, 0D),
				TextNode.create(100, 400).text(Text.create("JOID", TextInfo.create(font, 64F).color(Color.WHITE))),
				ResourceNode.create(400, 400, 128, 128).resource(pilot.createSprite(ClientTestItem.DIAMOND)),
				ResourceNode.create(700, 400, 128, 128).resource(Resource.of(pilot.getTexturePath(ClientTestTexture.ANIMATED_BLOCK)))));
		final RectNode background = RectNode.create(0, 0, 1920, 1080).color(Color.rgb(0x777777));
		context.open(ObjectScenarios.ui(background));
		context.run(() -> nodes.forEach(node -> node.attach(background)));
		context.waitTicks(40);
		final List<int[]> bounds = context.compute(() -> nodes.stream().map(ClientTestBounds::of).collect(Collectors.toList()));
		final BufferedImage image = context.capture("joid-game-objects");
		final List<String> labels = Arrays.asList("item", "block", "entity", "player skin", "Minecraft text", "sprite", "animated texture");
		for (int index = 0; index < labels.size(); index++) {
			final int drawn = ClientTestImages.countDifferent(image, bounds.get(index), 0x777777);
			context.check(drawn * 20 >= bounds.get(index)[2] * bounds.get(index)[3], "the " + labels.get(index) + " covers " + drawn + " of its " + bounds.get(index)[2] * bounds.get(index)[3] + " pixels at " + Arrays.toString(bounds.get(index)));
		}
		context.closeScreens();
	}

	public static void verifyBlockLighting(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		pilot.giveItem(0, ClientTestItem.SNOW_BLOCK, 1);
		context.waitTicks(10);
		final int[] slot = context.compute(() -> pilot.getHotbarSlotBounds(0));
		final int[] vanilla = ClientTestImages.faces(context.capture("joid-lighting-vanilla"), slot);
		final Node item = context.compute(() -> pilot.createItemNode(200, 200, 256, ClientTestItem.SNOW_BLOCK, false));
		final Node block = context.compute(() -> pilot.createBlockNode(600, 200, 256, ClientTestBlock.SNOW_BLOCK));
		final Node furnace = context.compute(() -> pilot.createItemNode(1000, 200, 256, ClientTestItem.FURNACE, false));
		context.open(ObjectScenarios.ui(item, block, furnace));
		final List<int[]> nodes = context.compute(() -> Arrays.asList(ClientTestBounds.of(item), ClientTestBounds.of(block)));
		final BufferedImage image = context.capture("joid-lighting");
		context.check(vanilla[0] > vanilla[1] && vanilla[1] > vanilla[2], "the vanilla hotbar block " + Arrays.toString(vanilla) + " is not lit top, left then right");
		final List<String> labels = Arrays.asList("block item", "block");
		for (int index = 0; index < nodes.size(); index++) {
			final int[] faces = ClientTestImages.faces(image, nodes.get(index));
			for (int face = 0; face < 3; face++) {
				if (Math.abs(faces[face] - vanilla[face]) > 4) {
					context.fail("the faces of the " + labels.get(index) + " " + Arrays.toString(faces) + " differ from the vanilla hotbar block " + Arrays.toString(vanilla));
					break;
				}
			}
		}
		context.closeScreens();
	}

	public static void verifyEntityBounds(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		for (final double yaw : new double[] {0D, 45D, 90D}) {
			final RectNode panel = RectNode.create(560, 340, 800, 400).color(Color.rgb(0x202020));
			final Node entity = context.compute(() -> pilot.createEntityNode(300, 100, 200, 200, ClientTestEntity.PIG, yaw, 0D));
			context.open(ObjectScenarios.ui(panel));
			context.run(() -> entity.attach(panel));
			context.waitTicks(10);
			final int[] node = context.compute(() -> ClientTestBounds.of(entity));
			final int[] area = context.compute(() -> ClientTestBounds.of(panel));
			final BufferedImage image = context.capture("joid-entity-bounds-" + (int) yaw);
			int drawn = 0;
			int outside = 0;
			for (int y = area[1] + 2; y < area[1] + area[3] - 2; y++) {
				for (int x = area[0] + 2; x < area[0] + area[2] - 2; x++) {
					final boolean inside = x >= node[0] - 1 && x <= node[0] + node[2] && y >= node[1] - 1 && y <= node[1] + node[3];
					if (ClientTestImages.delta(0x202020, image.getRGB(x, y) & 0xFFFFFF) > 8) {
						if (inside) {
							drawn++;
						} else {
							outside++;
						}
					}
				}
			}

			context.check(drawn >= 1000 && outside == 0, "the pig turned by " + yaw + " draws " + drawn + " pixels in its node " + Arrays.toString(node) + " and " + outside + " outside");
			context.closeScreens();
		}
	}

	public static UI ui(final Node... nodes) {
		return new UI() {

			@Override
			public void init() {
				for (final Node node : nodes) {
					node.attach(this);
				}
			}

		};
	}

	private static boolean isChanged(final List<BufferedImage> captures, final int[] bounds) {
		for (int capture = 1; capture < captures.size(); capture++) {
			for (int row = 0; row < bounds[3]; row++) {
				for (int column = 0; column < bounds[2]; column++) {
					if (captures.get(capture - 1).getRGB(bounds[0] + column, bounds[1] + row) != captures.get(capture).getRGB(bounds[0] + column, bounds[1] + row)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static void reload(final ClientTestContext context) {
		final IClientTestPilot pilot = context.getPilot();
		final int reloads = context.compute(pilot::countReloads);
		context.run(pilot::reloadResources);
		context.waitFor("the end of the resource reload", () -> pilot.countReloads() > reloads && !pilot.isReloading(), 2400);
		context.waitTicks(40);
	}

}