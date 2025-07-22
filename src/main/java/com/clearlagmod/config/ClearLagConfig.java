package com.clearlagmod.config;

import org.apache.commons.lang3.tuple.Pair;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class ClearLagConfig {
    public static final ClearLagConfig CONFIG;
    public static final ModConfigSpec SPEC;

    static {

        Pair<ClearLagConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder()
                        .configure(ClearLagConfig::new);
        CONFIG = pair.getLeft();
        SPEC   = pair.getRight();
    }

    public final ModConfigSpec.IntValue clearIntervalSeconds;

    public final ModConfigSpec.ConfigValue<List<? extends Integer>> warningSeconds;

    private ClearLagConfig(ModConfigSpec.Builder builder) {
        builder.push("general");

        clearIntervalSeconds = builder
                .comment("Interval between cleanings (in seconds)")
                .translation("clearlagmod.config.clearIntervalSeconds")
                .defineInRange("clearIntervalSeconds", 1800, 60, 86400);

        warningSeconds = builder
                .comment("Time for warnings before cleaning (in seconds)")
                .translation("clearlagmod.config.warningSeconds")
                .defineList("warningSeconds",
                        List.of(300, 60, 10),
                        o -> o instanceof Integer);

        builder.pop();
    }
}
