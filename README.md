<div align="center">

# JOID-MC

<div align="center">
  <img align="center" src="https://img.shields.io/badge/minecraft-26.2-brightgreen">
  <img align="center" src="https://img.shields.io/badge/joid-7.0.1-blue">
  <img align="center" src="https://img.shields.io/badge/fabric-0.19.5-dbd0b4">
  <img align="center" src="https://img.shields.io/badge/neoforge-26.2.0.88-orange">
  <img align="center" src="https://img.shields.io/badge/forge-65.1.3-1e2d44">
  <img align="center" src="https://img.shields.io/badge/license-Apache 2.0-green">
</div>

## JOID interfaces inside Minecraft 26.2

</div>

**JOID-MC** is a backend that runs [JOID](https://github.com/Zeldown/JOID) user interfaces inside Minecraft 26.2. JOID is a pure Java UI toolkit: a retained-mode node tree, reactive signals, a composable shader pipeline and MSDF text, with no CSS, no XML and no runtime parser. It never talks to a graphics API directly and goes through bridges instead. This project implements those bridges on top of **Blaze3D**, Minecraft's rendering abstraction, so the same UIs run on both its OpenGL and Vulkan backends, on Fabric, NeoForge and Forge.

## Bridges

| Class | JOID bridge | Role |
|---|---|---|
| `lib.bridge.render.RenderBridge` | `IRenderBridge` | Draws JOID with Blaze3D render pipelines. JOID shaders are translated to GLSL at runtime, textures and framebuffers are GPU textures, and the frame is rendered offscreen then composited into the GUI. |
| `lib.bridge.ui.ScreenBridge` | `IWindowBridge`, `IUIBridge` | Window size, mouse, keyboard and clipboard from Minecraft, and the host that opens JOID UIs in Minecraft screens. JOID tooltips are shown as vanilla tooltips. |
| `lib.bridge.audio.AudioBridge` | `IAudioBridge` | Streaming audio sources on Minecraft's OpenAL context, following the UI sound category. |

`lib.screen.JOIDScreen` hosts JOID UIs in a regular screen, and `lib.screen.JOIDMenuScreen` in a container screen, drawn under the slots. Every class lives under the `fr.augma.joidmc` package of the `common` project, organised like the packages of JOID.

## Usage

The bridges are registered once, when the client starts, through `JOIDMC.register()`. Open a UI from the client like with any JOID backend:

```java
JOID.open(new MyUI());
```

For a menu, extend `JOIDMenuScreen` and register the screen for your `MenuType` with the menu screen API of your loader:

```java
public class MyMenuScreen extends JOIDMenuScreen<MyMenu> {

	public MyMenuScreen(final MyMenu menu, final Inventory inventory, final Component title) {
		super(menu, inventory, title, 176, 166, new MyMenuUI());
	}

}
```

A UI is laid out in the JOID design space, stretched to the whole window, so it ignores the Minecraft GUI scale. Annotate it with `@UIMCData` to make it follow the GUI Scale option instead:

```java
@UIMCData
public class MyUI extends UI {

}
```

At the highest GUI scale the UI keeps its full size, and every step down shrinks it by the same ratio as a vanilla screen. Without the annotation, or with `@UIMCData(guiScale = false)`, the UI stays independent of the option.

In a development environment, the `P` key opens the JOID demo chooser in game.

## How it works

- **Rendering** happens during the GUI extraction of the screen. JOID draws into its own texture, sized to the window framebuffer, which is then blitted pixel for pixel into the deferred GUI.
- **Shaders** are written once in JOID GLSL. They are translated to GLSL 330, with every uniform packed in a single `std140` block, and compiled by Blaze3D for OpenGL or Vulkan.
- **Masks** rely on the stencil buffer in JOID, which Blaze3D 26.2 does not expose. The stencil is emulated with an 8-bit texture that the generated shaders test and write, with the same behaviour as the native OpenGL backends.

## Building

The JOID jars in `libs/` are stored with [Git LFS](https://git-lfs.com), so install it before cloning. Then build with a JDK 25:

```
./gradlew build
```

Each loader writes two jars to `<loader>/build/libs`: the main jar, which embeds JOID and its runtime libraries (vecmath, JavaCV and FFmpeg with the natives of Windows, Linux and macOS), and a `-slim` jar without them.

## Known limitations

- Lines drawn without smoothing are one pixel wide, Blaze3D has no wide lines.
- `TextureWrap.CLAMP_TO_BORDER` behaves like `CLAMP_TO_EDGE`, Blaze3D has no border mode.
- The main jar weighs about 100 MB, mostly FFmpeg natives.

## Credits

JOID — https://github.com/Zeldown/JOID

This project uses JOID, licensed under the Apache License 2.0.
See https://github.com/Zeldown/JOID/blob/main/LICENSE for the full text.

JOID is developed by **Zeldown**. This project also builds on:

- [MultiLoader Template](https://github.com/jaredlll08/MultiLoader-Template) by **Jared** — Fabric, NeoForge and Forge project layout
- [JavaCV / FFmpeg](https://github.com/bytedeco/javacv) by **Bytedeco** — video decoding used by JOID
- [vecmath](https://search.maven.org/artifact/javax.vecmath/vecmath) — vector math used by JOID
