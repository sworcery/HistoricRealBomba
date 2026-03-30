package com.historicbombs.init;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.historicbombs.HistoricBombsMod;
import com.historicbombs.block.HistoricTNTBlock;
import com.historicbombs.data.BombData;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
        DeferredRegister.createBlocks(HistoricBombsMod.MOD_ID);

    private static final Map<BombData, DeferredBlock<HistoricTNTBlock>> BOMB_BLOCKS = new LinkedHashMap<>();

    static {
        for (BombData bomb : BombData.values()) {
            DeferredBlock<HistoricTNTBlock> block = BLOCKS.registerBlock(bomb.getRegistryName(),
                props -> new HistoricTNTBlock(bomb, props),
                BlockBehaviour.Properties.of()
                    .mapColor(bomb.getCategory().getMapColor())
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .ignitedByLava());
            BOMB_BLOCKS.put(bomb, block);
        }
    }

    public static DeferredBlock<HistoricTNTBlock> getBlock(BombData bomb) {
        return BOMB_BLOCKS.get(bomb);
    }

    public static Map<BombData, DeferredBlock<HistoricTNTBlock>> getAllBlocks() {
        return BOMB_BLOCKS;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
