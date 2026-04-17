package com.historicbombs.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class HistoricBombsConfig {
    // Common config (server-side, shared)
    public static final ModConfigSpec COMMON_SPEC;
    public static final ModConfigSpec.BooleanValue ENABLE_DNU_VARIANTS;
    public static final ModConfigSpec.IntValue MAX_EXPLOSION_RADIUS;
    public static final ModConfigSpec.BooleanValue ENABLE_CRAFTING;

    // Client config (per-player, client-only)
    public static final ModConfigSpec CLIENT_SPEC;
    public static final ModConfigSpec.BooleanValue SHOW_INFOGRAPHIC;

    static {
        // Common config
        ModConfigSpec.Builder common = new ModConfigSpec.Builder();

        common.comment("Historic Bombs Mod Configuration");

        common.push("general");
        ENABLE_DNU_VARIANTS = common
            .comment("Enable the DO NOT USE overpowered bomb variants in creative tabs and crafting")
            .define("enableDoNotUseVariants", true);
        ENABLE_CRAFTING = common
            .comment("Enable crafting recipes for bombs (disable for creative-only servers)")
            .define("enableCrafting", true);
        common.pop();

        common.push("explosions");
        MAX_EXPLOSION_RADIUS = common
            .comment("Maximum explosion destruction radius in blocks. Values above 64 may cause lag on large bombs.")
            .defineInRange("maxExplosionRadius", 64, 10, 128);
        common.pop();

        COMMON_SPEC = common.build();

        // Client config
        ModConfigSpec.Builder client = new ModConfigSpec.Builder();

        client.push("display");
        SHOW_INFOGRAPHIC = client
            .comment("Show the welcome infographic on first world load")
            .define("showInfoGraphic", true);
        client.pop();

        CLIENT_SPEC = client.build();
    }
}
