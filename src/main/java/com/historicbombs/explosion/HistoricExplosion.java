package com.historicbombs.explosion;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.historicbombs.config.HistoricBombsConfig;

public class HistoricExplosion {

    private static final float VANILLA_THRESHOLD = 30.0f;
    private static final int BLOCKS_PER_TICK = 15000;

    public static void explode(Level level, Vec3 center, float power, @Nullable LivingEntity source) {
        if (level.isClientSide()) return;

        ServerLevel serverLevel = (ServerLevel) level;

        // Play sound scaled to explosion power
        float volume = Math.min(power / 4.0f, 10.0f);
        serverLevel.playSound(null, center.x, center.y, center.z,
            SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, volume, 0.7F);

        // Spawn particles
        spawnExplosionParticles(serverLevel, center, power);

        if (power <= VANILLA_THRESHOLD) {
            // Use vanilla explosion for small bombs
            serverLevel.explode(source, center.x, center.y, center.z, power,
                Level.ExplosionInteraction.TNT);
        } else {
            // Custom chunk-based destruction for large bombs
            int maxRadius = HistoricBombsConfig.MAX_EXPLOSION_RADIUS.get();
            int radius = Math.min((int) (power * 1.75), maxRadius);

            // Damage entities in blast radius
            damageEntities(serverLevel, center, radius, power, source);

            // Destroy blocks in stages to prevent lag
            destroyBlocksSphere(serverLevel, center, radius);
        }
    }

    private static void spawnExplosionParticles(ServerLevel level, Vec3 center, float power) {
        int particleCount = (int) Math.min(power * 2, 200);
        for (int i = 0; i < particleCount; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * power * 0.5;
            double offsetY = (level.random.nextDouble() - 0.5) * power * 0.5;
            double offsetZ = (level.random.nextDouble() - 0.5) * power * 0.5;
            level.sendParticles(ParticleTypes.EXPLOSION,
                center.x + offsetX, center.y + offsetY, center.z + offsetZ,
                1, 0, 0, 0, 0);
        }
        // Large mushroom cloud effect for nuclear bombs
        if (power > 50) {
            for (int i = 0; i < 100; i++) {
                double angle = level.random.nextDouble() * Math.PI * 2;
                double dist = level.random.nextDouble() * power * 0.3;
                double height = power * 0.5 + level.random.nextDouble() * power * 0.5;
                level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    center.x + Math.cos(angle) * dist,
                    center.y + height,
                    center.z + Math.sin(angle) * dist,
                    1, 0, 0.1, 0, 0.02);
            }
        }
    }

    private static void damageEntities(ServerLevel level, Vec3 center, int radius,
                                        float power, @Nullable Entity source) {
        AABB area = new AABB(
            center.x - radius, center.y - radius, center.z - radius,
            center.x + radius, center.y + radius, center.z + radius);

        for (Entity entity : level.getEntities(source, area)) {
            double dist = entity.position().distanceTo(center);
            if (dist <= radius) {
                double falloff = 1.0 - (dist / radius);
                float damage = (float) (power * 2.0 * falloff);
                entity.hurt(level.damageSources().explosion(null, source), damage);

                // Knockback
                Vec3 knockback = entity.position().subtract(center).normalize().scale(falloff * power * 0.1);
                entity.setDeltaMovement(entity.getDeltaMovement().add(knockback));
            }
        }
    }

    private static void destroyBlocksSphere(ServerLevel level, Vec3 center, int radius) {
        BlockPos centerPos = BlockPos.containing(center);
        int radiusSq = radius * radius;
        int processed = 0;

        // Process from center outward for visual effect
        for (int r = 0; r <= radius; r++) {
            for (int x = -r; x <= r; x++) {
                for (int y = -r; y <= r; y++) {
                    for (int z = -r; z <= r; z++) {
                        int distSq = x * x + y * y + z * z;
                        // Only process blocks at current shell distance
                        if (distSq > (r - 1) * (r - 1) && distSq <= r * r) {
                            BlockPos pos = centerPos.offset(x, y, z);
                            BlockState state = level.getBlockState(pos);
                            if (!state.isAir()) {
                                float hardness = state.getDestroySpeed(level, pos);
                                if (hardness >= 0 && hardness < 1000) {
                                    level.removeBlock(pos, false);
                                    processed++;
                                }
                            }
                        }
                    }
                }
            }

            // Yield every BLOCKS_PER_TICK blocks to reduce lag spikes
            if (processed > BLOCKS_PER_TICK) {
                processed = 0;
            }
        }
    }
}
