package com.kerbalcraft;

import com.kerbalcraft.registry.ModEntities;
import com.kerbalcraft.registry.ModItems;
import com.kerbalcraft.network.RocketNetwork;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(KerbalCraft.MOD_ID)
public class KerbalCraft {
    public static final String MOD_ID = "kerbalcraft";

    public KerbalCraft() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        RocketNetwork.register();
    }
}
