# KerbalCraft Modpack — Minecraft 1.20.1

This file is the compatibility plan for the eventual modpack. It is not yet an installable CurseForge or Modrinth export: the KerbalCraft jar must be released and the exact compatible mod versions must be pinned before packaging.

## Required baseline

- Minecraft Java Edition 1.20.1
- Minecraft Forge 47.2.x
- Java 17
- KerbalCraft jar built from this repository
- A fresh test profile and a backup of any existing world

## Recommended supporting mods

Keep the pack small until rocket flight is stable.

- **Just Enough Items (JEI):** recipe lookup for rocket parts.
- **Embeddium:** rendering performance.
- **ModernFix:** memory and loading optimizations; verify the selected build against Forge 1.20.1.
- **Configured** or **Catalogue** (optional): easier configuration browsing, if useful for the final pack.
- **Xaero's Minimap** (optional): navigation for launch sites and landing areas.

Optional world-generation, shader, physics, and automation mods should be added only after compatibility testing. Shaders and visual mods are not required for rocket physics. Create, for example, is an optional factory-building expansion, not a hard dependency of KerbalCraft.

## Server/client policy

- Install KerbalCraft and gameplay dependencies on both client and server where required by each mod.
- Client-only rendering/performance mods should stay client-side unless their authors say otherwise.
- Do not include a mod jar unless its license and distribution terms allow bundling it. Prefer a launcher manifest that references each mod's official project and version.
- Pin exact versions in the exported pack and test a clean client launch plus a dedicated-server launch.

## Pre-release checklist

- [ ] Gradle build passes on Java 17.
- [ ] Launch with only Forge and KerbalCraft.
- [ ] Craft each of the five parts and verify the recipes.
- [ ] Launch with one fuel tank, then multiple fuel tanks.
- [ ] Confirm fuel is consumed and flight telemetry updates.
- [ ] Check collision behavior and world saves/reloads.
- [ ] Test multiplayer with two clients.
- [ ] Add optional mods one at a time and repeat smoke tests.
- [ ] Export the launcher pack only after the mod jar is versioned and downloadable.

## Current gameplay prototype

Right-click a Command Pod while carrying at least one Rocket Engine and Fuel Tank. In Survival, the required parts and all carried fuel tanks are consumed to assemble the rocket. Each fuel tank provides 10 seconds of burn time. A Nose Cone and Decoupler are optional visual components. Flight is currently a straight-ahead prototype; steering, player-controlled throttle, real staging, and an assembly editor are not implemented yet.
