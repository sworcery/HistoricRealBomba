package com.historicbombs.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.historicbombs.HistoricBombsMod;
import com.historicbombs.config.HistoricBombsConfig;
import com.historicbombs.data.BombCategory;
import com.historicbombs.data.BombData;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HistoricBombsMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> HISTORIC_BOMBS_TAB =
        CREATIVE_TABS.register("historic_bombs",
            () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.historic_bombs.historic_bombs"))
                .icon(() -> new ItemStack(ModItems.getItem(BombData.TSAR_BOMBA).get()))
                .displayItems((params, output) -> {
                    // Thermonuclear weapons
                    for (BombData bomb : BombData.values()) {
                        if (bomb.getCategory() == BombCategory.THERMONUCLEAR) {
                            output.accept(ModItems.getItem(bomb).get());
                        }
                    }
                    // Fission weapons
                    for (BombData bomb : BombData.values()) {
                        if (bomb.getCategory() == BombCategory.FISSION) {
                            output.accept(ModItems.getItem(bomb).get());
                        }
                    }
                    // Thermobaric weapons
                    for (BombData bomb : BombData.values()) {
                        if (bomb.getCategory() == BombCategory.THERMOBARIC) {
                            output.accept(ModItems.getItem(bomb).get());
                        }
                    }
                    // Conventional weapons
                    for (BombData bomb : BombData.values()) {
                        if (bomb.getCategory() == BombCategory.CONVENTIONAL) {
                            output.accept(ModItems.getItem(bomb).get());
                        }
                    }
                    // DO NOT USE variants (only if enabled in config)
                    if (HistoricBombsConfig.ENABLE_DNU_VARIANTS.get()) {
                        for (BombData bomb : BombData.values()) {
                            if (bomb.getCategory() == BombCategory.DO_NOT_USE) {
                                output.accept(ModItems.getItem(bomb).get());
                            }
                        }
                    }
                })
                .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
