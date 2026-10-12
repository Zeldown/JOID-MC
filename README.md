<div align="center">

# JOID-MC
## JOID interfaces inside Minecraft

<div align="center">
  <img align="center" src="https://img.shields.io/badge/minecraft-26.2-brightgreen">
  <img align="center" src="https://img.shields.io/badge/joid-8.0.0-blue">
  <img align="center" src="https://img.shields.io/badge/loader-Fabric%20%C2%B7%20NeoForge%20%C2%B7%20Forge-orange">
  <img align="center" src="https://img.shields.io/badge/license-Apache--2.0-blue">
  <img align="center" src="https://github.com/Zeldown/JOID-MC/actions/workflows/build.yml/badge.svg?branch=version/26.2">
</div>

<br>

**Beautiful, reactive interfaces for Minecraft. One UI, every version and every loader.**

[**CurseForge**](https://www.curseforge.com/minecraft/mc-mods/joid-mc) · [**Modrinth**](https://modrinth.com/project/joid-mc) · [**JOID Documentation**](https://joid.dev-zeldown.workers.dev/) · [**Releases**](https://github.com/Zeldown/JOID-MC/releases)

</div>

<br>

JOID-MC brings [JOID](https://github.com/Zeldown/JOID), the Java UI engine, inside Minecraft, and adds everything a Minecraft interface needs: items, blocks and entities in 3D, the Minecraft font, inventories and the HUD.

## Why JOID-MC

- **One UI, every version and loader.** The code of your interface never changes: the same UI runs on Fabric, NeoForge and Forge, and on every Minecraft version JOID-MC supports.
- **Your mockup, at any size.** Lay out your UI on a fixed design canvas, copy positions, sizes and colors straight from your mockup, and JOID scales it to any window and any GUI scale.
- **Reactive to your data.** Show your data and forget about it: when a value changes, everything displaying it on screen updates by itself.
- **Your look, not Minecraft's.** Components handle behavior and input. You draw them in your own UI kit, and another kit gives the same code a whole new design.
- **Looks great at any size.** Text and shapes stay razor-sharp at any size and any resolution.
- **Minecraft built in.** Items, blocks and entities in 3D with the game's lighting, the Minecraft fonts and `§` colors, inventories with real slots, and a HUD you can replace layer by layer.
- **Everything included.** Images, SVG, animated GIF and WebP, video, 3D models, shadows, blur, masks, shaders, animations, and whatever else you can imagine.

## Copy the design

Draw your screen in your design tool, copy each layer's values, and get the same result in the game.

<p align="center">
  <img src="https://raw.githubusercontent.com/Zeldown/JOID-MC/version/26.2/docs/images/copy-the-design.webp" alt="A quest panel in its mockup, then the same panel in the Minecraft window, identical" width="100%">
</p>

## Showcase

Real renders inside the game window, styled for this showcase: JOID ships with no design of its own. Watch them in the [1080p video](https://raw.githubusercontent.com/Zeldown/JOID-MC/version/26.2/docs/images/showcase.mp4).

<table>
  <tr>
    <td width="50%"><img src="https://raw.githubusercontent.com/Zeldown/JOID-MC/version/26.2/docs/images/showcase-hud.webp" alt="Health, hunger and experience replaced by an animated JOID HUD, in game"></td>
    <td width="50%"><img src="https://raw.githubusercontent.com/Zeldown/JOID-MC/version/26.2/docs/images/showcase-chest.webp" alt="A chest redrawn with JOID over the world: real slots, a rotating player, items dragged with the mouse"></td>
  </tr>
  <tr>
    <td align="center"><sub><b>HUD.</b> Replace any layer, in game.</sub></td>
    <td align="center"><sub><b>Containers.</b> Real slots, synced with the server.</sub></td>
  </tr>
  <tr>
    <td width="50%"><img src="https://raw.githubusercontent.com/Zeldown/JOID-MC/version/26.2/docs/images/showcase-bestiary.webp" alt="A bestiary of cards with mobs, blocks and items turning in 3D"></td>
    <td width="50%"><img src="https://raw.githubusercontent.com/Zeldown/JOID-MC/version/26.2/docs/images/showcase-pause.webp" alt="A redesigned pause menu over the blurred world, with the player in 3D"></td>
  </tr>
  <tr>
    <td align="center"><sub><b>Bestiary.</b> Mobs, blocks and items in 3D.</sub></td>
    <td align="center"><sub><b>Menus &amp; screens.</b> Redesign any vanilla menu, or build your own.</sub></td>
  </tr>
</table>

---

# Documentation

JOID-MC is a JOID backend: everything in the [JOID documentation](https://joid.dev-zeldown.workers.dev/) applies as is. This part only covers what Minecraft adds.

## Installation

Each release has two jars per loader, built from the same code:

| Jar | Contains | For |
|---|---|---|
| `-dev` | JOID-MC and JOID, plus the dev mode (inspector, hot reload, warnings that point at your line) and the JOID and Minecraft demos on the `J` key. | Your development environment only. |
| `-prod` | JOID-MC and JOID, without any dev tool, demo or demo asset. | Players. This is the jar on CurseForge and Modrinth. |

The dev mode and the demos turn on by themselves with the `-dev` jar: there is nothing to configure.

### In your workspace

Download the `-dev` jar of your loader from the [releases](https://github.com/Zeldown/JOID-MC/releases), put it in the `libs/` folder of your mod and add it to your dependencies:

```gradle
dependencies {
    implementation(files("libs/joid-backend-minecraft-fabric-26.2-1.0.0-dev.jar"))
}
```

`runClient` then starts the game with JOID-MC in dev mode.

### Shipping your mod

JOID-MC is a separate mod that players install next to yours, like any library mod.

1. **Do not bundle it** in your jar: no `include`, `jarJar` or shadow.
2. **Declare it as required** in your mod metadata, with the mod id `joid`:

```json
"depends": {
    "joid": ">=1.0.0"
}
```

```toml
[[dependencies.yourmod]]
modId = "joid"
type = "required"
versionRange = "[1.0.0,)"
```

The first block goes in `fabric.mod.json`. The second goes in `neoforge.mods.toml`; in the `mods.toml` of Forge, write `mandatory = true` instead of `type = "required"`.

3. **Mark JOID-MC as a required dependency** on CurseForge and Modrinth, so that launchers install it with your mod.
4. **Use only what the `-prod` jar contains.** The demo classes and fonts (`DemoFont`) and the dev nodes exist only in `-dev`. Before publishing, swap the jar in `libs/` for the `-prod` one and start the game once.

## Getting started

JOID-MC starts by itself with the game: there is nothing to register. Write a UI and open it from the client:

```java
public class WelcomeUI extends UI {

	@Override
	public void init() {
		RectNode.create(660, 390, 600, 300).color(Color.decode("#1E2230")).attach(this);
	}

}
```

```java
JOID.open(new WelcomeUI());
```

The UI is laid out on JOID's design canvas (1920×1080 by default) and scaled to the window. In the development environment, the `J` key opens the JOID and Minecraft demos. To learn UIs, nodes, layout and signals, start with the [JOID Quick Start](https://joid.dev-zeldown.workers.dev/#/getting-started/quick-start).

## Screens

A UI opened with `JOID.open` becomes a Minecraft screen. `@UIDataMinecraft` sets how it behaves in the game:

```java
@UIDataMinecraft(title = "Welcome", pause = false, background = false)
public class WelcomeUI extends UI {

}
```

| Option | Default | Effect |
|---|---|---|
| `title` | `""` | Title of the screen, read by the narrator. |
| `pause` | `true` | Pauses a singleplayer game while the screen is open. |
| `background` | `true` | Draws the Minecraft menu background behind the UI, screens and containers alike: the blurred world in game, the panorama on the title screen. `false` draws no Minecraft background. |

Opening, closing and configuring UIs work as in [UIs](https://joid.dev-zeldown.workers.dev/#/concepts/uis).

## Containers

A container UI shows a Minecraft menu (`AbstractContainerMenu`) with real, synchronized slots. Extend `ContainerUI` and place a `SlotNode` for each slot:

```java
public class BackpackUI extends ContainerUI<BackpackMenu> {

	public BackpackUI(final BackpackMenu menu) {
		super(menu);
	}

	@Override
	public void init() {
		final GridNode grid = GridNode.create(660, 400, 612, 0).margin(8D).attach(this);

		for (final Slot slot : super.getContainer().slots) {
			SlotNode.create(0, 0, 60, 60).slot(slot).attach(grid);
		}

		super.bounds(grid);
	}

}
```

Register its screen where your loader registers menu screens, and open the menu as usual from the server:

```java
MenuScreens.register(BackpackMenu.TYPE, ContainerUI.screen(BackpackUI::new));
```

```java
event.register(BackpackMenu.TYPE, ContainerUI.screen(BackpackUI::new));
```

The first line is for Fabric and Forge (in `FMLClientSetupEvent`), the second for the `RegisterMenuScreensEvent` of NeoForge. `ContainerUI.screen(...)` is the only way to get the screen of a container UI. Your mod keeps its own menu types, registries and opening logic. `bounds(...)` tells which area belongs to the panel: a click outside drops the carried item, like in vanilla. Lay out the slots like any node ([Layout](https://joid.dev-zeldown.workers.dev/#/concepts/layout)); clicks, drags, shift-clicks and number keys on slots behave like vanilla, and the rest of the input works as in [Input](https://joid.dev-zeldown.workers.dev/#/concepts/input).

## HUD overlays

An overlay UI is drawn over the game. `@UIDataOverlayLayer` places it on a layer of the Minecraft HUD, and can hide the vanilla layer:

```java
@UIDataOverlay(active = true)
@UIDataOverlayLayer(layer = OverlayLayer.HEALTH, cancel = true)
public class HealthBarUI extends UI {

	@Override
	public void init() {
		RectNode.create(600, 960, 320, 24).color(Color.decode("#E5484D")).attach(this);
	}

}
```

```java
JOID.open(new HealthBarUI());
```

| Option | Effect |
|---|---|
| `layer` | The HUD layer: `HOTBAR`, `HEALTH`, `FOOD`, `EXPERIENCE_LEVEL`, `CROSSHAIR`, `CHAT`, `BOSS_BAR`… |
| `post` | Draws after the vanilla layer instead of before. |
| `cancel` | Hides the vanilla layer. |

A layer overlay follows the vanilla layer it replaces: it stays in the HUD under an open screen, blurred like the rest of the game, and keeps hiding the vanilla layer. Everything else about overlays (interaction, screens, F1) is in [Overlays with @UIDataOverlay](https://joid.dev-zeldown.workers.dev/#/concepts/uis?id=overlays-with-uidataoverlay).

## Minecraft components

| Node | Shows |
|---|---|
| `ItemNode` | An item stack, with its count, durability bar, cooldown, glint and tooltip. |
| `BlockNode` | A block in 3D, with its rotation. |
| `EntityNode` | An entity or a player skin in 3D, which can follow the mouse. |
| `SlotNode` | A slot of a container, with its item, hover and drag. |

```java
ItemNode.create(100, 100, 64, 64).stack(new ItemStack(Items.DIAMOND_SWORD)).tooltip(true).attach(this);

BlockNode.create(200, 100, 64, 64).block(Blocks.GRASS_BLOCK.defaultBlockState()).rotationYaw(30D).attach(this);

EntityNode.create(300, 60, 120, 200).type(EntityTypes.FOX).followMouse(true).attach(this);
```

Items, blocks and entities are lit like in the vanilla inventory. Every setter also takes a signal or a lambda, like any JOID node ([Signals and State](https://joid.dev-zeldown.workers.dev/#/concepts/state)).

## Text

The Minecraft fonts are JOID fonts: use them in any `TextInfo`, and mix them with your own fonts.

```java
final TextInfo info = TextInfo.create(MinecraftFont.DEFAULT, MinecraftFont.SIZE * 3, Color.WHITE);

TextNode.create(100, 100).text(Text.create("§6Gold §lbold §kmagic", info)).attach(this);

TextNode.create(100, 160).text(Text.create(Component.translatable("menu.game"), info)).attach(this);
```

- `§` color and format codes, obfuscated text included.
- A chat `Component`, with its colors, styles and translations.
- The other game fonts: `MinecraftFont.ALT` (enchanting table), `ILLAGER`, `UNIFORM`.
- Everything else from [Text and Fonts](https://joid.dev-zeldown.workers.dev/#/concepts/text): markup, effects, alignment, wrapping.

## Textures and resources

Any texture of the game or of a resource pack is a JOID resource:

```java
ResourceNode.create(100, 100, 64, 64).resource(Resource.of("minecraft:textures/block/diamond_block.png")).attach(this);

ResourceNode.create(200, 100, 64, 64).resource(Resource.of(Sheets.ITEMS_MAPPER.defaultNamespaceApply("diamond"))).attach(this);
```

`namespace:path` locations, atlas sprites and `.mcmeta` animations work out of the box, and follow resource pack reloads. See [Images and Media](https://joid.dev-zeldown.workers.dev/#/concepts/media).

## Custom drawing

In a custom node, `DrawItem`, `DrawBlock` and `DrawEntity` draw Minecraft objects anywhere:

```java
@Override
public void draw(final double mouseX, final double mouseY) {
	DrawItem.inst().drawItem(super.getX(), super.getY(), 64D, this.stack);
	DrawBlock.inst().drawBlock(super.getX() + 80D, super.getY(), 64D, Blocks.CHEST.defaultBlockState());
	DrawEntity.inst().drawEntity(super.getX() + 160D, super.getY(), 64D, 128D, Minecraft.getInstance().player);
}
```

See [Custom Nodes](https://joid.dev-zeldown.workers.dev/#/nodes/custom-nodes) and [Drawing](https://joid.dev-zeldown.workers.dev/#/drawing/drawing).

## License

JOID-MC is released under the [Apache 2.0 license](LICENSE).
