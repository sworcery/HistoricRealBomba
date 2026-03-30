package com.historicbombs.init;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.historicbombs.HistoricBombsMod;
import com.historicbombs.data.BombData;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
        DeferredRegister.createItems(HistoricBombsMod.MOD_ID);

    private static final Map<BombData, DeferredItem<BlockItem>> BOMB_ITEMS = new LinkedHashMap<>();

    static {
        for (BombData bomb : BombData.values()) {
            final BombData bombRef = bomb;
            DeferredItem<BlockItem> item = ITEMS.registerItem(bomb.getRegistryName(),
                props -> new BlockItem(ModBlocks.getBlock(bombRef).get(), props) {
                    @Override
                    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                                List<Component> tooltip, TooltipFlag flag) {
                        tooltip.add(Component.literal("Yield: " + bombRef.getFormattedYield())
                            .withStyle(ChatFormatting.GRAY));
                        tooltip.add(Component.literal(bombRef.getCountry() + " \u2014 " + bombRef.getYear())
                            .withStyle(ChatFormatting.GRAY));
                        tooltip.add(Component.literal(bombRef.getCategory().getDisplayName())
                            .withStyle(bombRef.getCategory().getChatFormatting()));
                        tooltip.add(Component.literal(bombRef.getDescription())
                            .withStyle(ChatFormatting.DARK_GRAY));
                        if (bombRef.isDoNotUse()) {
                            tooltip.add(Component.literal("\u26A0 WILL CAUSE EXTREME LAG \u26A0")
                                .withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
                        }
                        super.appendHoverText(stack, context, tooltip, flag);
                    }
                });
            BOMB_ITEMS.put(bomb, item);
        }
    }

    public static DeferredItem<BlockItem> getItem(BombData bomb) {
        return BOMB_ITEMS.get(bomb);
    }

    public static Map<BombData, DeferredItem<BlockItem>> getAllItems() {
        return BOMB_ITEMS;
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
