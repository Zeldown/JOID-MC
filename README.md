<div align="center">

<img src="https://raw.githubusercontent.com/Zeldown/JOID-MC/version/1.7.10/docs/images/logo.png" width="128" alt="JOID logo">

# JOID-MC
## JOID interfaces inside Minecraft

<div align="center">
  <img align="center" src="https://img.shields.io/badge/minecraft-1.7.10-brightgreen">
  <img align="center" src="https://img.shields.io/badge/joid-8.0.0-blue">
  <img align="center" src="https://img.shields.io/badge/loader-Forge-orange">
  <img align="center" src="https://img.shields.io/badge/license-Apache--2.0-blue">
  <img align="center" src="https://github.com/Zeldown/JOID-MC/actions/workflows/build.yml/badge.svg?branch=version/1.7.10">
  <a href="https://discord.joid.dev"><img align="center" src="https://img.shields.io/badge/discord-join-5865F2"></a>
</div>

<br>

**Beautiful, reactive interfaces for Minecraft. One UI, every version and every loader.**

[**CurseForge**](https://www.curseforge.com/minecraft/mc-mods/joid-mc) · [**Modrinth**](https://modrinth.com/project/joid-mc) · [**JOID Documentation**](https://documentation.joid.dev/) · [**Releases**](https://github.com/Zeldown/JOID-MC/releases) · [**Discord**](https://discord.joid.dev)

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
  <img src="https://raw.githubusercontent.com/Zeldown/JOID-MC/version/1.7.10/docs/images/copy-the-design.webp" alt="A quest panel in its mockup, then the same panel in the Minecraft window, identical" width="100%">
</p>

## Showcase

Real renders inside the game window, styled for this showcase: JOID ships with no design of its own. Watch them in the [1080p video](https://raw.githubusercontent.com/Zeldown/JOID-MC/version/1.7.10/docs/images/showcase.mp4).

<table>
  <tr>
    <td width="50%"><img src="https://raw.githubusercontent.com/Zeldown/JOID-MC/version/1.7.10/docs/images/showcase-hud.webp" alt="Health, hunger and experience replaced by an animated JOID HUD, in game"></td>
    <td width="50%"><img src="https://raw.githubusercontent.com/Zeldown/JOID-MC/version/1.7.10/docs/images/showcase-chest.webp" alt="A chest redrawn with JOID over the world: real slots, a rotating player, items dragged with the mouse"></td>
  </tr>
  <tr>
    <td align="center"><sub><b>HUD.</b> Replace any layer, in game.</sub></td>
    <td align="center"><sub><b>Containers.</b> Real slots, synced with the server.</sub></td>
  </tr>
  <tr>
    <td width="50%"><img src="https://raw.githubusercontent.com/Zeldown/JOID-MC/version/1.7.10/docs/images/showcase-bestiary.webp" alt="Cards of mobs, blocks and items turning in 3D, with a detail panel"></td>
    <td width="50%"><img src="https://raw.githubusercontent.com/Zeldown/JOID-MC/version/1.7.10/docs/images/showcase-fonts.webp" alt="The Minecraft font from 8 to 72 px, chat colors and formats, the enchanting table alphabet decoded, then a 6x zoom that stays pixel-sharp"></td>
  </tr>
  <tr>
    <td align="center"><sub><b>Game objects.</b> Entities, blocks and items, rendered in 3D.</sub></td>
    <td align="center"><sub><b>Fonts.</b> The Minecraft font, sharp at any size, with § colors.</sub></td>
  </tr>
  <tr>
    <td width="50%"><img src="https://raw.githubusercontent.com/Zeldown/JOID-MC/version/1.7.10/docs/images/showcase-anvil.webp" alt="A redesigned anvil: a word of the sword name is selected with a double click and retyped, the preview and the cost follow, then the renamed sword is taken"></td>
    <td width="50%"><img src="https://raw.githubusercontent.com/Zeldown/JOID-MC/version/1.7.10/docs/images/showcase-servers.webp" alt="A server list with colored messages of the day: a server is dragged to the top, then the list scrolls"></td>
  </tr>
  <tr>
    <td align="center"><sub><b>Forms.</b> Type, check and preview, in a real anvil.</sub></td>
    <td align="center"><sub><b>Lists.</b> Drag to reorder, scroll smoothly.</sub></td>
  </tr>
  <tr>
    <td width="50%"><img src="https://raw.githubusercontent.com/Zeldown/JOID-MC/version/1.7.10/docs/images/showcase-daylight.webp" alt="A daylight panel over the live world: the sun dial, the clock, the sky, the moon phase and the world follow one time value, first as the game advances it, then as the slider is dragged"></td>
    <td width="50%"><img src="https://raw.githubusercontent.com/Zeldown/JOID-MC/version/1.7.10/docs/images/showcase-video.webp" alt="A video with sound inside the game window, paused and resumed with its own controls, then previewed frame by frame while the pointer hovers the timeline"></td>
  </tr>
  <tr>
    <td align="center"><sub><b>Reactive.</b> Bind once, never refresh.</sub></td>
    <td align="center"><sub><b>Video.</b> Play any video, right in the game.</sub></td>
  </tr>
</table>

---

# Documentation

JOID-MC is a JOID backend: everything in the [JOID documentation](https://documentation.joid.dev/) applies as is. This part only covers what Minecraft adds.

## Installation

Each release has three jars per loader, built from the same code:

| Jar | Contains | For |
|---|---|---|
| `dev` | JOID-MC and JOID, with the dev mode of JOID ([Development](#development)) and the MCP names of a ForgeGradle workspace. | Your development environment only. |
| `prod` | JOID-MC and JOID, without the dev mode, reobfuscated for the game. | Players. This is the jar on CurseForge and Modrinth. |
| `sources` | The sources of JOID-MC, without JOID. | Your IDE: attach it to the `dev` jar. |

### In your workspace

Download the `dev` jar of your loader from the [releases](https://github.com/Zeldown/JOID-MC/releases), put it in the `libs/` folder of your mod and add it to the dependencies of your `build.gradle`:

**Forge**

```gradle
dependencies {
    compile files("libs/joid-backend-minecraft-forge-1.7.10-1.0.0-dev.jar")
}
```

`runClient` then starts the game with JOID-MC in dev mode. The `dev` jar keeps the names of a ForgeGradle workspace: only the `prod` jar runs in the `mods` folder of a game.

### Shipping your mod

JOID-MC is a separate mod that players install next to yours, like any library mod. It runs on the client only: a server does not need it, and a client with JOID-MC joins a server without it.

1. **Do not bundle it** in your jar: no shadow, no copy of its classes.
2. **Declare it as required on the client** in the metadata of your mod, with the mod id `joid`:

**Forge** · `@Mod` and `mcmod.info`

```java
@Mod(modid = "yourmod", dependencies = "required-after:joid@[1.0.0,)")
public class YourMod {

}
```

```json
"requiredMods": ["joid"],
"dependencies": ["joid"]
```

Forge 1.7.10 has no client-side dependency: use `required-after` when your mod runs on the client only. A mod that also runs on servers declares `after:joid`, since a `required-after` stops a dedicated server that does not have JOID-MC, and calls JOID-MC from its client proxy only.

3. **Mark JOID-MC as a required dependency** on CurseForge and Modrinth, so that launchers install it with your mod.

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

The UI is laid out on JOID's design canvas (1920×1080 by default) and scaled to the window. To learn UIs, nodes, layout and signals, start with the [JOID Quick Start](https://documentation.joid.dev/use/get-started/quick-start/).

JOID-MC needs OpenGL 2.1: on an older driver it stays off, opens no UI and says why in the console with a `[JOID]` line. Its masks and scroll areas clip with the stencil of the screen, which JOID-MC turns on at the first tick; with the framebuffers of the game turned off in the video settings, the masks do not clip and the console says so.

## Development

The `dev` jar of [Installation](#installation) starts JOID in dev mode by itself, there is nothing to call. With a JOID UI open:

| Dev mode | |
|---|---|
| Dev panel | `F3` shows or hides the dev panel of the UI on top: Inspect outlines the node under the pointer and lists the bounds, listeners, hierarchy and fields of the clicked node, Grid measures the canvas from the pointer. |
| Reload | `Ctrl` + `R` or `F5` reloads the UI (its `init()` runs again), `Shift` on top replaces it with a new instance of its class. |
| Hot reload | A UI reloads itself when the folder or the jar of its classes changes, for example after a recompilation from your IDE while the game runs. |
| Zoom | `Ctrl` or `Alt` with `=` or `-` zooms the UI by steps, `Alt` + scroll by small steps, `Shift` on top for larger ones. |
| Profiler | The console logs how long each UI takes to load and every frame of a UI that takes longer than 16.66 ms. |
| Warnings | `[JOID]` lines in the console point at what is wrong: a container UI without `bounds(...)`, a text that a signal cannot follow, a missing font weight, a shader that breaks a rule of JOID GLSL or that the driver refuses. |
| Missing textures | A resource that fails to load shows the missing texture instead of nothing. |

`@UIDataDebug(profiler = false, hotreload = false)` on a UI turns its profiler or its hot reload off. The keys go to JOID first: Minecraft does not see an `F3` or an `F5` that JOID used.

None of this is in the `prod` jar: no dev panel, no dev font, no profiler, most warnings stay silent, and the dev mode cannot be turned on. Start the game once with the `prod` jar before publishing. See [Dev tools](https://documentation.joid.dev/use/get-started/dev-tools/).

## Screens

A UI opened with `JOID.open` becomes a Minecraft screen. `@UIDataMinecraft` sets how it behaves in the game:

```java
@UIDataMinecraft(pause = false, background = false)
public class WelcomeUI extends UI {

}
```

| Option | Default | Effect |
|---|---|---|
| `pause` | `true` | Pauses a singleplayer game while the screen is open. The paused game also holds the payloads sent to its server until it resumes: a screen that sends one sets `pause = false`. Container UIs never pause. |
| `background` | `true` | Draws the Minecraft menu background behind the UI, screens and containers alike: the darkened world in game, the dirt on the title screen. `false` draws no Minecraft background. |

A screen of Minecraft 1.7.10 has no title, so `@UIDataMinecraft` has none. The Minecraft background and the UI backdrop of JOID (`@UIData(background)`, default `false`, see [UIs](https://documentation.joid.dev/use/learn/describing-the-ui/uis/)) are independent: add `@UIData(background = true)` to draw the backdrop as well.

- `JOID.open(ui)` opens the screen, `JOID.close(ui)` closes it, and `Escape` closes the top UI unless it is `@UIData(closeable = false)`. Several UIs open at once share one screen, which closes with the last of them.
- When the game or another mod opens its own screen, the JOID UIs of the screen close.
- Over an open container, `JOID.open` opens the UI above it: the container stays open underneath.
- UIs follow the GUI Scale option: at `Auto` they are at full size, at half that scale they are at half size. `@UIDataScale` caps or ignores it ([The design canvas](https://documentation.joid.dev/use/learn/describing-the-ui/design-canvas/)).

Everything else about UIs works as in [UIs](https://documentation.joid.dev/use/learn/describing-the-ui/uis/).

## Containers

A container UI shows a Minecraft container (`Container`) with real, synchronized slots. Extend `ContainerUI` and place a `SlotNode` for each slot:

```java
public class BackpackUI extends ContainerUI<BackpackContainer> {

	public BackpackUI(final BackpackContainer container) {
		super(container);
	}

	@Override
	public void init() {
		final GridNode grid = GridNode.create(660, 400, 612, 0).margin(8D).attach(this);

		for (final Object slot : super.getContainer().inventorySlots) {
			SlotNode.create(0, 0, 60, 60).slot((Slot) slot).attach(grid);
		}

		super.bounds(grid);
	}

}
```

Return its screen with `ContainerUI.screen(...)` where your mod returns its container screens:

**Forge** · in the `IGuiHandler` of your mod

```java
@Override
public Object getClientGuiElement(final int id, final EntityPlayer player, final World world, final int x, final int y, final int z) {
	return ContainerUI.screen(BackpackUI::new).apply(new BackpackContainer(player));
}
```

`ContainerUI.screen(...)` is the only way to get the screen of a container UI. Your mod keeps its own containers, `IGuiHandler` and opening logic: open the container from the server as usual (`player.openGui(...)`), never with `JOID.open`, which throws for a container UI. `Escape` and the inventory key close it like a vanilla container.

### ContainerUI

| Member | Effect |
|---|---|
| `ContainerUI(container)` | The constructor takes the container that the screen shows. |
| `getContainer()` | The container: its `inventorySlots`, `getSlot(index)` and your own fields. |
| `getCarried()` | The stack held by the mouse, `null` when there is none. |
| `bounds(node)` | The panel is this node, wherever it moves. |
| `bounds(x, y, width, height)` | The panel is this fixed area, in canvas units. |
| `getBounds()` | The panel used right now: `{x, y, width, height}`. |
| `getCarriedNode()` | The node that draws the held stack, above every other node. |
| `ContainerUI.screen(factory)` | The function that turns a container into the screen of its UI, for `getClientGuiElement`. |

The panel is the area of the container: a click outside it drops the held stack, a click inside it on no slot does nothing, like in vanilla. Without `bounds(...)`, the panel is the box around all visible nodes; when that box covers the whole window (a full-screen background, for example), no click can fall outside, and the dev mode warns about it in the console.

The lib draws the held stack under the mouse, with its count and durability bar, at the item size of the last hovered slot. During a drag, it shows what is left to place.

### SlotNode

A `SlotNode` shows and handles one slot of the container:

```java
SlotNode.create(0, 0, 72, 72).slot(super.getContainer().getSlot(0)).color(Color.decode("#8B8B8B")).hoveredColor(Color.decode("#FFFFFF60")).attach(this);
```

| Method | Default | Effect |
|---|---|---|
| `slot(Slot)` | none | Required. The slot it shows and handles. While the slot cannot be hovered, the node only draws its `color`. |
| `color(Color)` | `Color.TRANSPARENT` | Background of the node. |
| `hoveredColor(Color)` | `#FFFFFF80` | Drawn over the node while the slot is hovered. `Color.TRANSPARENT` turns it off. |
| `getItemSize()` | | Size of the item: 16/18 of the smallest side of the node, like a 16 px item in an 18 px vanilla slot. |

The slot draws its item centered with its count and durability bar, the empty-slot icon of armor slots, the preview of a drag (the count turns yellow when the slot is full) and the vanilla tooltip of the hovered item. Every vanilla slot action works: click, right-click, shift-click, double-click, drag to split, number keys and `Q` to drop. Shift-click moves stacks with the `transferStackInSlot` of your container.

The `xDisplayPosition` and `yDisplayPosition` of a `Slot` are not used: the `SlotNode` places it. The player inventory comes after your own slots in your container: the 27 slots of the inventory, then the 9 of the hotbar. For a container with 27 slots of its own:

```java
for (int index = 0; index < 27; index++) {
	SlotNode.create(48 + index % 9 * 72, 384 + index / 9 * 72, 68, 68).slot(super.getContainer().getSlot(27 + index)).attach(panel);
}

for (int index = 0; index < 9; index++) {
	SlotNode.create(48 + index * 72, 624, 68, 68).slot(super.getContainer().getSlot(54 + index)).attach(panel);
}
```

Lay out the slots like any node ([Layout](https://documentation.joid.dev/use/learn/describing-the-ui/layout/)); the rest of the input works as in [Events](https://documentation.joid.dev/use/learn/interactivity/events/).

## HUD overlays

An overlay UI is drawn over the game. `@UIDataOverlayLayer` places it on a layer of the Minecraft HUD, and can hide the vanilla layer:

```java
@UIData(anchorY = Align.END)
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

`@UIDataOverlay(active = true)` makes the UI an overlay: without it, `JOID.open` opens a screen. Its other options are in [UIs](https://documentation.joid.dev/use/learn/describing-the-ui/uis/); in game, the mouse is free only while a screen is open, so an overlay that takes clicks needs `interaction` and `render.aboveScreens`. An overlay cannot take the keyboard in game without a screen: open a JOID screen for that. `anchorY = Align.END` keeps the bottom of the canvas on the bottom of the window, next to the vanilla bars, whatever the window ratio.

### @UIDataOverlayLayer

| Option | Default | Effect |
|---|---|---|
| `layer` | required | The vanilla HUD layer the overlay is drawn with. |
| `post` | `false` | `false` draws the overlay just before the vanilla layer, under it; `true` just after, over it. |
| `cancel` | `false` | Hides the vanilla layer while the overlay is open and visible. |

An overlay without `@UIDataOverlayLayer` is drawn above the whole HUD; an open screen covers it, unless `render.aboveScreens` draws it over the screen. `F1` hides both, except with `render.always`.

| `OverlayLayer` | Vanilla layer |
|---|---|
| `CAMERA_OVERLAYS` | Full-screen effects: the pumpkin and the nether portal (`HELMET`, `PORTAL` of Forge). |
| `CROSSHAIR` | The crosshair (`CROSSHAIRS`). |
| `BOSS_BAR` | The boss bar (`BOSSHEALTH`). |
| `HEALTH` | The hearts (`HEALTH`). |
| `ARMOR` | The armor bar (`ARMOR`). |
| `FOOD` | The hunger bar (`FOOD`). |
| `MOUNT_HEALTH` | The hearts of the ridden mob (`HEALTHMOUNT`). |
| `AIR` | The air bubbles (`AIR`). |
| `HOTBAR` | The hotbar (`HOTBAR`). |
| `CONTEXTUAL_BAR` | The experience bar, or the jump bar of a horse (`EXPERIENCE`, `JUMPBAR`). |
| `CHAT` | The chat (`CHAT`). |
| `PLAYER_LIST` | The player list, `Tab` (`PLAYER_LIST`). |

## Minecraft components

| Node | Shows |
|---|---|
| `ItemNode` | An item stack, with its count, durability bar, glint and tooltip. |
| `BlockNode` | A block in 3D, with its metadata and rotation. |
| `EntityNode` | An entity or a player skin in 3D, which can follow the mouse. |
| `SlotNode` | A slot of a container (see [SlotNode](#slotnode)). |

Items, blocks and entities are lit like in the vanilla inventory. Every setter also takes a signal or a lambda, like any JOID node ([Signals](https://documentation.joid.dev/use/learn/interactivity/signals/)).

### ItemNode

| Method | Default | Effect |
|---|---|---|
| `stack(ItemStack)` | none | Required. The stack to draw, in a square as large as the smallest side of the node, centered. |
| `count(boolean)` | `true` | Draws the count when it is not 1. |
| `bar(boolean)` | `true` | Draws the durability bar of a damaged item. |
| `glint(boolean)` | the item's | `true` adds the enchantment glint, `false` removes it. |
| `tooltip(boolean)` | `false` | Shows the vanilla tooltip of the item on hover. |

```java
ItemNode.create(100, 100, 64, 64).stack(new ItemStack(Items.diamond_sword)).glint(true).tooltip(true).attach(this);

ItemNode.create(200, 100, 64, 64).stack(() -> Minecraft.getMinecraft().thePlayer.getHeldItem()).count(false).attach(this);
```

### BlockNode

| Method | Default | Effect |
|---|---|---|
| `block(Block)` | none | Required. The block to draw, in a square as large as the smallest side of the node, centered. Blocks drawn flat in the inventory (flowers, torches) draw nothing. |
| `metadata(int)` | `0` | The metadata of the block: its color, wood, facing. |
| `rotationYaw(double)` | `225D` | Turn around the vertical axis, in degrees. |
| `rotationPitch(double)` | `30D` | Tilt toward you, in degrees. The defaults give the inventory view. |

```java
BlockNode.create(300, 100, 64, 64).block(Blocks.furnace).metadata(3).rotationYaw(() -> System.currentTimeMillis() % 3600L / 10D).attach(this);
```

### EntityNode

| Method | Default | Effect |
|---|---|---|
| `entity(Entity)` | none | An existing entity, drawn as it is: pose, armor, held items. |
| `type(Class)` | none | A new entity of this class, created in the current world. Nothing is drawn without a world. |
| `profile(GameProfile)` | none | A player with the skin of this profile. The default skin shows until the skin is downloaded. |
| `skin(ResourceLocation)` | none | A player with this skin. |
| `scale(double)` | `1D` | Size relative to the node: the entity is fitted to the node and centered. |
| `rotationYaw(double)` | `0D` | Turn around the vertical axis, in degrees. `0D` faces you. |
| `rotationPitch(double)` | `0D` | Tilt toward you, in degrees. |
| `followMouse(boolean)` | `false` | The body and the head turn toward the mouse while the UI is on top, like the player in the vanilla inventory. |

`entity`, `type`, `profile` and `skin` are four sources: one of them is required, and the last one set wins. `profile` and `skin` also work without a world, on the title screen.

```java
EntityNode.create(100, 300, 120, 200).type(EntityWolf.class).followMouse(true).attach(this);

EntityNode.create(240, 300, 120, 200).entity(() -> Minecraft.getMinecraft().thePlayer).rotationYaw(-30D).attach(this);

EntityNode.create(380, 300, 120, 200).profile(new GameProfile(null, "Notch")).attach(this);

EntityNode.create(520, 300, 120, 200).skin(new ResourceLocation("yourmod", "textures/entity/guard.png")).attach(this);
```

The last one reads its skin from `assets/yourmod/textures/entity/guard.png`, a 64×32 texture like every skin of 1.7.10.

### Tooltips

Tooltips are vanilla tooltips. The text tooltip of any node takes `§` codes, and `drawHover` of a UI takes an `ItemStack`, an `IChatComponent` or a list of lines:

```java
RectNode.create(100, 520, 64, 64).color(Color.decode("#8B8B8B")).hover(() -> "§6Quest board").attach(this);
```

## Text

The Minecraft fonts are JOID fonts: use them in any `TextInfo`, and mix them with your own fonts.

```java
final TextInfo info = TextInfo.create(MinecraftFont.DEFAULT, MinecraftFont.SIZE * 3).color(Color.WHITE).shadowTint(0.25F).shadow(3F, 3F);

TextNode.create(100, 100).text(Text.create("§6Gold §lbold §kmagic", info)).attach(this);

TextNode.create(100, 160).text(Text.create(new ChatComponentTranslation("menu.game"), info)).attach(this);
```

| Member | Font |
|---|---|
| `MinecraftFont.DEFAULT` | The game font, with its bold: `ascii.png` of the resource pack, and the Unicode pages for every other character, or for all of them when the language uses the Unicode font. |
| `MinecraftFont.ALT` | The enchanting table glyphs. |
| `MinecraftFont.SIZE` | `8F`, the vanilla size: `MinecraftFont.SIZE * 3` draws the font three times larger. |

The vanilla shadow is `shadowTint(0.25F)`, the color of each glyph at 25 %, with an offset of a font pixel: `shadow(3F, 3F)` at `SIZE * 3`.

| Code | Effect |
|---|---|
| `§0` to `§9`, `§a` to `§f` | The 16 colors. A color also ends the formats before it. |
| `§l`, `§o`, `§n`, `§m`, `§k` | Bold, italic, underline, strikethrough, obfuscated. They add up. |
| `§r` | Back to the color and style of the `TextInfo`. |

`LegacyTextColor` gives the 16 colors as JOID colors: `LegacyTextColor.GOLD.getColor()`.

A chat `IChatComponent` keeps its colors and styles, and translates its translation parts in the language of the game. The `§` codes and the chat components work with every JOID font, and the Minecraft fonts with everything else of [Text](https://documentation.joid.dev/use/learn/describing-the-ui/text/): markup, effects, alignment, wrapping. Like the vanilla text boxes, JOID text fields drop a typed `§`: the codes come from your code, never from the player.

## Textures and resources

Any texture of the game or of a resource pack is a JOID resource:

```java
ResourceNode.create(100, 100, 64, 64).resource(Resource.of("minecraft:textures/blocks/diamond_block.png")).attach(this);

ResourceNode.create(200, 100, 64, 64).resource(Resource.of(Items.diamond.getIconFromDamage(0))).attach(this);

ResourceNode.create(300, 100, 64, 64).resource(Resource.of(Blocks.crafting_table.getIcon(1, 0))).attach(this);

ResourceNode.create(560, 100, 64, 64).resource(Resource.of(Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationBlocksTexture))).attach(this);
```

| `Resource.of(...)` takes | Draws |
|---|---|
| A `"namespace:path"` string or a `ResourceLocation` | A file of the game or of a resource pack. |
| An `IIcon` | A sprite of an atlas: items, blocks. |
| An `ITextureObject` (a `DynamicTexture`, a `TextureMap`, a loaded texture) or a `Framebuffer` | A texture already on the GPU, such as a whole atlas, live. |

Animated sprites and `.mcmeta` animations play like in game, and the resources follow resource pack reloads. Pixel art stays sharp: these resources use nearest filtering. Other files work the same way: `MsdfFontLoader.load("yourmod:fonts/inter.ttf")` loads a font of your mod. See [Images and media](https://documentation.joid.dev/use/learn/describing-the-ui/media/).

## Sound and key bindings

JOID videos play at the volume of the game: the volume of the `ResourcePlayerNode`, times `Master Volume`. Minecraft 1.7.10 has no `UI` slider; `audioGroup` adds another slider of the Music & Sounds options:

```java
ResourcePlayerNode.create(430, 10, 640, 360).resource(Resource.of(new File("videos/intro.mp4"))).audioGroup(SoundCategory.MUSIC).attach(this);
```

A `KeyBinding` works wherever JOID takes a key: the shortcut follows the key that the player chose in Controls.

```java
Shortcut.create(Minecraft.getMinecraft().gameSettings.keyBindInventory).onTrigger(event -> JOID.close(this)).attach(this);
```

`KeyResolver.resolve(binding)` gives its JOID `Key`, or `null` when it is unbound or on a mouse button.

## Custom drawing

In a custom node, `DrawItem`, `DrawBlock` and `DrawEntity` draw Minecraft objects anywhere:

```java
@Override
public void draw(final double mouseX, final double mouseY) {
	DrawItem.inst().drawItem(super.getX(), super.getY(), 64D, this.stack);
	DrawItem.inst().drawItemCount(super.getX(), super.getY(), 64D, this.stack, "§ex3");
	DrawBlock.inst().drawBlock(super.getX() + 80D, super.getY(), 64D, Blocks.chest, 0, 200D, 20D);
	DrawEntity.inst().drawEntity(super.getX() + 160D, super.getY(), 64D, 128D, Minecraft.getMinecraft().thePlayer, 1D, 0D, 0D, super.toDrawX(mouseX), super.toDrawY(mouseY));
}
```

| Method | Draws |
|---|---|
| `DrawItem.inst().drawItem(x, y, size, stack)` | The item model in a square of `size`. |
| `drawItem(x, y, size, stack, glint)` | With the glint forced on (`true`) or off (`false`), `null` for the glint of the item. |
| `drawItemBar(x, y, size, stack)` | The durability bar of a damaged item. |
| `drawItemCount(x, y, size, stack)` | The count, when it is not 1. |
| `drawItemCount(x, y, size, stack, text)` | Any text in place of the count, `§` codes included. |
| `DrawBlock.inst().drawBlock(x, y, size, block, metadata)` | The block in the inventory view. |
| `drawBlock(x, y, size, block, metadata, rotationYaw, rotationPitch)` | The block with its rotation, in degrees. |
| `DrawEntity.inst().drawEntity(x, y, width, height, entity)` | The entity fitted to the box, facing you. |
| `drawEntity(x, y, width, height, entity, rotationYaw, rotationPitch)` | With its rotation, in degrees. |
| `drawEntity(x, y, width, height, entity, scale, rotationYaw, rotationPitch, lookX, lookY)` | With its scale, and the head turned toward the point `lookX`, `lookY`. |
| `DrawEntity.inst().drawPlayer(...)` | The same three forms with a skin `ResourceLocation` in place of the entity. |

Draw the item, then its bar and count, to match a slot. `toDrawX` and `toDrawY` turn the mouse into the coordinates of `draw`. See [Custom nodes](https://documentation.joid.dev/use/learn/going-further/custom-nodes/) and [Drawing shapes and text](https://documentation.joid.dev/use/learn/going-further/drawing/).

## Tests

Every version branch runs the same tests, written once in the [common branch](https://github.com/Zeldown/JOID-MC/tree/common): a version only brings the code that drives its game.

| Job | Runs |
|---|---|
| `build` | Builds the jars, runs the unit tests and checks the jars: no demo, no test and no dev mode in the `prod` jar. |
| `client-tests` | Starts the game of each loader on OpenGL and plays the client test plan: screens, input, containers, HUD overlays, Minecraft components, text, sound, the contract suites of JOID and its snapshots. |
| `client-tests-vulkan` | The same plan on Vulkan, for the versions that have it. |
| `parity` | Checks that every version branch runs these jobs and keeps the plan of this README. |

All jobs run on the same pinned image, with the Mesa software renderers (llvmpipe for OpenGL, lavapipe for Vulkan). `./gradlew :<loader>:runClient -Pjoid.test` plays the client test plan in a game window: it takes the mouse and the keyboard of the window until the game closes.

Every version compares its snapshots with one set of references per graphics API, kept in the common branch: the OpenGL images of 1.7.10 and of 26.2 must match to within one level per channel, so a rendering gap between versions fails the build.

A version skips a scenario only when Minecraft lacks its concept:

| Version | Skipped | Why |
|---|---|---|
| 26.2 | `STENCIL` | Blaze3D gives no stencil buffer to the screen: JOID emulates it. |
| 1.7.10 | `client-tests-vulkan` | Minecraft 1.7.10 draws with OpenGL only. |
| 1.7.10 | `AUDIO_INTERFACE` | No interface sound category: JOID follows the master volume. |
| 1.7.10 | `ITEM_COOLDOWN` | No item cooldowns, added in 1.9. |
| 1.7.10 | `SCREEN_TITLE` | A screen has no title. |
| 1.7.10 | `SUBTITLES` | No subtitles, added in 1.9. |
| 1.7.10 | `NESTED_FEATURE_RASTER` | No feature render pipeline: the game draws at once. |
| 1.7.10 | `FONT_TRAILING_CODE` | The game measures a trailing `§` as -1 pixel, a bug that JOID does not copy. |
| 1.7.10 | `CONTAINER_CLOSE` | Closing a container drops the carried stack, newer versions give it back to the inventory. |
| 1.7.10 | `MinecraftShaderTranslatorTest` | No shader translation: JOID draws with OpenGL 2.1 itself. |
| 1.7.10 | `ChatTextConverterTest`: hex and palette colors | A chat component has no hex color. |

## Community

Questions, bug reports and ideas are welcome on the [JOID Discord](https://discord.joid.dev).

## License

JOID-MC is released under the [Apache 2.0 license](LICENSE).
