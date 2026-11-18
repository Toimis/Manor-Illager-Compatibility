[![License: CC BY-NC-SA 4.0](https://img.shields.io/badge/License-CC_BY--NC--SA_4.0-blue.svg)](LICENSE)

# Manor Illager Compatibility 1.1.3

Serverside mod for Fabric for Minecraft 1.21.1

# Content

Adds [Illager Invasion](https://modrinth.com/mod/illager-invasion) [21.1.6] mobs to the [Dungeons and Taverns Woodland Mansion Overhaul](https://modrinth.com/datapack/dungeons-and-taverns-woodland-mansion-overhaul) [1-v1.2]. Compatible with [Mod Menu](https://modrinth.com/mod/modmenu) [11.0.5].

# Configuration

`config/manor-illagers.properties`

| Group | Vanilla mob | Weighted mobs |
| --- | --- | --- |
| Melee | Vindicator | Basher, Inquisitor, Marauder |
| Caster | Evoker | Provoker, Archivist, Alchemist, Firecaller, Illusioner, Necromancer, Sorcerer |

Each melee slot first rolls the Vindicator percentage. Each caster slot first rolls the Evoker percentage. When that roll fails, the slot chooses one mob from its group in proportion to its weight. If the vanilla roll fails and all weights are zero, the slot stays empty.

Each configurable mob has a `.guaranteed` toggle. The Necromancer guarantee defaults to on and places it at the foot of the basement stairs. If a modified layout omits the hall, it uses the regular caster guarantee. Each newly generated manor assigns other guaranteed mobs to distinct, randomly chosen encounter slots. If the manor has too few suitable slots, the remaining guaranteed mobs spawn alongside the Invoker at the same entrance. This works even with zero chance or weight.

# Build

Use Python 3 and JDK 21. From the extracted source directory run:

    python3 build.py

The JAR appears in dist/. The build checks the compiled Mixin annotation retention before packaging. src contains Java in Fabric intermediary names. resources contains all packaged game data, including the binary structure templates. compile-api contains compile-only signatures for the pinned game version and Mod Menu 11.0.5. Only manor classes and resources ship in the built mod.

At server data load, the mod generates its encounter pools and room templates locally from the installed Woodland Mansion Replacement. The distributed JAR includes the entrance spawn markers.

# DISCLAIMER

Code was generated using AI.
