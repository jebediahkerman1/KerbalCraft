package com.kerbalcraft.client;

import com.kerbalcraft.KerbalCraft;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KerbalCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class SpaceEnvironmentClientEvents {
    private static final double SPACE_ALTITUDE = 200.0D;

    private SpaceEnvironmentClientEvents() {
    }

    @SubscribeEvent
    public static void tintSpaceFog(ViewportEvent.ComputeFogColor event) {
        if (event.getCamera().getEntity().getY() < SPACE_ALTITUDE
                || event.getCamera().getFluidInCamera() != FogType.NONE) {
            return;
        }

        // At the edge of space the distant overworld fades toward a near-black sky.
        event.setRed(0.003F);
        event.setGreen(0.004F);
        event.setBlue(0.012F);
    }
}
