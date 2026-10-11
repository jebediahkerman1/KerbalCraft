package com.kerbalcraft.registry;

import com.kerbalcraft.KerbalCraft;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KerbalCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModCreativeTabs {
    private ModCreativeTabs() {
    }

    @SubscribeEvent
    public static void addRocketPartsToCreativeInventory(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.COMMAND_POD);
            event.accept(ModItems.FUEL_TANK);
            event.accept(ModItems.ROCKET_ENGINE);
            event.accept(ModItems.NOSE_CONE);
            event.accept(ModItems.DECOUPLER);
        }
    }
}
