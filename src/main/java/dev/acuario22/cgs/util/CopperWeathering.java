package dev.acuario22.cgs.util;

import dev.acuario22.cgs.block.CopperChestBlock;
import dev.acuario22.cgs.block.CopperGolemStatueBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public final class CopperWeathering {
    // Vanilla ChangeOverTimeBlock random-tick gate used by copper in 1.20.1.
    private static final float VANILLA_RANDOM_TICK_CHANCE = 0.05688889F;
    private static final int SCAN_DISTANCE = 4;

    private CopperWeathering() {}

    public static void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random,
                                  IntegerProperty oxidationProperty, BooleanProperty waxedProperty) {
        if (state.getValue(waxedProperty)) return;

        int current = state.getValue(oxidationProperty);
        if (current >= 3 || random.nextFloat() >= VANILLA_RANDOM_TICK_CHANCE) return;

        int sameAge = 0;
        int olderAge = 0;

        for (BlockPos scanPos : BlockPos.withinManhattan(pos, SCAN_DISTANCE, SCAN_DISTANCE, SCAN_DISTANCE)) {
            if (scanPos.equals(pos)) continue;
            if (scanPos.distManhattan(pos) > SCAN_DISTANCE) continue;

            int neighbourAge = oxidationAge(level.getBlockState(scanPos));
            if (neighbourAge < 0) continue;

            // This mirrors vanilla copper: a less-weathered nearby copper block prevents
            // this block from advancing during this attempt.
            if (neighbourAge < current) return;

            if (neighbourAge > current) {
                olderAge++;
            } else {
                sameAge++;
            }
        }

        float ratio = (olderAge + 1.0F) / (olderAge + sameAge + 1.0F);
        float chanceModifier = current == 0 ? 0.75F : 1.0F;
        float advanceChance = ratio * ratio * chanceModifier;

        if (random.nextFloat() < advanceChance) {
            level.setBlock(pos, state.setValue(oxidationProperty, current + 1), 3);
        }
    }

    private static int oxidationAge(BlockState state) {
        if (state.getBlock() instanceof CopperChestBlock) {
            return state.getValue(CopperChestBlock.OXIDATION);
        }
        if (state.getBlock() instanceof CopperGolemStatueBlock) {
            return state.getValue(CopperGolemStatueBlock.OXIDATION);
        }
        if (state.getBlock() instanceof WeatheringCopper copper) {
            return copper.getAge().ordinal();
        }
        return -1;
    }
}
