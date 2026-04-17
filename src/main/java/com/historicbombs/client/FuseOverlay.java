package com.historicbombs.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.historicbombs.data.BombData;
import com.historicbombs.entity.HistoricPrimedTNTEntity;

@OnlyIn(Dist.CLIENT)
public class FuseOverlay implements LayeredDraw.Layer {

    // Layout
    private static final int BAR_WIDTH = 200;
    private static final int BAR_HEIGHT = 8;
    private static final int TOP_Y = 30;

    // Colors
    private static final int BG_COLOR = 0xC0000000;
    private static final int BAR_BG_COLOR = 0xFF333333;
    private static final int BAR_BORDER = 0xFF111111;
    private static final int URGENT_COLOR = 0xFFFF2222;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int TIME_COLOR = 0xFFFFDD44;
    private static final int COUNT_COLOR = 0xFFFF8844;
    private static final int URGENT_THRESHOLD_TICKS = 60;

    @Override
    public void render(GuiGraphics gfx, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        if (mc.screen != null) return;

        // Single pass: find nearest bomb and count active bombs simultaneously
        HistoricPrimedTNTEntity nearest = null;
        int shortestFuse = Integer.MAX_VALUE;
        long bombCount = 0;

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof HistoricPrimedTNTEntity tnt) {
                int fuse = tnt.getFuse();
                if (fuse > 0) {
                    bombCount++;
                    if (fuse < shortestFuse) {
                        shortestFuse = fuse;
                        nearest = tnt;
                    }
                }
            }
        }

        if (nearest == null) return;

        // Get bomb data
        BombData bomb = nearest.getBombData();
        int currentFuse = nearest.getFuse();
        int totalFuse = bomb.getFuseTicks();

        // Smooth interpolation
        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(true);
        float smoothFuse = Math.max(0, currentFuse - partialTick);
        float secondsLeft = smoothFuse / 20.0f;
        float progress = smoothFuse / totalFuse;

        // Screen center
        int screenWidth = gfx.guiWidth();
        int centerX = screenWidth / 2;
        int barLeft = centerX - BAR_WIDTH / 2;
        int barRight = barLeft + BAR_WIDTH;

        // Background panel
        int panelLeft = barLeft - 6;
        int panelRight = barRight + 6;
        int panelTop = TOP_Y - 4;
        int panelBottom = TOP_Y + BAR_HEIGHT + 18;
        gfx.fill(panelLeft, panelTop, panelRight, panelBottom, BG_COLOR);

        // Bomb name
        String name = bomb.getDisplayName();
        gfx.drawCenteredString(mc.font, name, centerX, TOP_Y - 1, TEXT_COLOR);

        // Progress bar
        int barTop = TOP_Y + 11;
        int barBottom = barTop + BAR_HEIGHT;

        // Bar background
        gfx.fill(barLeft - 1, barTop - 1, barRight + 1, barBottom + 1, BAR_BORDER);
        gfx.fill(barLeft, barTop, barRight, barBottom, BAR_BG_COLOR);

        // Filled portion
        int filledWidth = (int) (BAR_WIDTH * progress);
        if (filledWidth > 0) {
            int barColor;
            if (smoothFuse <= URGENT_THRESHOLD_TICKS) {
                float urgency = 1.0f - (smoothFuse / (float) URGENT_THRESHOLD_TICKS);
                barColor = blendColors(0xFF000000 | bomb.getCategory().getColor(), URGENT_COLOR, urgency);
            } else {
                barColor = 0xFF000000 | bomb.getCategory().getColor();
            }
            gfx.fill(barLeft, barTop, barLeft + filledWidth, barBottom, barColor);
        }

        // Time text on right
        String timeText = String.format("%.1fs", secondsLeft);
        gfx.drawString(mc.font, timeText, barRight + 8, barTop, TIME_COLOR);

        // Count of active bombs (if more than 1)
        if (bombCount > 1) {
            String countText = bombCount + " bombs active";
            int countX = barLeft - 4 - mc.font.width(countText);
            gfx.drawString(mc.font, countText, countX, barTop, COUNT_COLOR);
        }
    }

    private static int blendColors(int color1, int color2, float t) {
        t = Math.max(0, Math.min(1, t));
        int a1 = (color1 >> 24) & 0xFF, r1 = (color1 >> 16) & 0xFF, g1 = (color1 >> 8) & 0xFF, b1 = color1 & 0xFF;
        int a2 = (color2 >> 24) & 0xFF, r2 = (color2 >> 16) & 0xFF, g2 = (color2 >> 8) & 0xFF, b2 = color2 & 0xFF;
        int a = (int) (a1 + (a2 - a1) * t);
        int r = (int) (r1 + (r2 - r1) * t);
        int g = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
