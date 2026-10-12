package com.kerbalcraft.entity;

import com.kerbalcraft.registry.ModEntities;
import com.kerbalcraft.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class RocketEntity extends Entity {
    private static final EntityDataAccessor<Integer> FUEL_TICKS =
            SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_NOSE =
            SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_DECOUPLER =
            SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.BOOLEAN);

    private Vec3 thrustDirection = new Vec3(0.0D, 1.0D, 0.0D);
    private static final double THRUST = 0.055D;
    private static final double GRAVITY = 0.025D;

    public RocketEntity(EntityType<? extends RocketEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = false;
    }

    public void configure(int fuelTicks, boolean hasNose, boolean hasDecoupler, Vec3 direction) {
        entityData.set(FUEL_TICKS, Math.max(0, fuelTicks));
        entityData.set(HAS_NOSE, hasNose);
        entityData.set(HAS_DECOUPLER, hasDecoupler);
        if (direction != null && direction.lengthSqr() > 0.001D) {
            thrustDirection = direction.normalize();
        }
    }

    public int getFuelTicks() {
        return entityData.get(FUEL_TICKS);
    }

    public boolean hasNoseCone() {
        return entityData.get(HAS_NOSE);
    }

    public boolean hasDecoupler() {
        return entityData.get(HAS_DECOUPLER);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(FUEL_TICKS, 0);
        entityData.define(HAS_NOSE, false);
        entityData.define(HAS_DECOUPLER, false);
    }

    @Override
    public void tick() {
        super.tick();
        setNoGravity(true);

        if (!level().isClientSide) {
            Vec3 velocity = getDeltaMovement();

            if (getFuelTicks() > 0) {
                velocity = velocity.add(thrustDirection.scale(THRUST));
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
            setXRot((float) (-Math.atan2(velocity.y, Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z))
                    * (180.0D / Math.PI)));
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        entityData.set(FUEL_TICKS, tag.getInt("FuelTicks"));
        entityData.set(HAS_NOSE, tag.getBoolean("HasNose"));
        entityData.set(HAS_DECOUPLER, tag.getBoolean("HasDecoupler"));
        if (tag.contains("ThrustX")) {
            thrustDirection = new Vec3(tag.getDouble("ThrustX"), tag.getDouble("ThrustY"),
                    tag.getDouble("ThrustZ")).normalize();
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("FuelTicks", getFuelTicks());
        tag.putBoolean("HasNose", hasNoseCone());
        tag.putBoolean("HasDecoupler", hasDecoupler());
        tag.putDouble("ThrustX", thrustDirection.x);
        tag.putDouble("ThrustY", thrustDirection.y);
        tag.putDouble("ThrustZ", thrustDirection.z);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
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
