package dev.acuario22.cgs;

import net.minecraftforge.common.ForgeConfigSpec;

public final class CgsConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue SEARCH_RADIUS;
    public static final ForgeConfigSpec.IntValue VERTICAL_SEARCH_RADIUS;
    public static final ForgeConfigSpec.IntValue MAX_CONTAINERS_PER_SEARCH;
    public static final ForgeConfigSpec.IntValue MAX_ITEMS_PER_TRIP;
    public static final ForgeConfigSpec.IntValue IDLE_SCAN_INTERVAL_TICKS;
    public static final ForgeConfigSpec.IntValue FAILED_SEARCH_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue PATH_TIMEOUT_TICKS;
    public static final ForgeConfigSpec.BooleanValue ALLOW_COMPLETELY_EMPTY_DESTINATIONS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_OXIDATION;
    public static final ForgeConfigSpec.IntValue OXIDATION_STAGE_TICKS;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("sorting");
        SEARCH_RADIUS = b.comment("Horizontal radius. Loaded chunks only.").defineInRange("searchRadius", 24, 4, 64);
        VERTICAL_SEARCH_RADIUS = b.defineInRange("verticalSearchRadius", 8, 2, 32);
        MAX_CONTAINERS_PER_SEARCH = b.defineInRange("maxContainersPerSearch", 64, 8, 256);
        MAX_ITEMS_PER_TRIP = b.defineInRange("maxItemsPerTrip", 16, 1, 64);
        IDLE_SCAN_INTERVAL_TICKS = b.defineInRange("idleScanIntervalTicks", 40, 10, 400);
        FAILED_SEARCH_COOLDOWN_TICKS = b.defineInRange("failedSearchCooldownTicks", 100, 20, 1200);
        PATH_TIMEOUT_TICKS = b.defineInRange("pathTimeoutTicks", 240, 60, 1200);
        ALLOW_COMPLETELY_EMPTY_DESTINATIONS = b.define("allowCompletelyEmptyDestinations", true);
        b.pop();
        b.push("oxidation");
        ENABLE_OXIDATION = b.define("enableOxidation", true);
        OXIDATION_STAGE_TICKS = b.comment("72000 ticks is about one hour at 20 TPS.")
                .defineInRange("oxidationStageTicks", 72000, 1200, 2400000);
        b.pop();
        SPEC = b.build();
    }

    private CgsConfig() {}
}
