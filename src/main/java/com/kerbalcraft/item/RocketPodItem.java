package com.kerbalcraft.item;

import com.kerbalcraft.entity.RocketEntity;
import com.kerbalcraft.registry.ModEntities;
import com.kerbalcraft.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class RocketPodItem extends Item {
    public RocketPodItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack podStack = player.getItemInHand(hand);

        if (level.isClientSide) {
            return InteractionResultHolder.sidedSuccess(podStack, true);
        }

        Inventory inventory = player.getInventory();
        int engineSlot = findSlot(inventory, ModItems.ROCKET_ENGINE.get());
        int tankCount = countItems(inventory, ModItems.FUEL_TANK.get());

        if (engineSlot < 0 || tankCount < 0 || tankCount == 0) {
            player.displayClientMessage(Component.literal(
                    "Rocket assembly needs a Rocket Engine and a Fuel Tank.")
                    .withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(podStack);
        }

        boolean hasNose = findSlot(inventory, ModItems.NOSE_CONE.get()) >= 0;
        boolean hasDecoupler = findSlot(inventory, ModItems.DECOUPLER.get()) >= 0;

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.fail(podStack);
        }

        if (!player.getAbilities().instabuild) {
            consumeOne(inventory, engineSlot);
            consumeOne(inventory, findSlot(inventory, ModItems.FUEL_TANK.get()));
            if (hasNose) {
                consumeOne(inventory, findSlot(inventory, ModItems.NOSE_CONE.get()));
            }
            if (hasDecoupler) {
                consumeOne(inventory, findSlot(inventory, ModItems.DECOUPLER.get()));
            }
            podStack.shrink(1);
        }

        Vec3 look = player.getLookAngle().normalize();
        RocketEntity rocket = ModEntities.ROCKET.get().create(level);
        if (rocket == null) {
            return InteractionResultHolder.fail(podStack);
        }

        Vec3 spawn = player.position().add(look.scale(1.6D)).add(0.0D, 1.1D, 0.0D);
        rocket.moveTo(spawn.x, spawn.y, spawn.z, player.getYRot(), player.getXRot());
        rocket.configure(20 * 10 * tankCount, hasNose, hasDecoupler, look);
        rocket.setDeltaMovement(look.scale(0.25D));
        level.addFreshEntity(rocket);

        serverPlayer.displayClientMessage(Component.literal(
                "LIFTOFF! Fuel: " + (tankCount * 10) + " seconds. Engine thrust is active.")
                .withStyle(ChatFormatting.GREEN), true);

        return InteractionResultHolder.sidedSuccess(podStack, false);
    }

    private static int countItems(Inventory inventory, Item item) {
        int count = 0;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.is(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static int findSlot(Inventory inventory, Item item) {
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (inventory.getItem(slot).is(item)) {
                return slot;
            }
        }
        return -1;
    }

    private static void consumeOne(Inventory inventory, int slot) {
        if (slot >= 0 && slot < inventory.getContainerSize()) {
            inventory.removeItem(slot, 1);
        }
    }
}
