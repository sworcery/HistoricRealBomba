package com.historicbombs.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.historicbombs.data.BombCategory;
import com.historicbombs.data.BombData;
import com.historicbombs.config.HistoricBombsConfig;

import java.util.List;

/**
 * Welcome / infographic screen shown on first world load.
 * Displays a "Field Manual" with category legend, scale reference,
 * fuse warnings, DNU warnings, and a historical note.
 */
@OnlyIn(Dist.CLIENT)
public class InfoScreen extends Screen {

    // Layout constants
    private static final int PANEL_WIDTH = 320;
    private static final int PANEL_PADDING = 12;
    private static final int SECTION_GAP = 14;
    private static final int LINE_HEIGHT = 10;

    // Colors
    private static final int BG_COLOR       = 0xFF202020;
    private static final int BORDER_COLOR   = 0xFF555555;
    private static final int TITLE_COLOR    = 0xFFFFD700;
    private static final int HEADER_COLOR   = 0xFFFFFFFF;
    private static final int TEXT_COLOR      = 0xFFCCCCCC;
    private static final int WARNING_COLOR  = 0xFFFF4444;
    private static final int HAZARD_YELLOW  = 0xFFFFCC00;
    private static final int HAZARD_BLACK   = 0xFF1A1A1A;
    private static final int NOTE_COLOR     = 0xFF88AAFF;

    // Scale reference progression: small conventional → MOAB → fission → thermonuclear → largest
    private static final BombData[] SCALE_BOMBS = {
        BombData.FRITZ_X, BombData.MOAB, BombData.LITTLE_BOY,
        BombData.CASTLE_BRAVO, BombData.TSAR_BOMBA
    };

    // Scroll state
    private double scrollOffset = 0;
    private int contentHeight = 0;
    private int panelTop;
    private int panelLeft;
    private int panelHeight;
    private int contentAreaTop;
    private int contentAreaBottom;

    // Checkbox state
    private boolean dontShowAgain = false;

    public InfoScreen() {
        super(Component.literal("Historic Bombs Mod \u2014 Field Manual"));
    }

    @Override
    protected void init() {
        super.init();

        panelLeft = (this.width - PANEL_WIDTH) / 2;
        panelHeight = Math.min(this.height - 40, 360);
        panelTop = (this.height - panelHeight) / 2;
        contentAreaTop = panelTop + 18;
        contentAreaBottom = panelTop + panelHeight - 36;

        // Precompute content height for scroll limits
        contentHeight = computeContentHeight();

        // Close button (bottom-right of panel)
        int buttonWidth = 80;
        int buttonY = panelTop + panelHeight - 28;
        this.addRenderableWidget(Button.builder(
            Component.literal("Close"),
            button -> this.onClose()
        ).bounds(panelLeft + PANEL_WIDTH - buttonWidth - PANEL_PADDING, buttonY, buttonWidth, 20).build());

        // "Don't show again" checkbox (bottom-left of panel)
        Checkbox checkbox = Checkbox.builder(Component.literal("Don't show again"), this.font)
            .pos(panelLeft + PANEL_PADDING, buttonY + 2)
            .onValueChange((cb, selected) -> dontShowAgain = selected)
            .build();
        this.addRenderableWidget(checkbox);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        // Panel border + background
        gfx.fill(panelLeft - 2, panelTop - 2,
                 panelLeft + PANEL_WIDTH + 2, panelTop + panelHeight + 2, BORDER_COLOR);
        gfx.fill(panelLeft, panelTop,
                 panelLeft + PANEL_WIDTH, panelTop + panelHeight, BG_COLOR);

        // Title bar
        gfx.fill(panelLeft, panelTop, panelLeft + PANEL_WIDTH, contentAreaTop, 0xFF333333);
        gfx.drawCenteredString(this.font, this.title, this.width / 2, panelTop + 5, TITLE_COLOR);

        // Clamp scroll offset
        int viewportHeight = contentAreaBottom - contentAreaTop;
        int maxScroll = Math.max(0, contentHeight - viewportHeight);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        // Enable scissor for scrollable content area
        gfx.enableScissor(panelLeft, contentAreaTop, panelLeft + PANEL_WIDTH, contentAreaBottom);

        int x = panelLeft + PANEL_PADDING;
        int textWidth = PANEL_WIDTH - (PANEL_PADDING * 2);
        int y = contentAreaTop + 4 - (int) scrollOffset;

        // ══ Section 1: Categories ══
        y = drawSectionHeader(gfx, "Categories", x, y, textWidth);
        y += 2;
        for (BombCategory cat : BombCategory.values()) {
            if (cat == BombCategory.DO_NOT_USE) continue;
            y = drawCategorySwatch(gfx, cat.getDisplayName(), cat.getColor(), x, y, TEXT_COLOR);
        }
        // DNU category with warning color
        y = drawCategorySwatch(gfx, "\u26A0 DO NOT USE \u26A0 (Overpowered)", 0xFF0000, x, y, WARNING_COLOR);
        y += SECTION_GAP;

        // ══ Section 2: Scale Reference ══
        y = drawSectionHeader(gfx, "Scale Reference", x, y, textWidth);
        y += 4;
        y = drawScaleChart(gfx, x, y, textWidth);
        y += SECTION_GAP;

        // ══ Section 3: Fuse Warning ══
        y = drawSectionHeader(gfx, "Fuse Timing", x, y, textWidth);
        y += 2;
        y = drawWrappedText(gfx,
            "Bigger bombs have longer fuses, giving you more time to run! " +
            "Fuse times range from 4 seconds (small conventional) to 12 seconds (thermonuclear).",
            x, y, textWidth, TEXT_COLOR);
        y += 4;
        y = drawWrappedText(gfx,
            "DO NOT USE variants have a fixed 12-second fuse. You will need every second.",
            x, y, textWidth, WARNING_COLOR);
        y += SECTION_GAP;

        // ══ Section 4: DNU Warning ══
        y = drawHazardWarning(gfx, x, y, textWidth);
        y += SECTION_GAP;

        // ══ Section 5: Historical Note ══
        y = drawSectionHeader(gfx, "Historical Note", x, y, textWidth);
        y += 2;
        y = drawWrappedText(gfx,
            "This mod features real nuclear weapons tested between 1943 and 2017. " +
            "Over 2,000 nuclear tests were conducted worldwide during the Cold War era.",
            x, y, textWidth, TEXT_COLOR);
        y += 4;
        y = drawWrappedText(gfx,
            "This mod is for educational and entertainment purposes within Minecraft.",
            x, y, textWidth, NOTE_COLOR);
        y += 8;

        // Update content height from actual render
        contentHeight = (y + (int) scrollOffset) - (contentAreaTop + 4);

        gfx.disableScissor();

        // Scroll bar indicator
        if (contentHeight > viewportHeight) {
            int sbX = panelLeft + PANEL_WIDTH - 5;
            int thumbH = Math.max(15, (int) ((float) viewportHeight / contentHeight * viewportHeight));
            int thumbY = contentAreaTop + (int) ((scrollOffset / maxScroll) * (viewportHeight - thumbH));
            gfx.fill(sbX, contentAreaTop, sbX + 3, contentAreaBottom, 0x40FFFFFF);
            gfx.fill(sbX, thumbY, sbX + 3, thumbY + thumbH, 0xAAFFFFFF);
        }

        // Render widgets (buttons, checkbox) on top
        super.render(gfx, mouseX, mouseY, partialTick);
    }

    // ───────────── Drawing helpers ─────────────

    private int drawSectionHeader(GuiGraphics gfx, String text, int x, int y, int maxWidth) {
        gfx.drawString(this.font, "\u2550\u2550 " + text + " \u2550\u2550", x, y, HEADER_COLOR);
        y += LINE_HEIGHT + 2;
        gfx.hLine(x, x + maxWidth, y, 0xFF444444);
        y += 4;
        return y;
    }

    private int drawCategorySwatch(GuiGraphics gfx, String label, int swatchColor, int x, int y, int textColor) {
        int size = 8;
        gfx.fill(x, y + 1, x + size, y + 1 + size, 0xFF000000 | swatchColor);
        gfx.renderOutline(x, y + 1, size, size, 0xFF888888);
        gfx.drawString(this.font, label, x + size + 4, y, textColor);
        y += LINE_HEIGHT + 2;
        return y;
    }

    private int drawScaleChart(GuiGraphics gfx, int x, int y, int textWidth) {
        double logMax = Math.log10(BombData.TSAR_BOMBA.getYieldKt() + 1);
        int barAreaStart = x + 85;
        int barMaxWidth = textWidth - 90;

        for (BombData bomb : SCALE_BOMBS) {
            // Label (truncated if needed)
            String label = bomb.getDisplayName();
            if (this.font.width(label) > 80) {
                while (this.font.width(label + "..") > 80 && label.length() > 1) {
                    label = label.substring(0, label.length() - 1);
                }
                label += "..";
            }
            gfx.drawString(this.font, label, x, y, TEXT_COLOR);

            // Bar (log scale)
            double logVal = Math.log10(bomb.getYieldKt() + 1);
            int barWidth = Math.max(3, (int) ((logVal / logMax) * barMaxWidth));
            int barColor = 0xFF000000 | bomb.getCategory().getColor();
            gfx.fill(barAreaStart, y, barAreaStart + barWidth, y + 8, barColor);
            gfx.renderOutline(barAreaStart, y, barWidth, 8, 0xFF888888);

            // Yield text after bar
            String yieldStr = formatShortYield(bomb);
            int yieldX = barAreaStart + barWidth + 4;
            if (yieldX + this.font.width(yieldStr) <= panelLeft + PANEL_WIDTH - PANEL_PADDING) {
                gfx.drawString(this.font, yieldStr, yieldX, y, 0xFF999999);
            }
            y += LINE_HEIGHT + 3;
        }
        return y;
    }

    private String formatShortYield(BombData bomb) {
        double kt = bomb.getYieldKt();
        if (kt >= 1000) return String.format("%.0f Mt", kt / 1000.0);
        if (kt >= 1) return String.format("%.0f kt", kt);
        return String.format("%.1f t", kt * 1000);
    }

    private int drawHazardWarning(GuiGraphics gfx, int x, int y, int maxWidth) {
        int boxTop = y;

        // Warning title
        gfx.drawCenteredString(this.font,
            "\u26A0\u26A0\u26A0 WARNING: DO NOT USE VARIANTS \u26A0\u26A0\u26A0",
            panelLeft + PANEL_WIDTH / 2, y, WARNING_COLOR);
        y += LINE_HEIGHT + 4;

        // Top hazard stripes
        y = drawHazardStripes(gfx, x, y, maxWidth);
        y += 2;

        y = drawWrappedText(gfx,
            "DO NOT USE variants use LINEAR yield scaling instead of logarithmic. They will:",
            x, y, maxWidth, TEXT_COLOR);
        y += 2;
        y = drawWrappedText(gfx, "\u2022 Destroy hundreds of blocks in every direction",
            x + 8, y, maxWidth - 8, WARNING_COLOR);
        y = drawWrappedText(gfx, "\u2022 Cause massive server/client lag",
            x + 8, y, maxWidth - 8, WARNING_COLOR);
        y = drawWrappedText(gfx, "\u2022 Potentially corrupt your world",
            x + 8, y, maxWidth - 8, WARNING_COLOR);
        y += 2;
        y = drawWrappedText(gfx,
            "DNU variants have massive explosion radii that can destroy entire bases. You have been warned!",
            x, y, maxWidth, HAZARD_YELLOW);
        y += 2;

        // Bottom hazard stripes
        y = drawHazardStripes(gfx, x, y, maxWidth);

        // Red outline around entire warning box
        gfx.renderOutline(x - 2, boxTop - 2, maxWidth + 4, y - boxTop + 4, WARNING_COLOR);

        return y;
    }

    private int drawHazardStripes(GuiGraphics gfx, int x, int y, int maxWidth) {
        for (int i = 0; i < maxWidth; i += 12) {
            int sx = x + i;
            boolean isYellow = (i / 6) % 2 == 0;
            int color = isYellow ? HAZARD_YELLOW : HAZARD_BLACK;
            gfx.fill(sx, y, Math.min(sx + 6, x + maxWidth), y + 3, color);
        }
        return y + 3;
    }

    private int drawWrappedText(GuiGraphics gfx, String text, int x, int y, int maxWidth, int color) {
        List<FormattedCharSequence> lines = this.font.split(Component.literal(text), maxWidth);
        for (FormattedCharSequence line : lines) {
            gfx.drawString(this.font, line, x, y, color);
            y += LINE_HEIGHT;
        }
        return y;
    }

    /**
     * Pre-compute the content height so we know the scroll range before first render.
     * This mirrors the layout logic in render() but without actually drawing.
     */
    private int computeContentHeight() {
        int textWidth = PANEL_WIDTH - (PANEL_PADDING * 2);
        int h = 4; // initial top padding

        // Section 1: Categories (header + 5 entries)
        h += LINE_HEIGHT + 2 + 4; // header
        h += (LINE_HEIGHT + 2) * 5; // 4 standard categories + DNU
        h += SECTION_GAP;

        // Section 2: Scale Reference (header + 5 bars)
        h += LINE_HEIGHT + 2 + 4; // header
        h += 4; // gap
        h += (LINE_HEIGHT + 3) * SCALE_BOMBS.length;
        h += SECTION_GAP;

        // Section 3: Fuse Warning (header + ~4 lines text + ~2 lines warning)
        h += LINE_HEIGHT + 2 + 4; // header
        h += 2;
        h += countWrappedLines("Bigger bombs have longer fuses, giving you more time to run! Fuse times range from 4 seconds (small conventional) to 12 seconds (thermonuclear).", textWidth) * LINE_HEIGHT;
        h += 4;
        h += countWrappedLines("DO NOT USE variants have a fixed 12-second fuse. You will need every second.", textWidth) * LINE_HEIGHT;
        h += SECTION_GAP;

        // Section 4: DNU Warning (~15 lines with stripes)
        h += LINE_HEIGHT + 4; // warning title
        h += 3 + 2; // top stripes
        h += countWrappedLines("DO NOT USE variants use LINEAR yield scaling instead of logarithmic. They will:", textWidth) * LINE_HEIGHT;
        h += 2;
        h += LINE_HEIGHT * 3; // 3 bullet points
        h += 2;
        h += countWrappedLines("DNU variants have massive explosion radii that can destroy entire bases. You have been warned!", textWidth) * LINE_HEIGHT;
        h += 2;
        h += 3; // bottom stripes
        h += SECTION_GAP;

        // Section 5: Historical Note
        h += LINE_HEIGHT + 2 + 4; // header
        h += 2;
        h += countWrappedLines("This mod features real nuclear weapons tested between 1943 and 2017. Over 2,000 nuclear tests were conducted worldwide during the Cold War era.", textWidth) * LINE_HEIGHT;
        h += 4;
        h += countWrappedLines("This mod is for educational and entertainment purposes within Minecraft.", textWidth) * LINE_HEIGHT;
        h += 8;

        return h;
    }

    private int countWrappedLines(String text, int maxWidth) {
        if (this.font == null) return (text.length() / 40) + 1; // rough estimate before font available
        return this.font.split(Component.literal(text), maxWidth).size();
    }

    // ───────────── Input handling ─────────────

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scrollOffset -= scrollY * 12;
        return true;
    }

    @Override
    public void onClose() {
        if (dontShowAgain) {
            HistoricBombsConfig.SHOW_INFOGRAPHIC.set(false);
        }
        super.onClose();
    }

    @Override
    public void renderBackground(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        // Do nothing — skip the default dark/blurred overlay so the game world stays fully visible
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
