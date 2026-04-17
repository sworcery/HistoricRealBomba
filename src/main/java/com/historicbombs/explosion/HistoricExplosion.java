package com.historicbombs.explosion;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import com.historicbombs.HistoricBombsMod;
import com.historicbombs.config.HistoricBombsConfig;

@EventBusSubscriber(modid = HistoricBombsMod.MOD_ID)
public class HistoricExplosion {

    private static final float VANILLA_THRESHOLD = 30.0f;

    // Tick-spreading constants
    private static final int BLOCKS_PER_TICK = 8000;
    private static final int MAX_CONCURRENT_EXPLOSIONS = 3;
    private static final int PARTICLE_CAP = 200;
    private static final int MUSHROOM_CLOUD_PARTICLES = 100;
    private static final float MUSHROOM_CLOUD_THRESHOLD = 50.0f;
    private static final float SOUND_VOLUME_DIVISOR = 4.0f;
    private static final float SOUND_VOLUME_MAX = 10.0f;
    private static final float DAMAGE_MULTIPLIER = 2.0f;
    private static final float KNOCKBACK_SCALE = 0.1f;
    private static final float RADIUS_MULTIPLIER = 1.75f;
    private static final float INDESTRUCTIBLE_HARDNESS = 1000.0f;

    // Active explosions being processed across ticks
    private static final Deque<PendingExplosion> activeExplosions = new ConcurrentLinkedDeque<>();

    public static void explode(Level level, Vec3 center, float power, @Nullable LivingEntity source) {
        if (level.isClientSide()) return;

        ServerLevel serverLevel = (ServerLevel) level;

        // Play sound scaled to explosion power
        float volume = Math.min(power / SOUND_VOLUME_DIVISOR, SOUND_VOLUME_MAX);
        serverLevel.playSound(null, center.x, center.y, center.z,
            SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, volume, 0.7F);

        // Spawn particles
        spawnExplosionParticles(serverLevel, center, power);

        if (power <= VANILLA_THRESHOLD) {
            // Use vanilla explosion for small bombs
            serverLevel.explode(source, center.x, center.y, center.z, power,
                Level.ExplosionInteraction.TNT);
        } else {
            // Enforce concurrent explosion limit — drop if too many are already processing
            if (activeExplosions.size() >= MAX_CONCURRENT_EXPLOSIONS) {
                return;
            }

            int maxRadius = HistoricBombsConfig.MAX_EXPLOSION_RADIUS.get();
            int radius = Math.min((int) (power * RADIUS_MULTIPLIER), maxRadius);

            // Damage entities immediately (one-shot, bounded by entity count in area)
            damageEntities(serverLevel, center, radius, power, source);

            // Queue block destruction to spread across ticks
            activeExplosions.add(new PendingExplosion(serverLevel, center, radius));
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (activeExplosions.isEmpty()) return;

        Iterator<PendingExplosion> it = activeExplosions.iterator();
        while (it.hasNext()) {
            PendingExplosion pending = it.next();
            boolean done = pending.processChunk(BLOCKS_PER_TICK);
            if (done) {
                it.remove();
            }
        }
    }

    public static int getActiveExplosionCount() {
        return activeExplosions.size();
    }

    private static void spawnExplosionParticles(ServerLevel level, Vec3 center, float power) {
        int particleCount = (int) Math.min(power * 2, PARTICLE_CAP);
        for (int i = 0; i < particleCount; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * power * 0.5;
            double offsetY = (level.random.nextDouble() - 0.5) * power * 0.5;
            double offsetZ = (level.random.nextDouble() - 0.5) * power * 0.5;
            level.sendParticles(ParticleTypes.EXPLOSION,
                center.x + offsetX, center.y + offsetY, center.z + offsetZ,
                1, 0, 0, 0, 0);
        }
        // Large mushroom cloud effect for nuclear bombs
        if (power > MUSHROOM_CLOUD_THRESHOLD) {
            for (int i = 0; i < MUSHROOM_CLOUD_PARTICLES; i++) {
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
                float damage = (float) (power * DAMAGE_MULTIPLIER * falloff);
                entity.hurtServer(level, level.damageSources().explosion(source, source), damage);

                // Knockback
                Vec3 knockback = entity.position().subtract(center).normalize().scale(falloff * power * KNOCKBACK_SCALE);
                entity.setDeltaMovement(entity.getDeltaMovement().add(knockback));
            }
        }
    }

    /**
     * Holds the state for a large explosion being processed across multiple server ticks.
     * Uses a flat spherical iteration instead of shell-based to avoid O(n^4) waste.
     */
    private static class PendingExplosion {
        private final ServerLevel level;
        private final BlockPos centerPos;
        private final int radius;
        private final int radiusSq;

        // Current iteration position (flat walk through the bounding cube)
        private int currentX;
        private int currentY;
        private int currentZ;
        private boolean complete = false;

        PendingExplosion(ServerLevel level, Vec3 center, int radius) {
            this.level = level;
            this.centerPos = BlockPos.containing(center);
            this.radius = radius;
            this.radiusSq = radius * radius;
            this.currentX = -radius;
            this.currentY = -radius;
            this.currentZ = -radius;
        }

        /**
         * Process up to maxBlocks block removals this tick.
         * Returns true when the entire sphere has been processed.
         */
        boolean processChunk(int maxBlocks) {
            if (complete) return true;

            int processed = 0;

            while (currentX <= radius) {
                while (currentY <= radius) {
                    while (currentZ <= radius) {
                        int distSq = currentX * currentX + currentY * currentY + currentZ * currentZ;
                        if (distSq <= radiusSq) {
                            BlockPos pos = centerPos.offset(currentX, currentY, currentZ);
                            BlockState state = level.getBlockState(pos);
                            if (!state.isAir()) {
                                float hardness = state.getDestroySpeed(level, pos);
                                if (hardness >= 0 && hardness < INDESTRUCTIBLE_HARDNESS) {
                                    level.removeBlock(pos, false);
                                    processed++;

                                    if (processed >= maxBlocks) {
                                        // Advance to next position and yield
                                        currentZ++;
                                        return false;
                                    }
                                }
                            }
                        }
                        currentZ++;
                    }
                    currentZ = -radius;
                    currentY++;
                }
                currentY = -radius;
                currentX++;
            }

            complete = true;
            return true;
        }
    }
}
