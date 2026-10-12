package com.kerbalcraft.event;

import com.kerbalcraft.KerbalCraft;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KerbalCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SpaceTravelCommands {
    private static final ResourceKey<Level> MOON_KEY = ResourceKey.create(
            Registries.DIMENSION, new ResourceLocation(KerbalCraft.MOD_ID, "moon"));

    private SpaceTravelCommands() {
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("moon")
                .requires(source -> source.hasPermission(2))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    ServerLevel moon = player.server.getLevel(MOON_KEY);
                    if (moon == null) {
                        context.getSource().sendFailure(Component.literal(
                                "The Moon dimension could not load. Check the KerbalCraft datapack files."));
                        return 0;
                    }
                    player.teleportTo(moon, 0.5D, 8.0D, 0.5D, player.getYRot(), player.getXRot());
                    context.getSource().sendSuccess(() -> Component.literal("Welcome to the Moon!"), false);
                    return 1;
                }));

        dispatcher.register(Commands.literal("earth")
                .requires(source -> source.hasPermission(2))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    ServerLevel overworld = player.server.getLevel(Level.OVERWORLD);
                    if (overworld == null) {
                        return 0;
                    }
                    player.teleportTo(overworld, player.getX(), overworld.getHeight(
                            net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING,
                            (int) player.getX(), (int) player.getZ()) + 1.0D,
                            player.getZ(), player.getYRot(), player.getXRot());
                    context.getSource().sendSuccess(() -> Component.literal("Returned to Earth."), false);
                    return 1;
                }));
    }
}
