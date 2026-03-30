package com.historicbombs.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.historicbombs.HistoricBombsMod;
import com.historicbombs.entity.HistoricPrimedTNTEntity;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(Registries.ENTITY_TYPE, HistoricBombsMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<HistoricPrimedTNTEntity>> HISTORIC_TNT =
        ENTITY_TYPES.register("historic_primed_tnt",
            () -> EntityType.Builder.<HistoricPrimedTNTEntity>of(HistoricPrimedTNTEntity::new, MobCategory.MISC)
                .sized(0.98F, 0.98F)
                .clientTrackingRange(10)
                .updateInterval(10)
                .fireImmune()
                .build(ResourceKey.create(Registries.ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath(HistoricBombsMod.MOD_ID, "historic_primed_tnt"))));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
