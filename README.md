# ![prefablogo](https://user-images.githubusercontent.com/27054231/120544189-abf34380-c3dc-11eb-91b8-536d1ef47d7c.png)

**Prefab** is a Minecraft mod, a collection of functional and aesthetic buildings that you can place into your world instantly! With our instant houses and structures, you can skip over the time consuming job of landscaping, and trying to build without a creeper undoing your work. Our preview function lets you see the structure in the world, and move through a projection of it in real time. Just pick the place you want to put it, and the Prefab Blueprints do the rest! Whether you haven't got the time, patience or will, we've got structures that are guaranteed to please some of you, but not all of you. :smile:

The building designs are pre-determined (hence the pre-fabrication idea) but can have small pieces of configuration.
You can use this in any mod pack. Please provide feedback on additional structures to add (using vanilla blocks/items is preferred). If you find issues please let me know.

**[CurseForge](https://www.curseforge.com/minecraft/mc-mods/prefab)**

## Minecraft 1.7.10 backport

This branch is a backport of Prefab to **Minecraft 1.7.10 / Forge 10.13.4.1614**. It is based on the
`1.10.2-Forge` branch (the oldest official version) and uses the
[GTNewHorizons mod template](https://github.com/GTNewHorizons/ExampleMod1.7.10) (RetroFuturaGradle).

### Building

Java 25 is used to run Gradle (the toolchains for Minecraft are downloaded automatically).

```bash
./gradlew build
```

The mod jar is written to `build/libs/`. `./gradlew runClient` and `./gradlew runServer` start a development
client/server.

### Differences to the 1.10.2 version

Minecraft 1.7.10 has no block states and is missing a number of blocks and items that the 1.10 structures use.

* The structure files are unchanged. They still describe blocks with their 1.10 names and block state properties.
  `ModernBlockTranslator` converts them to 1.7.10 blocks and metadata (including rotation) when a structure is built
  or previewed.
* Blocks which don't exist in 1.7.10 are replaced:

  | 1.10 block | 1.7.10 replacement |
  | --- | --- |
  | Spruce/birch/jungle/acacia/dark oak doors, fences and fence gates | Oak doors, fences and fence gates |
  | Granite / Diorite / Andesite | Hardened clay / Quartz block / Stone |
  | Prefab granite/diorite/andesite stairs and slabs | Brick, quartz and stone brick stairs/slabs |
  | Bone block | Quartz pillar |
  | Sea lantern | Glowstone |
  | Dark prismarine | Cyan stained hardened clay |
  | Purpur stairs | Quartz stairs |
  | Magma block | Netherrack |
  | Slime block | Lime wool |
  | End rod | Torch |
  | Inverted daylight sensor | Daylight sensor |
  | Coarse dirt | Dirt |
  | Buttons on floors / ceilings | Wooden pressure plate / removed |

* The replacements above are only used when [Et Futurum Requiem](https://github.com/Roadhog360/Et-Futurum-Requiem)
  is not installed (see below).
* The Prefab granite, diorite and andesite stairs and slabs were removed since their base blocks don't exist.
* Recipes which used missing items were adjusted: the Horse Stable only uses oak fences/gates, the Ender Gateway uses
  Eyes of Ender instead of dark prismarine and the Aqua Base uses glowstone instead of a sea lantern.
* Trapdoors in 1.10 don't need a supporting block. The new `Allow Floating Trapdoors` option (enabled by default)
  gives 1.7.10 trapdoors the same behavior so structures keep their decorations.
* The structure preview uses a constant transparency instead of the 1.10 shader.
* The update checker was removed, Forge for 1.7.10 has no version checking API.
* The structure scanning tools (only used by the original author to create structure files) were not ported.

### Et Futurum Requiem support

[Et Futurum Requiem](https://github.com/Roadhog360/Et-Futurum-Requiem) back-ports many blocks from newer versions of
Minecraft. It's an optional dependency: when it's installed, structures are built with its blocks instead of the
vanilla replacements, so they look like they did in 1.10:

* Granite, diorite, andesite (and the polished variants) and Prefab's granite/diorite/andesite stairs and slabs
* Spruce, birch, jungle, acacia and dark oak doors, fences and fence gates
* Coarse dirt, bone blocks, magma blocks, slime blocks, end rods, sea lanterns, prismarine, purpur, red sandstone,
  inverted daylight sensors, iron trapdoors and concrete

The original recipes which use these blocks are also added (Horse Stable with any wood type, Ender Gateway with dark
prismarine and Aqua Base with a sea lantern).

Blocks are looked up by registry name, so blocks which are disabled in the Et Futurum Requiem configuration fall back to
the vanilla replacements. The integration can be turned off with the `Use Et Futurum Requiem Blocks` option.

The development client and server include Et Futurum Requiem. Pass `-PnoEtFuturum` to Gradle to run them without it.
