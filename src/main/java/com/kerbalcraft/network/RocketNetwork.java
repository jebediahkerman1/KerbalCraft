package com.kerbalcraft.network;

import com.kerbalcraft.KerbalCraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class RocketNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(KerbalCraft.MOD_ID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private static boolean registered;

    private RocketNetwork() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        CHANNEL.registerMessage(0, RocketControlPacket.class,
                RocketControlPacket::encode, RocketControlPacket::decode, RocketControlPacket::handle);
        registered = true;
    }
}
