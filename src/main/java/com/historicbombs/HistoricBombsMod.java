package com.historicbombs;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import com.historicbombs.config.HistoricBombsConfig;
import com.historicbombs.init.ModBlocks;
import com.historicbombs.init.ModItems;
import com.historicbombs.init.ModCreativeTabs;
import com.historicbombs.init.ModEntities;

@Mod(HistoricBombsMod.MOD_ID)
public class HistoricBombsMod {
    public static final String MOD_ID = "historic_bombs";

    public HistoricBombsMod(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, HistoricBombsConfig.SPEC);
    }
}
