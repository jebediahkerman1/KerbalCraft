# KerbalCraft 🚀

A Minecraft Java 1.20.1 Forge mod inspired by Kerbal Space Program.

## First milestone: rocket part items

This starter adds five registered items to the creative inventory:

- Command Pod
- Fuel Tank
- Rocket Engine
- Nose Cone
- Decoupler

These are the first inventory items only. They are not yet attachable or flyable; assembly and flight physics will be added in later milestones.

## Target environment

- Minecraft Java Edition 1.20.1
- Minecraft Forge 47.2.0
- Java 17
- ForgeGradle 6

## Building locally

This repository contains the source and Gradle build configuration. To build it, use a Gradle 8.x installation with Java 17, then run:

```sh
gradle build
```

The compiled mod jar will be in `build/libs/`.

If you prefer the standard Forge MDK workflow, download the Forge 1.20.1 - 47.2.0 MDK and use its Gradle wrapper files in this project.

## Next milestones

1. Custom textures and 3D item models.
2. Placeable rocket assembly stand.
3. Attach rocket parts into a stack.
4. Fuel, engine ignition, thrust, and launch controls.
5. Steering and basic flight physics.
