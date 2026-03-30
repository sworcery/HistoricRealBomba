package com.historicbombs.data;

import net.minecraft.ChatFormatting;
import net.minecraft.world.level.material.MapColor;

public enum BombCategory {
    THERMONUCLEAR("Thermonuclear Weapon", 0x39FF14, MapColor.COLOR_GREEN, ChatFormatting.GREEN),
    FISSION("Fission Weapon", 0xFFB300, MapColor.COLOR_YELLOW, ChatFormatting.GOLD),
    CONVENTIONAL("Conventional Weapon", 0xC2A366, MapColor.SAND, ChatFormatting.YELLOW),
    THERMOBARIC("Thermobaric Weapon", 0xDC143C, MapColor.COLOR_RED, ChatFormatting.DARK_RED),
    DO_NOT_USE("\u26A0 DO NOT USE \u26A0", 0xFF0000, MapColor.COLOR_RED, ChatFormatting.RED);

    private final String displayName;
    private final int color;
    private final MapColor mapColor;
    private final ChatFormatting chatFormatting;

    BombCategory(String displayName, int color, MapColor mapColor, ChatFormatting chatFormatting) {
        this.displayName = displayName;
        this.color = color;
        this.mapColor = mapColor;
        this.chatFormatting = chatFormatting;
    }

    public String getDisplayName() { return displayName; }
    public int getColor() { return color; }
    public MapColor getMapColor() { return mapColor; }
    public ChatFormatting getChatFormatting() { return chatFormatting; }
}
