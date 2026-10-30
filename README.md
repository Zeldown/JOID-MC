<div align="center">

<img src="https://raw.githubusercontent.com/Zeldown/JOID-MC/version/26.2/docs/images/logo.png" width="128" alt="JOID logo">

# JOID-MC common
## The part of JOID-MC that every Minecraft version shares

<div align="center">
  <img align="center" src="https://img.shields.io/badge/java-8-blue">
  <img align="center" src="https://img.shields.io/badge/joid-8.0.0-blue">
  <img align="center" src="https://img.shields.io/badge/license-Apache--2.0-blue">
  <img align="center" src="https://github.com/Zeldown/JOID-MC/actions/workflows/build.yml/badge.svg?branch=common">
</div>

</div>

<br>

[JOID-MC](https://github.com/Zeldown/JOID-MC) has one branch per Minecraft version (`version/26.2`, …). This branch holds the code that is the same in all of them, built once into a jar that every version branch embeds, as it embeds the jars of [JOID](https://github.com/Zeldown/JOID). A fix made here reaches every version with the next update of their `libs/` folder.

This is not a mod: to make interfaces for Minecraft, take JOID-MC from the branch of your Minecraft version.

## Contents

The jar depends on `joid-core` only. It holds no class of Minecraft or of a mod loader: where a signature needs a type of the game, it takes a type parameter, which each version branch fills.

| Package | Contents |
|---|---|
| `lib.font.markup`, `lib.font.effect.impl` | The `§` formatting codes: `LegacyTextMarkup`, `LegacyTextColor`, and the obfuscated, strikethrough and underline effects. |
| `lib.ui.core.container` | `BaseContainerUI`, the bounds of a container UI and the node of its carried item. |
| `bridge.ui` | The UI bridges of the game screens, the containers and the overlays (`BaseScreenUIBridge`, `BaseContainerUIBridge`, `BaseOverlayUIBridge`), and their tooltip queue. A version branch only fills their `protected` hooks: drawing, frame counting, cursor, tooltips, game state. |
| `loader.network` | `IPayload`, its reader and writer, and `PayloadType`, typed by the server player of the version. |
| `loader.registry` | `KeyBindRegistry` and `ReloadListenerRegistry`, filled by JOID-MC and read by the mod loader of each version. |
| `demo.test` | The client test plan of every version: its scenarios and assertions, and `IClientTestPilot`, which each version implements to drive its game. Only in the `demo` jar. |

The package names are those of JOID-MC, under `dev.joid.backend.minecraft`: a class keeps its name when it moves between this branch and a version branch.

## Building

JDK 8 is required, since Gradle 3.5 runs on Java 8 only.

```sh
./gradlew build
```

`build/libs` then holds:

| Jar | For |
|---|---|
| `joid-backend-minecraft-common-<version>-dev.jar` | The `dev` jars of the version branches. |
| `joid-backend-minecraft-common-<version>-prod.jar` | The `prod` jars of the version branches. |
| `joid-backend-minecraft-common-<version>-sources.jar` | The `sources` jars of the version branches. |
| `joid-backend-minecraft-common-<version>-demo.jar` | The dev runs of the version branches: the classes of the `dev` jar, the demos and the client test plan. |

`build` also runs the tests and checks the jars: Java 8 classes only, nothing outside `dev/joid/backend/minecraft`, no link to a class of the game, and no link to a class of JOID that the matching jar of JOID does not ship.

## Updating a version branch

`scripts/update-libs.sh` builds the jars, then copies them into the `libs/` folder of each checkout of a version branch given to it, in place of the previous ones, and sets `common_version` in its `gradle.properties`:

```sh
scripts/update-libs.sh ../JOID-MC-26.2 ../JOID-MC-1.7.10
```

Then build each version branch, commit its `libs/` folder and push it: its continuous integration checks the update.

## Adding a version

Each Minecraft version has its own branch, `version/<Minecraft version>`, made from the closest version branch and never renamed. It joins the shared pipeline with three things:

1. **A pilot.** `MinecraftClientTestPilot`, in `dev.joid.backend.minecraft.demo.test`, implements `IClientTestPilot` for the game of the version: input, captures, game state, and the Minecraft objects that the scenarios use. The version starts `ClientTestPlan.start(new MinecraftClientTestPilot())` when the game runs with `-Pjoid.test`. A scenario whose concept the version lacks goes in `getExclusions()` with its reason, and in the table of the Tests section of the README.
2. **Its `build.yml`**, which only calls the shared workflow with the parameters of the version:

```yaml
jobs:
  version:
    uses: Zeldown/JOID-MC/.github/workflows/version.yml@common
    with:
      java-version: '25'
      loaders: '["fabric", "neoforge", "forge"]'
      vulkan: true
```

3. **Its README**, the README of `version/26.2` with the same sections in the same order: only the sentences and the code that the version changes, the same showcases rendered in the version, and the same Tests section.

The `parity` job of every branch fails as soon as a version branch leaves this model.

## License

JOID-MC is released under the [Apache 2.0 license](LICENSE).
