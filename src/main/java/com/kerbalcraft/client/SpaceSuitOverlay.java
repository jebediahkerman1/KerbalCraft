package com.kerbalcraft.client;

import com.kerbalcraft.KerbalCraft;
import com.kerbalcraft.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KerbalCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class SpaceSuitOverlay {
    private static final String OXYGEN_KEY = "KerbalCraftOxygenTicks";
    private static final int MAX_OXYGEN_TICKS = 6000;

    private SpaceSuitOverlay() {
    }

    @SubscribeEvent
    public static void renderOxygen(RenderGuiOverlayEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        ItemStack helmet = minecraft.player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack tank = minecraft.player.getItemBySlot(EquipmentSlot.CHEST);
        if (!helmet.is(ModItems.SPACE_HELMET.get()) || !tank.is(ModItems.AIR_TANK.get())) {
            return;
        }

        int oxygen = tank.hasTag() && tank.getTag().contains(OXYGEN_KEY)
                ? tank.getTag().getInt(OXYGEN_KEY) : MAX_OXYGEN_TICKS;
        int percent = Math.max(0, Math.min(100, Math.round(oxygen * 100.0F / MAX_OXYGEN_TICKS)));
        int color = percent > 50 ? 0x80FF80 : (percent > 20 ? 0xFFD580 : 0xFF5555);
        event.getGuiGraphics().drawString(minecraft.font,
                Component.literal("Oxygen: " + percent + "%"), 8, 8, color, true);
    }
}
