package com.historicbombs.config;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.conditions.ICondition;

/**
 * Recipe condition that evaluates to true only when bomb crafting is enabled
 * in the mod config. Used by generated recipe JSONs to disable crafting
 * recipes when the server admin sets enableCrafting=false.
 */
public record CraftingEnabledCondition() implements ICondition {
    public static final CraftingEnabledCondition INSTANCE = new CraftingEnabledCondition();
    public static final MapCodec<CraftingEnabledCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public boolean test(IContext context) {
        // If the config hasn't loaded yet (very early in startup), default to allowing recipes
        // so the initial recipe scan doesn't skip them. Config reload will re-evaluate.
        try {
            return HistoricBombsConfig.ENABLE_CRAFTING.get();
        } catch (IllegalStateException e) {
            return true;
        }
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
