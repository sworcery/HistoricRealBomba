package com.historicbombs.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import com.historicbombs.HistoricBombsMod;
import com.historicbombs.config.HistoricBombsConfig;

/**
 * Handles client-side player login to show the InfoScreen on first world load.
 * Subscribes to the GAME event bus for ClientPlayerNetworkEvent.LoggingIn.
 */
@EventBusSubscriber(modid = HistoricBombsMod.MOD_ID, value = Dist.CLIENT)
public class ClientLoginHandler {

    @SubscribeEvent
    public static void onPlayerLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        // Check config -- if player checked "Don't show again", skip
        if (!HistoricBombsConfig.SHOW_INFOGRAPHIC.get()) return;

        // Schedule screen open on next tick to avoid issues during login
        Minecraft.getInstance().execute(() -> {
            Minecraft.getInstance().setScreen(new InfoScreen());
        });
    }
}
