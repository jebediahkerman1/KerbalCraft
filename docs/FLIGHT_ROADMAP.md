# KerbalCraft Flight Systems Roadmap

## Current prototype

- Inventory-based assembly from Command Pod, Engine, Fuel Tanks, and optional Nose Cone/Decoupler.
- Server-authoritative rocket entity and launch ownership.
- Server-validated control packets: throttle, yaw, pitch, and stage action.
- Basic thrust, fuel burn, gravity, and collision cleanup.
- In-flight telemetry HUD with altitude, speed, fuel, tank count, throttle, and control hints.
- Rocket model displays the fuel tanks included in the assembly.
- Stage action splits the rocket into an independently moving upper stage and lower-stage debris entity; the lower stage inherits velocity and receives a separation impulse.

## Remaining engineering milestones

1. **True multistage rockets**
   - Store a stage layout instead of a single decoupler flag.
   - Spawn independent discarded-stage entities and transfer velocity at separation.
   - Burn fuel from the active stage only and allow later stages to ignite.
2. **Rocket editor**
   - Place parts in slots and validate engine/tank/pod compatibility before launch.
   - Persist the design as structured entity/item data.
3. **Flight model**
   - Expose thrust and fuel capacity as part properties.
   - Add throttle-aware acceleration limits, aerodynamic drag, and better collision/landing behavior.
   - Avoid hard-coded single-engine and single-fuel values.
4. **Flight computer**
   - Add vertical and horizontal speed, acceleration, stage indicators, launch countdown, and engine ignition state.
5. **Testing and release**
   - Keep the automated Forge build green.
   - Test launch, controls, staging, item consumption, save/load, and multiplayer on Minecraft 1.20.1 / Forge 47.2.0.
   - Verify model alignment, textures, and rendering in-game.
   - Prepare a released mod jar and a pinned, tested modpack export.

## Design rule

The server owns rocket physics, fuel, ownership, and stage state. Clients send control intentions, never authoritative positions or fuel values. CI compilation is necessary but not a substitute for in-game testing.
