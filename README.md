# KerbalCraft 🚀

A Minecraft Java 1.20.1 Forge mod inspired by Kerbal Space Program.

## Current prototype: assemble and launch

The mod registers five rocket parts in the Ingredients creative tab:

- Command Pod
- Fuel Tank
- Rocket Engine
- Nose Cone
- Decoupler

### Launching a rocket

1. Obtain a Command Pod, at least one Fuel Tank, and a Rocket Engine.
2. Optional: bring a Nose Cone and Decoupler to include them in the rocket's visual stack.
3. Hold the Command Pod and right-click.
4. In Survival, the Command Pod, engine, optional parts, and all carried fuel tanks are consumed to assemble the rocket. Creative mode preserves parts.
5. The rocket launches in the direction you're looking. Each fuel tank adds about 10 seconds of engine burn.
6. When a rocket is nearby, the HUD displays altitude, speed, fuel time, and tank count.

This is an early flight prototype: parts are assembled from inventory rather than snapped together in a construction UI. Steering, player-controlled throttle, actual staging/decoupling, and persistent rocket design data remain future work.

## Target environment

- Minecraft Java Edition 1.20.1
- Minecraft Forge 47.2.0
- Java 17
- ForgeGradle 6

## Building locally

Use Java 17 and Gradle 8.x:

```sh
gradle build
```

The compiled mod jar will be in `build/libs/`. GitHub Actions runs a build on pushes and pull requests.

## Modpack planning

See [docs/MODPACK.md](docs/MODPACK.md) for the Forge baseline, optional supporting mods, and release checklist. See [docs/FLIGHT_ROADMAP.md](docs/FLIGHT_ROADMAP.md) for planned flight systems.

The modpack is not yet an installable launcher export; it needs a released KerbalCraft jar and pinned, tested supporting-mod versions.

## Next milestones

1. Add server-validated steering, throttle, and staging controls.
2. Add a proper assembly stand/editor for physically arranging parts.
3. Improve fuel properties, thrust, aerodynamic drag, and landing behavior.
4. Test saving/loading and multiplayer replication.
