package com.kerbalcraft.client;

import com.kerbalcraft.KerbalCraft;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Renders a blocky, cube-shaped Earth as an actual world-space object during orbital flight.
 * This is a visual prototype: its apparent distance is fixed in front of the camera.
 */
@Mod.EventBusSubscriber(modid = KerbalCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class SquareEarthWorldRenderer {
    private static final ResourceLocation MOON = new ResourceLocation(KerbalCraft.MOD_ID, "moon");

    private SquareEarthWorldRenderer() {}

    @SubscribeEvent
    public static void renderEarth(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null
                || minecraft.level.dimension().location().equals(MOON)
                || minecraft.player.getY() < 320.0D) return;

        Vec3 camera = event.getCamera().getPosition();
        var lookVector = event.getCamera().getLookVector();
        Vec3 look = new Vec3(lookVector.x(), lookVector.y(), lookVector.z()).normalize();
        Vec3 center = camera.add(look.scale(96.0D));
        double half = 12.0D;

        PoseStack pose = event.getPoseStack();
        pose.pushPose();
        pose.translate(-camera.x, -camera.y, -camera.z);
        var matrix = pose.last().pose();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        double x0 = center.x - half, x1 = center.x + half;
        double y0 = center.y - half, y1 = center.y + half;
        double z0 = center.z - half, z1 = center.z + half;

        // Six opaque faces give the Earth a deliberately cubic silhouette.
        face(buffer, matrix, x0,y0,z0, x0,y1,z0, x1,y1,z0, x1,y0,z0, 38,101,190);
        face(buffer, matrix, x1,y0,z1, x1,y1,z1, x0,y1,z1, x0,y0,z1, 25,79,158);
        face(buffer, matrix, x0,y0,z1, x0,y1,z1, x0,y1,z0, x0,y0,z0, 30,120,190);
        face(buffer, matrix, x1,y0,z0, x1,y1,z0, x1,y1,z1, x1,y0,z1, 22,88,160);
        face(buffer, matrix, x0,y1,z0, x0,y1,z1, x1,y1,z1, x1,y1,z0, 44,130,210);
        face(buffer, matrix, x0,y0,z1, x0,y0,z0, x1,y0,z0, x1,y0,z1, 15,55,120);
        BufferUploader.drawWithShader(buffer.end());

        // Blocky green landmasses on the camera-facing side.
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        double front = center.z - half - 0.03D;
        face(buffer, matrix,
                center.x - 8, center.y + 5, front,
                center.x - 8, center.y + 1, front,
                center.x - 2, center.y + 1, front,
                center.x - 2, center.y + 5, front, 72,157,75);
        face(buffer, matrix,
                center.x + 1, center.y + 2, front,
                center.x + 1, center.y - 5, front,
                center.x + 8, center.y - 5, front,
                center.x + 8, center.y + 2, front, 51,133,62);
        face(buffer, matrix,
                center.x - 5, center.y - 5, front,
                center.x - 5, center.y - 8, front,
                center.x - 1, center.y - 8, front,
                center.x - 1, center.y - 5, front, 91,179,82);
        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        pose.popPose();
    }

    private static void face(BufferBuilder buffer, org.joml.Matrix4f matrix,
                             double ax, double ay, double az, double bx, double by, double bz,
                             double cx, double cy, double cz, double dx, double dy, double dz,
                             int red, int green, int blue) {
        vertex(buffer, matrix, ax, ay, az, red, green, blue);
        vertex(buffer, matrix, bx, by, bz, red, green, blue);
        vertex(buffer, matrix, cx, cy, cz, red, green, blue);
        vertex(buffer, matrix, dx, dy, dz, red, green, blue);
    }

    private static void vertex(BufferBuilder buffer, org.joml.Matrix4f matrix,
                               double x, double y, double z, int red, int green, int blue) {
        buffer.vertex(matrix, (float) x, (float) y, (float) z)
                .color(red, green, blue, 255).endVertex();
    }
}
