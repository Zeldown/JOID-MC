<div align="center">

# JOID Blaze3D
## JOID interfaces inside Minecraft 26.2

</div>

**JOID Blaze3D** is a backend that runs [JOID](https://github.com/Zeldown/JOID) user interfaces inside Minecraft 26.2. JOID is a pure Java UI toolkit: a retained-mode node tree, reactive signals, a composable shader pipeline and MSDF text, with no CSS, no XML and no runtime parser. It never talks to a graphics API directly and goes through bridges instead. This project implements those bridges on top of **Blaze3D**, Minecraft's rendering abstraction, so the same UIs run on both its OpenGL and Vulkan backends, on Fabric, NeoForge and Forge.

## Compatibility

| | Version |
|---|---|
| Minecraft | 26.2 |
| Java | 25 |
| JOID | 7.0.0 |
| Fabric | Loader 0.19.3, Fabric API 0.152.1+26.2 |
| NeoForge | 26.2.0.1-beta |
| Forge | 65.1.3 |

## Bridges

| Class | JOID bridge | Role |
|---|---|---|
| `render.RenderBridge` | `IRenderBridge` | Draws JOID with Blaze3D render pipelines. JOID shaders are translated to GLSL at runtime, textures and framebuffers are GPU textures, and the frame is rendered offscreen then composited into the GUI. |
| `screen.ScreenBridge` | `IWindowBridge`, `IUIBridge` | Window size, mouse, keyboard and clipboard from Minecraft, and the host that opens JOID UIs in Minecraft screens. JOID tooltips are shown as vanilla tooltips. |
| `audio.AudioBridge` | `IAudioBridge` | Streaming audio sources on Minecraft's OpenAL context, following the UI sound category. |

`screen.JOIDScreen` hosts JOID UIs in a regular screen, and `screen.JOIDMenuScreen` in a container screen, drawn under the slots. Every class lives in the `fr.augma.joidblaze3d` package of the `common` project.

## Usage

The bridges are registered once, when the client starts, through `JoidBlaze3D.register()`. Open a UI from the client like with any JOID backend:

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

In game, the `/joid` client command opens the JOID demo chooser.

## How it works

- **Rendering** happens during the GUI extraction of the screen. JOID draws into its own texture, sized to the window framebuffer, which is then blitted pixel for pixel into the deferred GUI.
- **Shaders** are written once in JOID GLSL. They are translated to GLSL 330, with every uniform packed in a single `std140` block, and compiled by Blaze3D for OpenGL or Vulkan.
- **Masks** rely on the stencil buffer in JOID, which Blaze3D 26.2 does not expose. The stencil is emulated with an 8-bit texture that the generated shaders test and write, with the same behaviour as the native OpenGL backends.

## Building

The JOID jar in `libs/` is stored with [Git LFS](https://git-lfs.com), so install it before cloning. Then build with a JDK 25:

```
./gradlew build
```

Each loader writes two jars to `<loader>/build/libs`: the main jar, which embeds JOID and its runtime libraries (vecmath, JavaCV and FFmpeg with the natives of Windows, Linux and macOS), and a `-slim` jar without them.

## Known limitations

- Lines drawn without smoothing are one pixel wide, Blaze3D has no wide lines.
- `TextureWrap.CLAMP_TO_BORDER` behaves like `CLAMP_TO_EDGE`, Blaze3D has no border mode.
- The main jar weighs about 200 MB with the demo build of JOID, mostly FFmpeg natives and demo videos.
- The font and resource loaders of JOID keep non-daemon threads alive, so Minecraft reports a shutdown watchdog crash when it closes.

## Credits

JOID — https://github.com/Zeldown/JOID

This project uses JOID, licensed under the JOID Community Source License v1.0.
See https://github.com/Zeldown/JOID/blob/main/LICENSE.md for the full text.

JOID is developed by **Zeldown**. This project also builds on:

- [MultiLoader Template](https://github.com/jaredlll08/MultiLoader-Template) by **Jared** — Fabric, NeoForge and Forge project layout
- [JavaCV / FFmpeg](https://github.com/bytedeco/javacv) by **Bytedeco** — video decoding used by JOID
- [vecmath](https://search.maven.org/artifact/javax.vecmath/vecmath) — vector math used by JOID
