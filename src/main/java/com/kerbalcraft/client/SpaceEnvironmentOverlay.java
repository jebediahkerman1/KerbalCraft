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
    private static final double ORBIT_ALTITUDE = 320.0D;
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

        double altitude = minecraft.player.getY();
        boolean moon = minecraft.level.dimension().location().equals(MOON);
        boolean space = moon || altitude >= SPACE_ALTITUDE;
        if (!space) {
            return;
        }

        int width = event.getWindow().getGuiScaledWidth();
        int height = event.getWindow().getGuiScaledHeight();

        // Stars gradually appear as the player climbs out of the atmosphere.
        float starBlend = moon ? 1.0F : (float) Math.max(0.0D, Math.min(1.0D,
                (altitude - SPACE_ALTITUDE) / (ORBIT_ALTITUDE - SPACE_ALTITUDE)));
        for (int i = 0; i < STAR_COUNT; i++) {
            float threshold = (float) Math.floorMod(i * 37 + 13, 101) / 100.0F;
            if (threshold > starBlend) {
                continue;
            }
            int x = Math.floorMod(i * 97 + i * i * 13 + 29, Math.max(1, width));
            int y = Math.floorMod(i * 53 + i * i * 7 + 11, Math.max(1, height));
            int size = i % 13 == 0 ? 2 : 1;
            int alpha = 150 + Math.floorMod(i * 37, 106);
            int color = (alpha << 24) | 0x00EAF5FF;
            event.getGuiGraphics().fill(x, y, Math.min(width, x + size), Math.min(height, y + size), color);
        }

        boolean orbit = !moon && altitude >= ORBIT_ALTITUDE;
        int y = 8;
        event.getGuiGraphics().drawString(minecraft.font,
                net.minecraft.network.chat.Component.literal(moon ? "KERBALCRAFT: THE MOON"
                        : orbit ? "KERBALCRAFT: ORBIT" : "SPACE TRANSITION"),
                8, y, 0xB8E7FF, true);
        event.getGuiGraphics().drawString(minecraft.font,
                net.minecraft.network.chat.Component.literal(moon
                        ? "Low gravity | Lunar surface"
                        : orbit ? "Orbit reached | Square Earth below"
                        : "Atmosphere thinning | Altitude: " + (int) altitude + " m"),
                8, y + 11, 0xFFFFFF, true);

        if (orbit) {
            // A deliberately square, blocky Earth icon for the first orbit-view prototype.
            int size = Math.min(92, Math.max(56, width / 8));
            int earthX = width - size - 28;
            int earthY = height / 2 - size / 2;
            event.getGuiGraphics().fill(earthX - 3, earthY - 3,
                    earthX + size + 3, earthY + size + 3, 0xFF101820);
            event.getGuiGraphics().fill(earthX, earthY, earthX + size, earthY + size, 0xFF286BC7);
            event.getGuiGraphics().fill(earthX + size / 8, earthY + size / 7,
                    earthX + size / 2, earthY + size / 3, 0xFF4D9B55);
            event.getGuiGraphics().fill(earthX + size / 2, earthY + size / 3,
                    earthX + size * 7 / 8, earthY + size * 2 / 3, 0xFF398447);
            event.getGuiGraphics().fill(earthX + size / 8, earthY + size * 3 / 4,
                    earthX + size / 3, earthY + size * 7 / 8, 0xFF67B86A);
            event.getGuiGraphics().fill(earthX + size / 6, earthY + size / 2,
                    earthX + size * 5 / 6, earthY + size / 2 + 2, 0x99FFFFFF);
        }
    }
}
