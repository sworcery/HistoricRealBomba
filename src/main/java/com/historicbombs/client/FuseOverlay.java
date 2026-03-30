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

/**
 * HUD overlay that displays a fuse countdown bar when any Historic Bomb is lit.
 * Shows the bomb name, seconds remaining, and a depleting progress bar
 * colored by category — turning red in the final 3 seconds.
 */
@OnlyIn(Dist.CLIENT)
public class FuseOverlay implements LayeredDraw.Layer {

    // Layout
    private static final int BAR_WIDTH = 200;
    private static final int BAR_HEIGHT = 8;
    private static final int TOP_Y = 30; // Below boss bar area

    // Colors
    private static final int BG_COLOR = 0xC0000000;       // Semi-transparent black background
    private static final int BAR_BG_COLOR = 0xFF333333;   // Empty bar track
    private static final int BAR_BORDER = 0xFF111111;     // Bar outline
    private static final int URGENT_COLOR = 0xFFFF2222;   // Red for last 3 seconds
    private static final int TEXT_COLOR = 0xFFFFFFFF;      // White
    private static final int TIME_COLOR = 0xFFFFDD44;      // Yellow for timer

    @Override
    public void render(GuiGraphics gfx, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        // Don't show if a screen is open (menus, chat, etc.)
        if (mc.screen != null) return;

        // Find the active bomb with the shortest fuse
        HistoricPrimedTNTEntity nearest = null;
        int shortestFuse = Integer.MAX_VALUE;

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof HistoricPrimedTNTEntity tnt) {
                int fuse = tnt.getFuse();
                if (fuse > 0 && fuse < shortestFuse) {
                    shortestFuse = fuse;
                    nearest = tnt;
                }
            }
        }

        if (nearest == null) return;

        // Get bomb data
        BombData bomb = nearest.getBombData();
        int currentFuse = nearest.getFuse();
        int totalFuse = bomb.getFuseTicks();

        // Smooth interpolation: subtract partial tick for smooth countdown
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
        gfx.drawCenteredString(Minecraft.getInstance().font, name, centerX, TOP_Y - 1, TEXT_COLOR);

        // Progress bar
        int barTop = TOP_Y + 11;
        int barBottom = barTop + BAR_HEIGHT;

        // Bar background (empty track)
        gfx.fill(barLeft - 1, barTop - 1, barRight + 1, barBottom + 1, BAR_BORDER);
        gfx.fill(barLeft, barTop, barRight, barBottom, BAR_BG_COLOR);

        // Filled portion
        int filledWidth = (int) (BAR_WIDTH * progress);
        if (filledWidth > 0) {
            // Color: category color normally, red in last 3 seconds (60 ticks)
            int barColor;
            if (smoothFuse <= 60) {
                // Blend from category color to urgent red
                float urgency = 1.0f - (smoothFuse / 60.0f);
                barColor = blendColors(0xFF000000 | bomb.getCategory().getColor(), URGENT_COLOR, urgency);
            } else {
                barColor = 0xFF000000 | bomb.getCategory().getColor();
            }
            gfx.fill(barLeft, barTop, barLeft + filledWidth, barBottom, barColor);
        }

        // Time text on the right side of bar
        String timeText = String.format("%.1fs", secondsLeft);
        int timeX = barRight + 8;
        gfx.drawString(Minecraft.getInstance().font, timeText, timeX, barTop, TIME_COLOR);

        // Count of active bombs (if more than 1)
        long bombCount = countActiveBombs(mc);
        if (bombCount > 1) {
            String countText = bombCount + " bombs active";
            int countX = panelLeft - Minecraft.getInstance().font.width(countText) - 4;
            gfx.drawString(Minecraft.getInstance().font, countText, barLeft - 4 - Minecraft.getInstance().font.width(countText), barTop, 0xFFFF8844);
        }
    }

    private long countActiveBombs(Minecraft mc) {
        long count = 0;
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof HistoricPrimedTNTEntity tnt && tnt.getFuse() > 0) {
                count++;
            }
        }
        return count;
    }

    /**
     * Linearly blend two ARGB colors.
     * @param color1 Starting color
     * @param color2 Ending color
     * @param t Blend factor 0.0 (all color1) to 1.0 (all color2)
     */
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
