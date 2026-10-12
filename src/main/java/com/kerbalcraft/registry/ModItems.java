package com.kerbalcraft.registry;

import com.kerbalcraft.KerbalCraft;
import com.kerbalcraft.item.RocketPodItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, KerbalCraft.MOD_ID);

    public static final RegistryObject<Item> COMMAND_POD = ITEMS.register(
            "command_pod",
            () -> new RocketPodItem(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<Item> FUEL_TANK = ITEMS.register(
            "fuel_tank",
            () -> new Item(new Item.Properties().stacksTo(16))
    );

    public static final RegistryObject<Item> ROCKET_ENGINE = ITEMS.register(
            "rocket_engine",
            () -> new Item(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<Item> SRB = ITEMS.register(
            "srb",
            () -> new Item(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<Item> NOSE_CONE = ITEMS.register(
            "nose_cone",
            () -> new Item(new Item.Properties().stacksTo(16))
    );

    public static final RegistryObject<Item> DECOUPLER = ITEMS.register(
            "decoupler",
            () -> new Item(new Item.Properties().stacksTo(16))
    );

    private ModItems() {
    }
}
