package com.kerbalcraft.registry;

import com.kerbalcraft.KerbalCraft;
import com.kerbalcraft.entity.RocketEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, KerbalCraft.MOD_ID);

    public static final RegistryObject<EntityType<RocketEntity>> ROCKET = ENTITY_TYPES.register(
            "rocket",
            () -> EntityType.Builder.<RocketEntity>of(RocketEntity::new, MobCategory.MISC)
                    .sized(0.7F, 2.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("rocket")
    );

    private ModEntities() {
    }
}
