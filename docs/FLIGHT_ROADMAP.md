# KerbalCraft Flight Systems Roadmap

## Current prototype

- Inventory-based assembly from Command Pod, Engine, Fuel Tanks, and optional Nose Cone/Decoupler.
- Server-authoritative rocket entity.
- Basic thrust, fuel burn, gravity, and collision cleanup.
- In-flight telemetry HUD when a rocket is nearby.
- Rocket model displays the fuel tanks included in the assembly.

## Next engineering milestones

1. **Controls and networking**
   - Add server-validated input packets for throttle, pitch, yaw, and staging.
   - Only allow control from the pilot/owner of a rocket.
   - Rate-limit packets and reject stale or invalid entity IDs.
2. **Staging**
   - Store a rocket's stage layout instead of a single boolean decoupler flag.
   - Separate the upper stage and transfer velocity on staging.
   - Burn fuel from the active stage only.
3. **Rocket editor**
   - Place parts in slots and validate engine/tank/pod compatibility before launch.
   - Persist the design as structured entity/item data.
4. **Flight model**
   - Expose thrust and fuel capacity as part properties.
   - Add throttle, acceleration limits, aerodynamic drag, and better collision/landing behavior.
   - Avoid hard-coded single-engine and single-fuel values.
5. **Flight computer**
   - Add altitude, vertical speed, horizontal speed, fuel, and stage indicators.
   - Add launch countdown and engine ignition state.
6. **Testing**
   - Keep the automated Forge build green.
   - Add smoke-test instructions for single-player and dedicated-server launches.
   - Verify item consumption, saving/loading, and multiplayer replication.

## Design rule

The server owns rocket physics and fuel state. Clients send control intentions, never authoritative positions or fuel values. Visual telemetry is client-side and reads synchronized entity state.
