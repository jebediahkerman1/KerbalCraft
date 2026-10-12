package com.kerbalcraft.client;

import com.kerbalcraft.KerbalCraft;
import com.kerbalcraft.entity.RocketEntity;
import com.kerbalcraft.network.RocketControlPacket;
import com.kerbalcraft.network.RocketNetwork;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = KerbalCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class RocketControls {
    private static final String CATEGORY = "key.categories.kerbalcraft";
    private static final KeyMapping THROTTLE_UP = new KeyMapping(
            "key.kerbalcraft.throttle_up", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY);
    private static final KeyMapping THROTTLE_DOWN = new KeyMapping(
            "key.kerbalcraft.throttle_down", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F, CATEGORY);
    private static final KeyMapping STAGE = new KeyMapping(
            "key.kerbalcraft.stage", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_X, CATEGORY);
    private static final KeyMapping STEER_LEFT = new KeyMapping(
            "key.kerbalcraft.steer_left", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT, CATEGORY);
    private static final KeyMapping STEER_RIGHT = new KeyMapping(
            "key.kerbalcraft.steer_right", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT, CATEGORY);
    private static final KeyMapping PITCH_UP = new KeyMapping(
            "key.kerbalcraft.pitch_up", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UP, CATEGORY);
    private static final KeyMapping PITCH_DOWN = new KeyMapping(
            "key.kerbalcraft.pitch_down", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_DOWN, CATEGORY);

    private RocketControls() {
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(THROTTLE_UP);
        event.register(THROTTLE_DOWN);
        event.register(STAGE);
        event.register(STEER_LEFT);
        event.register(STEER_RIGHT);
        event.register(PITCH_UP);
        event.register(PITCH_DOWN);
    }

    @Mod.EventBusSubscriber(modid = KerbalCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static final class ClientEvents {
        private ClientEvents() {
        }

        @SubscribeEvent
        public static void clientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null || minecraft.level == null) {
                return;
            }

            RocketEntity nearest = null;
            double nearestDistance = 96.0D * 96.0D;
            for (RocketEntity rocket : minecraft.level.getEntitiesOfClass(
                    RocketEntity.class, new AABB(minecraft.player.blockPosition()).inflate(96.0D))) {
                if (!rocket.isOwnedBy(minecraft.player.getUUID())) {
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

            int throttleDelta = THROTTLE_UP.isDown() == THROTTLE_DOWN.isDown()
                    ? 0 : (THROTTLE_UP.isDown() ? 1 : -1);
            float yaw = (STEER_RIGHT.isDown() ? 1.0F : 0.0F)
                    - (STEER_LEFT.isDown() ? 1.0F : 0.0F);
            float pitch = (PITCH_UP.isDown() ? 1.0F : 0.0F)
                    - (PITCH_DOWN.isDown() ? 1.0F : 0.0F);
            boolean stage = STAGE.consumeClick();
            RocketNetwork.CHANNEL.sendToServer(new RocketControlPacket(
                    nearest.getId(), throttleDelta, yaw, pitch, stage));
        }
    }
}
