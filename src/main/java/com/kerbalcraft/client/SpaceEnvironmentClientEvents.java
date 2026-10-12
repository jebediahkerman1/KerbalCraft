package com.kerbalcraft.client;

import com.kerbalcraft.KerbalCraft;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KerbalCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class SpaceEnvironmentClientEvents {
    private static final double ATMOSPHERE_START = 120.0D;
    private static final double ORBIT_ALTITUDE = 320.0D;

    private SpaceEnvironmentClientEvents() {
    }

    @SubscribeEvent
    public static void tintSpaceFog(ViewportEvent.ComputeFogColor event) {
        if (event.getCamera().getFluidInCamera() != FogType.NONE) {
            return;
        }

        double altitude = event.getCamera().getEntity().getY();
        float blend = (float) Math.max(0.0D, Math.min(1.0D,
                (altitude - ATMOSPHERE_START) / (ORBIT_ALTITUDE - ATMOSPHERE_START)));
        if (blend <= 0.0F) {
            return;
        }

        // Blend the normal atmospheric fog smoothly toward near-black orbital darkness.
        event.setRed(event.getRed() * (1.0F - blend) + 0.003F * blend);
        event.setGreen(event.getGreen() * (1.0F - blend) + 0.004F * blend);
        event.setBlue(event.getBlue() * (1.0F - blend) + 0.012F * blend);
    }
}
