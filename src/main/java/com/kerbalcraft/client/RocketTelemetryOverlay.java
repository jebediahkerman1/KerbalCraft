package com.kerbalcraft.client;

import com.kerbalcraft.KerbalCraft;
import com.kerbalcraft.entity.RocketEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KerbalCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class RocketTelemetryOverlay {
    private RocketTelemetryOverlay() {
    }

    @SubscribeEvent
    public static void renderTelemetry(RenderGuiOverlayEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        RocketEntity nearest = null;
        double nearestDistance = 96.0D * 96.0D;
        for (RocketEntity rocket : minecraft.level.getEntitiesOfClass(
                RocketEntity.class, minecraft.player.getBoundingBox().inflate(96.0D))) {
            if (rocket.isStageDebris()) {
                continue;
            }
            double distance = minecraft.player.distanceToSqr(rocket);
            if (distance < nearestDistance) {
                nearest = rocket;
                nearestDistance = distance;
            }
        }

        if (nearest == null) {
            return;
        }

        Vec3 velocity = nearest.getDeltaMovement();
        double speed = velocity.length() * 20.0D;
        String fuel = String.format("%.1f s", nearest.getFuelTicks() / 20.0D);
        int x = 8;
        int y = 8;
        event.getGuiGraphics().drawString(minecraft.font,
                Component.literal("KERBALCRAFT TELEMETRY"), x, y, 0xFFFFFF, true);
        event.getGuiGraphics().drawString(minecraft.font,
                Component.literal(String.format("Altitude: %.1f m", nearest.getY())),
                x, y + 12, 0xB8E7FF, true);
        event.getGuiGraphics().drawString(minecraft.font,
                Component.literal(String.format("Speed: %.1f m/s", speed)),
                x, y + 24, 0xB8E7FF, true);
        event.getGuiGraphics().drawString(minecraft.font,
                Component.literal("Fuel remaining: " + fuel),
                x, y + 36, nearest.getFuelTicks() > 0 ? 0x80FF80 : 0xFF7777, true);
        event.getGuiGraphics().drawString(minecraft.font,
                Component.literal("Fuel tanks: " + nearest.getTankCount()),
                x, y + 48, 0xFFFFFF, true);
        event.getGuiGraphics().drawString(minecraft.font,
                Component.literal(String.format("Throttle: %.0f%%", nearest.getThrottle() * 100.0F)),
                x, y + 60, 0xFFD580, true);
        event.getGuiGraphics().drawString(minecraft.font,
                Component.literal("R/F throttle | Arrows steer | X stage"),
                x, y + 72, 0xFFFFFF, true);
    }
}
