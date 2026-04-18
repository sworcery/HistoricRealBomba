package com.historicbombs.init;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import com.historicbombs.HistoricBombsMod;
import com.historicbombs.config.CraftingEnabledCondition;

import java.util.function.Supplier;

public class ModConditions {
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITIONS =
        DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, HistoricBombsMod.MOD_ID);

    public static final Supplier<MapCodec<? extends ICondition>> CRAFTING_ENABLED =
        CONDITIONS.register("crafting_enabled", () -> CraftingEnabledCondition.CODEC);

    public static void register(IEventBus eventBus) {
        CONDITIONS.register(eventBus);
    }
}
