package com.kerbalcraft.entity;

import com.kerbalcraft.registry.ModEntities;
import com.kerbalcraft.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.UUID;

public class RocketEntity extends Entity {
    private static final EntityDataAccessor<Integer> FUEL_TICKS =
            SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TANK_COUNT =
            SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_NOSE =
            SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_DECOUPLER =
            SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> THROTTLE =
            SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.FLOAT);

    private Vec3 thrustDirection = new Vec3(0.0D, 1.0D, 0.0D);
    private UUID ownerId;
    private long lastControlGameTime = Long.MIN_VALUE;
    private static final double THRUST = 0.055D;
    private static final double GRAVITY = 0.025D;

    public RocketEntity(EntityType<? extends RocketEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = false;
    }

    public void configure(int fuelTicks, int tankCount, boolean hasNose, boolean hasDecoupler, Vec3 direction) {
        entityData.set(FUEL_TICKS, Math.max(0, fuelTicks));
        entityData.set(TANK_COUNT, Math.max(1, tankCount));
        entityData.set(HAS_NOSE, hasNose);
        entityData.set(HAS_DECOUPLER, hasDecoupler);
        entityData.set(THROTTLE, 1.0F);
        if (direction != null && direction.lengthSqr() > 0.001D) {
            thrustDirection = direction.normalize();
        }
    }

    public void setOwner(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public int getFuelTicks() {
        return entityData.get(FUEL_TICKS);
    }

    public int getTankCount() {
        return entityData.get(TANK_COUNT);
    }

    public boolean hasNoseCone() {
        return entityData.get(HAS_NOSE);
    }

    public boolean hasDecoupler() {
        return entityData.get(HAS_DECOUPLER);
    }

    public float getThrottle() {
        return entityData.get(THROTTLE);
    }

    public void applyControls(ServerPlayer player, int throttleDelta, float yawInput,
                              float pitchInput, boolean stage) {
        if (level().isClientSide || ownerId == null || !ownerId.equals(player.getUUID())) {
            return;
        }
        long gameTime = level().getGameTime();
        if (lastControlGameTime == gameTime) {
            return;
        }
        lastControlGameTime = gameTime;

        if (throttleDelta > 0) {
            entityData.set(THROTTLE, Math.min(1.0F, getThrottle() + 0.05F));
        } else if (throttleDelta < 0) {
            entityData.set(THROTTLE, Math.max(0.0F, getThrottle() - 0.05F));
        }

        if (Math.abs(yawInput) > 0.01F) {
            double angle = yawInput * 0.025D;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            thrustDirection = new Vec3(
                    thrustDirection.x * cos + thrustDirection.z * sin,
                    thrustDirection.y,
                    -thrustDirection.x * sin + thrustDirection.z * cos
            ).normalize();
        }
        if (Math.abs(pitchInput) > 0.01F) {
            double horizontal = Math.sqrt(thrustDirection.x * thrustDirection.x
                    + thrustDirection.z * thrustDirection.z);
            double angle = Math.atan2(thrustDirection.y, horizontal) + pitchInput * 0.02D;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            if (horizontal > 0.001D) {
                thrustDirection = new Vec3(
                        thrustDirection.x / horizontal * cos,
                        sin,
                        thrustDirection.z / horizontal * cos
                ).normalize();
            }
        }
        if (stage) {
            activateStage();
        }
    }

    private void activateStage() {
        if (!hasDecoupler() || getTankCount() <= 1 || getFuelTicks() <= 0) {
            return;
        }

        Vec3 dropPosition = position().subtract(thrustDirection.scale(1.1D));
        ItemEntity spentTank = new ItemEntity(level(), dropPosition.x, dropPosition.y,
                dropPosition.z, new ItemStack(ModItems.FUEL_TANK.get()));
        spentTank.setDeltaMovement(getDeltaMovement().subtract(thrustDirection.scale(0.18D))
                .add(0.0D, -0.04D, 0.0D));
        level().addFreshEntity(spentTank);

        entityData.set(TANK_COUNT, Math.max(1, getTankCount() - 1));
        entityData.set(FUEL_TICKS, Math.max(0, getFuelTicks() - 200));
        entityData.set(HAS_DECOUPLER, false);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(FUEL_TICKS, 0);
        entityData.define(TANK_COUNT, 1);
        entityData.define(HAS_NOSE, false);
        entityData.define(HAS_DECOUPLER, false);
        entityData.define(THROTTLE, 1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        setNoGravity(true);

        if (!level().isClientSide) {
            Vec3 velocity = getDeltaMovement();

            if (getFuelTicks() > 0 && getThrottle() > 0.0F) {
                velocity = velocity.add(thrustDirection.scale(THRUST * getThrottle()));
                entityData.set(FUEL_TICKS, getFuelTicks() - 1);
            }

            velocity = velocity.add(0.0D, -GRAVITY, 0.0D).scale(0.995D);
            setDeltaMovement(velocity);
            move(MoverType.SELF, velocity);

            if (horizontalCollision || (verticalCollision && tickCount > 5)) {
                discard();
                return;
            }

            if (position().y < level().getMinBuildHeight() - 16) {
                discard();
                return;
            }
        } else {
            move(MoverType.SELF, getDeltaMovement());
        }

        Vec3 velocity = getDeltaMovement();
        if (velocity.lengthSqr() > 0.0001D) {
            setYRot((float) (Math.atan2(-velocity.x, velocity.z) * (180.0D / Math.PI)));
            setXRot((float) (-Math.atan2(velocity.y,
                    Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z)) * (180.0D / Math.PI)));
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        entityData.set(FUEL_TICKS, tag.getInt("FuelTicks"));
        entityData.set(TANK_COUNT, Math.max(1, tag.getInt("TankCount")));
        entityData.set(HAS_NOSE, tag.getBoolean("HasNose"));
        entityData.set(HAS_DECOUPLER, tag.getBoolean("HasDecoupler"));
        entityData.set(THROTTLE, tag.contains("Throttle") ? tag.getFloat("Throttle") : 1.0F);
        if (tag.hasUUID("Owner")) {
            ownerId = tag.getUUID("Owner");
        }
        if (tag.contains("ThrustX")) {
            Vec3 loadedDirection = new Vec3(tag.getDouble("ThrustX"), tag.getDouble("ThrustY"),
                    tag.getDouble("ThrustZ"));
            if (loadedDirection.lengthSqr() > 0.001D) {
                thrustDirection = loadedDirection.normalize();
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("FuelTicks", getFuelTicks());
        tag.putInt("TankCount", getTankCount());
        tag.putBoolean("HasNose", hasNoseCone());
        tag.putBoolean("HasDecoupler", hasDecoupler());
        tag.putFloat("Throttle", getThrottle());
        if (ownerId != null) {
            tag.putUUID("Owner", ownerId);
        }
        tag.putDouble("ThrustX", thrustDirection.x);
        tag.putDouble("ThrustY", thrustDirection.y);
        tag.putDouble("ThrustZ", thrustDirection.z);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        return false;
    }
}
