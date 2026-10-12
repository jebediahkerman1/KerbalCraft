package com.kerbalcraft.client;

import com.kerbalcraft.KerbalCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KerbalCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class SpaceEnvironmentOverlay {
    private static final double SPACE_ALTITUDE = 200.0D;
    private static final ResourceLocation MOON = new ResourceLocation(KerbalCraft.MOD_ID, "moon");
    private static final int STAR_COUNT = 140;

    private SpaceEnvironmentOverlay() {
    }

    @SubscribeEvent
    public static void renderSpaceStatus(RenderGuiOverlayEvent.Post event) {
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        boolean moon = minecraft.level.dimension().location().equals(MOON);
        boolean space = moon || minecraft.player.getY() >= SPACE_ALTITUDE;
        if (!space) {
            return;
        }

        int width = event.getWindow().getGuiScaledWidth();
        int height = event.getWindow().getGuiScaledHeight();

        // Stable screen-space stars keep the space backdrop visible even over the Overworld sky.
        for (int i = 0; i < STAR_COUNT; i++) {
            int x = Math.floorMod(i * 97 + i * i * 13 + 29, Math.max(1, width));
            int y = Math.floorMod(i * 53 + i * i * 7 + 11, Math.max(1, height));
            int size = i % 13 == 0 ? 2 : 1;
            int alpha = 150 + Math.floorMod(i * 37, 106);
            int color = (alpha << 24) | 0x00EAF5FF;
            event.getGuiGraphics().fill(x, y, Math.min(width, x + size), Math.min(height, y + size), color);
        }

        int y = 8;
        event.getGuiGraphics().drawString(minecraft.font,
                net.minecraft.network.chat.Component.literal(moon ? "KERBALCRAFT: THE MOON" : "SPACE ENVIRONMENT"),
                8, y, 0xB8E7FF, true);
        event.getGuiGraphics().drawString(minecraft.font,
                net.minecraft.network.chat.Component.literal(moon
                        ? "Low gravity | Lunar surface"
                        : "Low gravity | Altitude: " + (int) minecraft.player.getY() + " m"),
                8, y + 11, 0xFFFFFF, true);
    }
}
