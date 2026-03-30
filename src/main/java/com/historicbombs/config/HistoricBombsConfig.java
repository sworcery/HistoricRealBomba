package com.historicbombs.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class HistoricBombsConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue SHOW_INFOGRAPHIC;
    public static final ModConfigSpec.BooleanValue ENABLE_DNU_VARIANTS;
    public static final ModConfigSpec.IntValue MAX_EXPLOSION_RADIUS;
    public static final ModConfigSpec.BooleanValue ENABLE_CRAFTING;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Historic Bombs Mod Configuration");

        builder.push("general");
        SHOW_INFOGRAPHIC = builder
            .comment("Show the welcome infographic on first world load")
            .define("showInfoGraphic", true);
        ENABLE_DNU_VARIANTS = builder
            .comment("Enable the DO NOT USE overpowered bomb variants")
            .define("enableDoNotUseVariants", true);
        ENABLE_CRAFTING = builder
            .comment("Enable crafting recipes for bombs (disable for creative-only servers)")
            .define("enableCrafting", true);
        builder.pop();

        builder.push("explosions");
        MAX_EXPLOSION_RADIUS = builder
            .comment("Maximum explosion destruction radius in blocks (higher = more lag)")
            .defineInRange("maxExplosionRadius", 200, 10, 1000);
        builder.pop();

        SPEC = builder.build();
    }
}
