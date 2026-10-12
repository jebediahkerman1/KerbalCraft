package com.kerbalcraft.event;

import com.kerbalcraft.KerbalCraft;
import com.kerbalcraft.registry.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KerbalCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SpaceSuitEvents {
    private static final String OXYGEN_KEY = "KerbalCraftOxygenTicks";
    public static final int MAX_OXYGEN_TICKS = 6000; // Five minutes at 20 ticks per second.
    private static final double SPACE_ALTITUDE = 200.0D;
    private static final ResourceLocation MOON = new ResourceLocation(KerbalCraft.MOD_ID, "moon");

    private SpaceSuitEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide
                || !(event.player instanceof ServerPlayer player)) {
            return;
        }

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack tank = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

        boolean hasHelmetAndTank = helmet.is(ModItems.SPACE_HELMET.get())
                && tank.is(ModItems.AIR_TANK.get());
        boolean inWater = player.isEyeInFluid(FluidTags.WATER);
        boolean inSpace = (player.level().dimension() == Level.OVERWORLD && player.getY() >= SPACE_ALTITUDE)
                || player.level().dimension().location().equals(MOON);

        // High altitude has a low-gravity environment. Counter part of vanilla gravity
        // while airborne; rockets keep using their own flight model.
        if (inSpace && !player.onGround() && player.getVehicle() == null) {
            player.setDeltaMovement(player.getDeltaMovement().add(0.0D, 0.045D, 0.0D));
            player.fallDistance = 0.0F;
        }

        boolean fullSpaceSuit = hasHelmetAndTank
                && leggings.is(ModItems.SPACE_PANTS.get())
                && boots.is(ModItems.SPACE_BOOTS.get());

        if (inSpace && !fullSpaceSuit) {
            // Space has no breathable atmosphere: an incomplete suit cannot protect the player.
            if (player.tickCount % 20 == 0) {
                player.hurt(player.damageSources().generic(), 2.0F);
            }
            return;
        }

        if (!hasHelmetAndTank) {
            return;
        }

        int oxygen = tank.hasTag() && tank.getTag().contains(OXYGEN_KEY)
                ? tank.getTag().getInt(OXYGEN_KEY) : MAX_OXYGEN_TICKS;
        boolean needsOxygen = inWater || inSpace;

        if (needsOxygen && oxygen > 0) {
            oxygen--;
            tank.getOrCreateTag().putInt(OXYGEN_KEY, oxygen);
            if (inSpace && oxygen == 1200) {
                player.displayClientMessage(Component.literal("OXYGEN LOW: 60 seconds remaining")
                        .withStyle(net.minecraft.ChatFormatting.YELLOW), true);
            }
            if (inSpace && oxygen == 200) {
                player.displayClientMessage(Component.literal("CRITICAL OXYGEN: 10 seconds remaining")
                        .withStyle(net.minecraft.ChatFormatting.RED), true);
            }
            if (inWater) {
                // Keep vanilla drowning disabled while oxygen is available.
                player.setAirSupply(300);
            }
        } else if (needsOxygen && oxygen <= 0) {
            // Once the tank is empty, the player starts suffocating in space.
            if (inSpace && player.tickCount % 20 == 0) {
                player.hurt(player.damageSources().generic(), 2.0F);
            }
        } else if (!needsOxygen && oxygen < MAX_OXYGEN_TICKS) {
            // Refill in breathable air, including below the space-altitude threshold.
            oxygen = Math.min(MAX_OXYGEN_TICKS, oxygen + 2);
            tank.getOrCreateTag().putInt(OXYGEN_KEY, oxygen);
        }
    }
}
