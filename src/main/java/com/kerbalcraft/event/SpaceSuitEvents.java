package com.kerbalcraft.event;

import com.kerbalcraft.KerbalCraft;
import com.kerbalcraft.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KerbalCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SpaceSuitEvents {
    private static final String OXYGEN_KEY = "KerbalCraftOxygenTicks";
    public static final int MAX_OXYGEN_TICKS = 6000; // Five minutes at 20 ticks per second.

    private SpaceSuitEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }

        ServerPlayer player = (ServerPlayer) event.player;
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack tank = player.getItemBySlot(EquipmentSlot.CHEST);
        boolean hasSpaceGear = helmet.is(ModItems.SPACE_HELMET.get())
                && tank.is(ModItems.AIR_TANK.get());

        if (!hasSpaceGear) {
            return;
        }

        int oxygen = tank.hasTag() && tank.getTag().contains(OXYGEN_KEY)
                ? tank.getTag().getInt(OXYGEN_KEY) : MAX_OXYGEN_TICKS;

        if (player.isEyeInFluid(FluidTags.WATER)) {
            if (oxygen > 0) {
                oxygen--;
                tank.getOrCreateTag().putInt(OXYGEN_KEY, oxygen);
                // Keep the vanilla drowning timer full while this tank has oxygen.
                player.setAirSupply(300);
            }
        } else if (oxygen < MAX_OXYGEN_TICKS) {
            // The tank refills when the player can breathe normally.
            oxygen = Math.min(MAX_OXYGEN_TICKS, oxygen + 2);
            tank.getOrCreateTag().putInt(OXYGEN_KEY, oxygen);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        // A respawned player keeps the oxygen amount stored on their equipped tank.
        if (!event.isWasDeath()) {
            return;
        }
        ItemStack oldTank = event.getOriginal().getItemBySlot(EquipmentSlot.CHEST);
        ItemStack newTank = event.getEntity().getItemBySlot(EquipmentSlot.CHEST);
        if (oldTank.is(ModItems.AIR_TANK.get()) && newTank.is(ModItems.AIR_TANK.get())
                && oldTank.hasTag() && oldTank.getTag().contains(OXYGEN_KEY)) {
            newTank.getOrCreateTag().putInt(OXYGEN_KEY, oldTank.getTag().getInt(OXYGEN_KEY));
        }
    }
}
