package com.kerbalcraft.network;

import com.kerbalcraft.entity.RocketEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RocketControlPacket {
    private final int entityId;
    private final int throttleDelta;
    private final float yawInput;
    private final float pitchInput;
    private final boolean stage;

    public RocketControlPacket(int entityId, int throttleDelta, float yawInput, float pitchInput, boolean stage) {
        this.entityId = entityId;
        this.throttleDelta = Math.max(-1, Math.min(1, throttleDelta));
        this.yawInput = clamp(yawInput);
        this.pitchInput = clamp(pitchInput);
        this.stage = stage;
    }

    public static void encode(RocketControlPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.entityId);
        buffer.writeByte(packet.throttleDelta);
        buffer.writeFloat(packet.yawInput);
        buffer.writeFloat(packet.pitchInput);
        buffer.writeBoolean(packet.stage);
    }

    public static RocketControlPacket decode(FriendlyByteBuf buffer) {
        return new RocketControlPacket(buffer.readVarInt(), buffer.readByte(),
                buffer.readFloat(), buffer.readFloat(), buffer.readBoolean());
    }

    public static void handle(RocketControlPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || player.isRemoved()) {
                return;
            }
            Entity entity = player.serverLevel().getEntity(packet.entityId);
            if (!(entity instanceof RocketEntity rocket)
                    || player.distanceToSqr(rocket) > 128.0D * 128.0D) {
                return;
            }
            rocket.applyControls(player, packet.throttleDelta, packet.yawInput,
                    packet.pitchInput, packet.stage);
        });
        context.setPacketHandled(true);
    }

    private static float clamp(float value) {
        if (!Float.isFinite(value)) {
            return 0.0F;
        }
        return Math.max(-1.0F, Math.min(1.0F, value));
    }
}
