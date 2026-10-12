# KerbalCraft In-Game Test Plan

Use Minecraft Java 1.20.1, Forge 47.2.0, and Java 17. These checks must be run in Minecraft; a successful CI build only confirms that the source compiles.

## Launch and inventory

- [ ] Mod starts without a crash or missing registry/model errors.
- [ ] All five rocket parts appear in the Ingredients creative tab.
- [ ] A Command Pod cannot launch without both an engine and at least one fuel tank.
- [ ] A valid rocket launches in the direction the player is looking.
- [ ] Survival consumes the intended parts; Creative preserves them.
- [ ] Fuel time matches the number of tanks (about 10 seconds per tank).

## Controls and flight

- [ ] R raises throttle in small increments, up to 100%.
- [ ] F lowers throttle to 0%.
- [ ] Arrow keys change the rocket's thrust direction smoothly.
- [ ] The server ignores control packets from a player who did not launch that rocket.
- [ ] Telemetry displays plausible altitude, speed, fuel, tank count, and throttle.
- [ ] Collision and out-of-world cleanup do not leave invisible entities behind.

## Staging

- [ ] X does nothing when no decoupler is attached.
- [ ] X does nothing when only one fuel tank remains.
- [ ] With a decoupler and multiple tanks, X ejects one tank item with a visible separation impulse.
- [ ] Fuel capacity decreases by about 10 seconds per ejected tank.
- [ ] Staging still works after fuel reaches zero.

## Models and textures

- [ ] Command Pod, fuel tank, engine, nose cone, and decoupler textures are present.
- [ ] Every item model renders in inventory, hand, and ground display contexts.
- [ ] Rocket parts align without gaps or severe overlap at 1, 2, 3, and 8 tanks.
- [ ] Nose cone and decoupler appear only when included in the launch build.
- [ ] Rocket orientation follows its direction of travel without unexpected flips.

## Save/load and multiplayer

- [ ] Rocket fuel, tank count, throttle, stage state, and owner survive a world save/reload where applicable.
- [ ] Two players can launch separate rockets without controlling one another's rocket.
- [ ] Dedicated-server launch and control work without client-only class crashes.
- [ ] No repeated exceptions appear in the client or server logs.

Record the Minecraft version, Forge version, reproduction steps, and relevant latest.log excerpt for every failure.


## Space environment checks

- Ascend above Y=200 and confirm the space status HUD appears.
- Check that the distant fog darkens while the camera is not underwater or in another fluid.
- Compare airborne falling at ground level and in the space-altitude zone to verify reduced gravity.
- Confirm the fall-distance reset does not cause unexpected behavior when returning to the ground.
- Test full-suit survival, oxygen drain, empty-tank damage, and refill after descending below Y=200.
- Confirm normal fog, gravity, and HUD behavior return below the altitude threshold.
- These checks must be performed in Minecraft; CI compilation alone does not verify rendering or physics.
