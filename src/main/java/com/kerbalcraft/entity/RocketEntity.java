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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.Optional;
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
    private static final EntityDataAccessor<Boolean> STAGE_DEBRIS =
            SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> THROTTLE =
            SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<UUID>> OWNER =
            SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.OPTIONAL_UUID);

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
        entityData.set(OWNER, Optional.ofNullable(ownerId));
    }

    public boolean isOwnedBy(UUID candidate) {
        return candidate != null && entityData.get(OWNER).filter(candidate::equals).isPresent();
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

    public boolean isStageDebris() {
        return entityData.get(STAGE_DEBRIS);
    }

    public void configureStageDebris(int tankCount, Vec3 inheritedVelocity) {
        entityData.set(FUEL_TICKS, 0);
        entityData.set(TANK_COUNT, Math.max(1, tankCount));
        entityData.set(HAS_NOSE, false);
        entityData.set(HAS_DECOUPLER, false);
        entityData.set(STAGE_DEBRIS, true);
        entityData.set(THROTTLE, 0.0F);
        setDeltaMovement(inheritedVelocity);
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
        if (!hasDecoupler() || getTankCount() <= 1) {
            return;
        }

        int originalTanks = getTankCount();
        int droppedTanks = Math.max(1, originalTanks / 2);
        int remainingTanks = Math.max(1, originalTanks - droppedTanks);
        int originalFuel = getFuelTicks();
        int droppedFuel = (int) Math.round(originalFuel * (droppedTanks / (double) originalTanks));

        Vec3 separationImpulse = thrustDirection.scale(0.12D);
        Vec3 debrisVelocity = getDeltaMovement().subtract(separationImpulse)
                .add(0.0D, -0.025D, 0.0D);
        Vec3 debrisPosition = position().subtract(thrustDirection.scale(1.8D));
        RocketEntity lowerStage = ModEntities.ROCKET.get().create(level());
        if (lowerStage != null) {
            lowerStage.moveTo(debrisPosition.x, debrisPosition.y, debrisPosition.z, getYRot(), getXRot());
            lowerStage.configureStageDebris(droppedTanks, debrisVelocity);
            level().addFreshEntity(lowerStage);
        }

        entityData.set(TANK_COUNT, remainingTanks);
        entityData.set(FUEL_TICKS, Math.max(0, originalFuel - droppedFuel));
        entityData.set(HAS_DECOUPLER, false);
        setDeltaMovement(getDeltaMovement().add(separationImpulse));
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(FUEL_TICKS, 0);
        entityData.define(TANK_COUNT, 1);
        entityData.define(HAS_NOSE, false);
        entityData.define(HAS_DECOUPLER, false);
        entityData.define(STAGE_DEBRIS, false);
        entityData.define(THROTTLE, 1.0F);
        entityData.define(OWNER, Optional.empty());
    }

    @Override
    public void tick() {
        super.tick();
        setNoGravity(true);

        if (!level().isClientSide) {
            Vec3 velocity = getDeltaMovement();

            if (!isStageDebris() && getFuelTicks() > 0 && getThrottle() > 0.0F) {
                velocity = velocity.add(thrustDirection.scale(THRUST * getThrottle()));
                entityData.set(FUEL_TICKS, getFuelTicks() - 1);
            }

            double altitude = getY();
            boolean inAtmosphere = altitude < 180.0D;
            double atmosphericDrag = inAtmosphere
                    ? 0.985D + Math.max(0.0D, altitude) / 180.0D * 0.010D
                    : 0.995D;
            velocity = velocity.add(0.0D, -GRAVITY, 0.0D).scale(atmosphericDrag);
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
        double altitude = getY();
        if (level().isClientSide && altitude >= 60.0D && altitude <= 180.0D
                && velocity.lengthSqr() > 0.36D) {
            Vec3 heatTrail = position().subtract(velocity.normalize().scale(1.0D));
            if (random.nextInt(2) == 0) {
                level().addParticle(ParticleTypes.FLAME, heatTrail.x, heatTrail.y, heatTrail.z,
                        random.nextGaussian() * 0.025D, random.nextGaussian() * 0.025D,
                        random.nextGaussian() * 0.025D);
            }
            if (random.nextInt(3) == 0) {
                level().addParticle(ParticleTypes.SMOKE, heatTrail.x, heatTrail.y, heatTrail.z,
                        -velocity.x * 0.025D, -velocity.y * 0.025D, -velocity.z * 0.025D);
            }
        }
        if (!isStageDebris() && level().isClientSide && getFuelTicks() > 0 && getThrottle() > 0.0F
                && velocity.lengthSqr() > 0.0001D) {
            Vec3 exhaustDirection = velocity.normalize();
            Vec3 exhaust = position().subtract(exhaustDirection.scale(0.85D));
            level().addParticle(ParticleTypes.FLAME, exhaust.x, exhaust.y, exhaust.z,
                    -exhaustDirection.x * 0.04D, -exhaustDirection.y * 0.04D,
                    -exhaustDirection.z * 0.04D);
            if (random.nextInt(4) == 0) {
                level().addParticle(ParticleTypes.SMOKE, exhaust.x, exhaust.y, exhaust.z,
                        -exhaustDirection.x * 0.02D, -exhaustDirection.y * 0.02D,
                        -exhaustDirection.z * 0.02D);
            }
        }
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
        entityData.set(STAGE_DEBRIS, tag.getBoolean("StageDebris"));
        entityData.set(THROTTLE, tag.contains("Throttle") ? tag.getFloat("Throttle") : 1.0F);
        if (tag.hasUUID("Owner")) {
            ownerId = tag.getUUID("Owner");
            entityData.set(OWNER, Optional.of(ownerId));
        } else {
            ownerId = null;
            entityData.set(OWNER, Optional.empty());
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
        tag.putBoolean("StageDebris", isStageDebris());
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
