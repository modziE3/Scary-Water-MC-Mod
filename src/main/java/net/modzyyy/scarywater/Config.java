package net.modzyyy.scarywater;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

    private static final ModConfigSpec.Builder BUILDER =
            new ModConfigSpec.Builder();

    // Swimming bob-height settings
    public static final ModConfigSpec.BooleanValue ENABLED =
            BUILDER
                    .comment("Whether the scary water jump restriction is enabled.")
                    .define("realisticSwimmingEnabled", true);

    public static final ModConfigSpec.DoubleValue ALLOWED_EYE_HEIGHT =
            BUILDER
                    .comment("How far above the water surface the player's eyes can be while jumping.")
                    .defineInRange("allowedEyeHeight", 0.30, 0.0, 10.0);

    // Swimming stamina settings
    public static final ModConfigSpec.BooleanValue STAMINA_ENABLED =
            BUILDER
                    .comment("Whether swimming stamina is enabled.")
                    .define("staminaEnabled", true);

    public static final ModConfigSpec.DoubleValue MAX_STAMINA =
            BUILDER
                    .comment("Maximum swimming stamina.")
                    .defineInRange("maxStamina", 100.0, 1.0, 10000.0);

    public static final ModConfigSpec.DoubleValue BOBBING_DRAIN =
            BUILDER
                    .comment("Stamina drained per second while bobbing/treading water.")
                    .defineInRange("bobbingDrain", 2.0, 0.0, 1000.0);

    public static final ModConfigSpec.DoubleValue SWIMMING_DRAIN =
            BUILDER
                    .comment("Stamina drained per second while actively swimming.")
                    .defineInRange("swimmingDrain", 10.0, 0.0, 1000.0);

    public static final ModConfigSpec.DoubleValue STAMINA_REGEN =
            BUILDER
                    .comment("Stamina regained per second while out of water.")
                    .defineInRange(
                            "swimmingStaminaRegeneration",
                            15.0,
                            0.1,
                            2000.0
                    );

    public static final ModConfigSpec.ConfigValue<String> STAMINA_COLOR =
            BUILDER
                    .comment("Colour of the stamina bar in hexadecimal RGB format, e.g. #55FF55.")
                    .define("staminaColor", "#03C0FF");

    static final ModConfigSpec SPEC =
            BUILDER.build();
}