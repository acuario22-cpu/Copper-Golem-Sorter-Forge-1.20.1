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
    public static final ForgeConfigSpec.IntValue STATUE_AVERAGE_TICKS;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();

        b.push("sorting");
        SEARCH_RADIUS = b.comment("Official Copper Golem horizontal search radius.")
                .defineInRange("searchRadius", 32, 4, 64);
        VERTICAL_SEARCH_RADIUS = b.comment("Official Copper Golem vertical search radius.")
                .defineInRange("verticalSearchRadius", 8, 2, 32);
        MAX_CONTAINERS_PER_SEARCH = b.comment("Maximum chest candidates considered in one search cycle.")
                .defineInRange("maxContainersPerSearch", 10, 1, 64);
        MAX_ITEMS_PER_TRIP = b.comment("Maximum number of items carried at once.")
                .defineInRange("maxItemsPerTrip", 16, 1, 64);
        IDLE_SCAN_INTERVAL_TICKS = b.comment("Delay between ordinary idle source scans.")
                .defineInRange("idleScanIntervalTicks", 40, 10, 400);
        FAILED_SEARCH_COOLDOWN_TICKS = b.comment("Official failed-search delay: 7 seconds = 140 ticks.")
                .defineInRange("failedSearchCooldownTicks", 140, 20, 1200);
        PATH_TIMEOUT_TICKS = b.comment("Maximum time spent trying to reach one chest.")
                .defineInRange("pathTimeoutTicks", 240, 60, 1200);
        ALLOW_COMPLETELY_EMPTY_DESTINATIONS = b.comment("Official behavior allows an empty chest to accept a new item type.")
                .define("allowCompletelyEmptyDestinations", true);
        b.pop();

        b.push("oxidation");
        ENABLE_OXIDATION = b.define("enableOxidation", true);
        OXIDATION_STAGE_TICKS = b.comment("Approximate active ticks per golem oxidation stage.")
                .defineInRange("oxidationStageTicks", 72000, 1200, 2400000);
        STATUE_AVERAGE_TICKS = b.comment("Average ticks before a fully oxidized unwaxed golem becomes a statue. Conversion can happen immediately.")
                .defineInRange("statueAverageTicks", 200, 1, 12000);
        b.pop();

        SPEC = b.build();
    }

    private CgsConfig() {}
}
