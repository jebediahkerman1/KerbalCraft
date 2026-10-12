package com.kerbalcraft.client;

import com.kerbalcraft.entity.RocketEntity;
import com.kerbalcraft.registry.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RocketRenderer extends EntityRenderer<RocketEntity> {
    public RocketRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.45F;
    }

    @Override
    public void render(RocketEntity rocket, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-rocket.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(rocket.getXRot()));

        int tankCount = Math.max(1, Math.min(rocket.getTankCount(), 8));
        for (int i = 0; i < tankCount; i++) {
            renderPart(new ItemStack(ModItems.FUEL_TANK.get()), poseStack, buffer, packedLight,
                    0.2D - (i * 0.42D));
        }

        if (rocket.isStageDebris()) {
            // A detached lower stage is just its fuel tanks and engine; it has no pod.
            renderPart(new ItemStack(ModItems.ROCKET_ENGINE.get()), poseStack, buffer, packedLight,
                    -0.55D - (Math.max(0, tankCount - 1) * 0.42D));
        } else {
            renderPart(new ItemStack(ModItems.COMMAND_POD.get()), poseStack, buffer, packedLight,
                    0.85D);
            renderPart(new ItemStack(ModItems.ROCKET_ENGINE.get()), poseStack, buffer, packedLight,
                    -0.55D - (Math.max(0, tankCount - 1) * 0.42D));

            if (rocket.hasNoseCone()) {
                renderPart(new ItemStack(ModItems.NOSE_CONE.get()), poseStack, buffer, packedLight, 1.25D);
            }
            if (rocket.hasDecoupler()) {
                renderPart(new ItemStack(ModItems.DECOUPLER.get()), poseStack, buffer, packedLight,
                        -0.45D - (Math.max(0, tankCount - 1) * 0.42D));
            }
        }

        poseStack.popPose();
        super.render(rocket, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private void renderPart(ItemStack stack, PoseStack poseStack, MultiBufferSource buffer,
                            int packedLight, double y) {
        poseStack.pushPose();
        poseStack.translate(0.0D, y, 0.0D);
        poseStack.scale(1.15F, 1.15F, 1.15F);
        net.minecraft.client.Minecraft.getInstance().getItemRenderer().renderStatic(
                stack, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY,
                poseStack, buffer, net.minecraft.client.Minecraft.getInstance().level, 0);
        poseStack.popPose();
    }

    @Override
    public net.minecraft.resources.ResourceLocation getTextureLocation(RocketEntity entity) {
        return null;
    }
}
