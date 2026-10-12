package com.kerbalcraft.client;

import com.kerbalcraft.KerbalCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KerbalCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class SpaceEnvironmentOverlay {
    private static final double SPACE_ALTITUDE = 200.0D;

    private SpaceEnvironmentOverlay() {
    }

    @SubscribeEvent
    public static void renderSpaceStatus(RenderGuiOverlayEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null
                || minecraft.player.getY() < SPACE_ALTITUDE) {
            return;
        }

        int x = 8;
        int y = minecraft.player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD).isEmpty()
                ? 8 : 22;
        event.getGuiGraphics().drawString(minecraft.font,
                Component.literal("SPACE ENVIRONMENT"), x, y, 0xB8E7FF, true);
        event.getGuiGraphics().drawString(minecraft.font,
                Component.literal("Low gravity | Altitude: " + (int) minecraft.player.getY() + " m"),
                x, y + 11, 0xFFFFFF, true);
    }
}
