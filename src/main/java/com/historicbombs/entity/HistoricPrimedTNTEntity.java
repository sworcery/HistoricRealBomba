package com.historicbombs.entity;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Level;
import com.historicbombs.data.BombData;
import com.historicbombs.explosion.HistoricExplosion;
import com.historicbombs.init.ModEntities;

public class HistoricPrimedTNTEntity extends Entity implements TraceableEntity {
    private static final EntityDataAccessor<Integer> DATA_FUSE_ID =
        SynchedEntityData.defineId(HistoricPrimedTNTEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> DATA_BOMB_TYPE =
        SynchedEntityData.defineId(HistoricPrimedTNTEntity.class, EntityDataSerializers.STRING);

    @Nullable
    private LivingEntity owner;
    private BombData bombData;

    public HistoricPrimedTNTEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
        this.bombData = BombData.FRITZ_X;
    }

    public HistoricPrimedTNTEntity(Level level, double x, double y, double z,
                                    @Nullable LivingEntity igniter, BombData bombData) {
        this(ModEntities.HISTORIC_TNT.get(), level);
        this.setPos(x, y, z);
        double angle = level.random.nextDouble() * Math.PI * 2.0;
        this.setDeltaMovement(-Math.sin(angle) * 0.02, 0.2, -Math.cos(angle) * 0.02);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.owner = igniter;
        this.bombData = bombData;
        this.setFuse(bombData.getFuseTicks());
        this.entityData.set(DATA_BOMB_TYPE, bombData.name());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_FUSE_ID, 80);
        builder.define(DATA_BOMB_TYPE, BombData.FRITZ_X.name());
    }

    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    public void tick() {
        this.applyGravity();
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
        }

        int fuse = this.getFuse() - 1;
        this.setFuse(fuse);

        if (fuse <= 0) {
            this.discard();
            if (!this.level().isClientSide()) {
                this.explode();
            }
        } else {
            this.updateInWaterStateAndDoFluidPushing();
        }
    }

    private void explode() {
        HistoricExplosion.explode(this.level(), this.position(), getBombData().getExplosionPower(), this.owner);
    }

    @Override
    public final boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Fuse", this.getFuse());
        tag.putString("BombType", bombData.name());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        int loadedFuse = tag.contains("Fuse") ? tag.getInt("Fuse") : 80;
        this.setFuse(Math.max(1, Math.min(loadedFuse, 600)));
        if (tag.contains("BombType")) {
            try {
                this.bombData = BombData.valueOf(tag.getString("BombType"));
                this.entityData.set(DATA_BOMB_TYPE, bombData.name());
            } catch (IllegalArgumentException e) {
                this.bombData = BombData.FRITZ_X;
            }
        }
    }

    public void setFuse(int fuse) {
        this.entityData.set(DATA_FUSE_ID, fuse);
    }

    public int getFuse() {
        return this.entityData.get(DATA_FUSE_ID);
    }

    public BombData getBombData() {
        if (this.level().isClientSide()) {
            String typeName = this.entityData.get(DATA_BOMB_TYPE);
            // Use cached value if synched data hasn't changed
            if (bombData != null && bombData.name().equals(typeName)) {
                return bombData;
            }
            try {
                bombData = BombData.valueOf(typeName);
            } catch (IllegalArgumentException e) {
                bombData = BombData.FRITZ_X;
            }
            return bombData;
        }
        return bombData;
    }

    @Nullable
    @Override
    public Entity getOwner() {
        return this.owner;
    }
}
