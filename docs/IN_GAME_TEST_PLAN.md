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


## Atmospheric re-entry, stars, and Moon checks

- Ascend above Y=200 in the Overworld and confirm the star overlay, space status, and low-gravity feel.
- Descend through Y=180 to Y=60 at high speed in a rocket and check for increased drag plus flame/smoke re-entry particles.
- Confirm re-entry effects do not appear while flying in the Moon dimension.
- As an operator, run `/moon`; confirm the Moon dimension loads and the player arrives above the lunar surface.
- Confirm the Moon has a dark starry sky and a flat stone/gravel surface for this prototype.
- Run `/earth` and confirm return to the Overworld at safe height.
- Test full suit and oxygen consumption in the Moon dimension, and check reduced gravity for both player and rocket.
- Check server startup logs for dimension JSON/codec errors. These checks require actual Minecraft testing; CI alone does not confirm the dimension loads correctly.


## Smooth orbit transition and square Earth

- Ascend through Y=120 to Y=320 and check that fog darkens gradually rather than switching abruptly.
- Confirm stars progressively appear as altitude increases above Y=200.
- At Y=320 and above, verify the HUD changes to the orbit label and the 3D square Earth cube appears in front of the camera.
- Descend below the transition range and confirm the star field, world-space Earth, and orbital label disappear.

## Orbital flight and lunar craters

- [ ] Launch vertically and confirm atmospheric drag fades with altitude instead of applying the same loss at every height.
- [ ] At Y=320+, confirm the rocket bends under inverse-square gravity when it has horizontal velocity.
- [ ] Compare vertical and horizontal speed against total speed in telemetry.
- [ ] Check that estimated apoapsis rises while ascending and stops increasing once vertical speed is negative.
- [ ] Confirm flight-state telemetry changes between atmospheric flight, orbital insertion, and orbital coast.
- [ ] Look around at Y=320+ and confirm the square Earth is a 3D cube with green landmasses; verify terrain correctly occludes it when appropriate.
- [ ] Visit /moon, explore several chunks, and confirm craters are bowl-shaped, stable after chunk reload, and do not expose void or remove the base layer.
- [ ] Test Moon chunk loading in multiplayer and check for excessive chunk-load lag or repeated terrain edits.
- [ ] Verify oxygen stays attached to the equipped Air Tank across save/reload, drains only in water/space, refills in breathable air, and causes damage when depleted in space.

These are manual checks; no in-game run has been performed by the repository changes alone.
