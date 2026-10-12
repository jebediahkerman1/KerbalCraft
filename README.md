# KerbalCraft 🚀

A Minecraft Java 1.20.1 Forge mod inspired by Kerbal Space Program.

## Current prototype: assemble and fly

The mod registers five rocket parts in the Ingredients creative tab:

- Command Pod
- Fuel Tank
- Rocket Engine
- Nose Cone
- Decoupler

### Launching a rocket

1. Obtain a Command Pod, at least one Fuel Tank, and a Rocket Engine.
2. Optional: bring a Nose Cone and Decoupler to include them in the rocket.
3. Hold the Command Pod and right-click.
4. In Survival, the Command Pod, engine, optional parts, and all carried fuel tanks are consumed to assemble the rocket. Creative mode preserves parts.
5. The rocket launches in the direction you're looking. Each fuel tank provides about 10 seconds of engine burn.
6. When a rocket is nearby, the HUD shows altitude, speed, fuel time, tank count, and throttle.

### Flight controls

The controls target the nearest rocket within 96 blocks. The server validates that the player who launched it is the one controlling it.

- **R** — increase throttle
- **F** — decrease throttle
- **Arrow keys** — steer and pitch the rocket
- **X** — activate the decoupler stage

Press **X** with a decoupler and at least two fuel tanks to split the rocket into an upper stage and a separate lower-stage debris rocket. The lower stage inherits the rocket's velocity and gets a small separation impulse; the upper stage keeps the remaining fuel and continues under your controls. Detached stages fall under gravity and are not targetable by flight controls.

### Space suit and oxygen

- Craft an Air Tank from a Space Suit chestplate and a bucket, then equip it in the chest slot.
- Equip the Space Helmet with the Air Tank to breathe underwater for up to five minutes; the tank refills in breathable air.
- At Y=200 and above, the player enters the mod's space-altitude environment: reduced gravity, near-black distant fog, a star overlay, and an on-screen altitude/status indicator.
- Rockets experience extra atmospheric drag during descent in the Overworld and produce flame/smoke re-entry effects at high speed between Y=60 and Y=180.
- Surviving there requires the Space Helmet, Air Tank, Space Suit Leggings, and Space Suit Boots together.
- Oxygen drains while in the space-altitude zone. An incomplete suit or an empty tank causes damage; the oxygen HUD shows the tank's remaining percentage.
- The mod includes a basic Moon dimension with a flat rocky/gravel surface and a dark, starry sky. It is an early prototype, not a realistic lunar landscape.
- Operators can use `/moon` to travel to the Moon and `/earth` to return to the Overworld. These commands require operator permission.

## Current limitations

This is still an early flight prototype. Parts are assembled from inventory rather than snapped together in a construction UI. Atmospheric re-entry currently uses simplified drag and visual particles, not a full heat-shield or thermal-damage simulation. The Moon is a flat prototype surface rather than a cratered lunar world. The mod has not yet been verified in an actual Minecraft session or multiplayer server. A successful CI build checks compilation; it does not replace in-game testing.

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

See [docs/MODPACK.md](docs/MODPACK.md) for the Forge baseline, optional supporting mods, and release checklist. See [docs/FLIGHT_ROADMAP.md](docs/FLIGHT_ROADMAP.md) for remaining flight systems and [docs/IN_GAME_TEST_PLAN.md](docs/IN_GAME_TEST_PLAN.md) for the hands-on test checklist.

The modpack is not yet an installable launcher export; it needs a released KerbalCraft jar and pinned, tested supporting-mod versions.

## Next milestones

1. Verify the new high-altitude space environment in-game: low gravity, fog tint, status HUD, and suit/oxygen survival.
2. Build a proper assembly stand/editor for physically arranging parts.
3. Add true multi-stage engines with independent fuel and ignition.
4. Improve part-specific fuel capacity, thrust, aerodynamic drag, and landing behavior.
5. Add a separate space dimension or celestial destinations after the altitude zone is stable.
6. Test saving/loading, dedicated servers, multiplayer replication, and in-game visuals.
