package dev.acuario22.cgs.registry;

import dev.acuario22.cgs.CopperGolemSorter;
import dev.acuario22.cgs.block.CopperChestBlock;
import dev.acuario22.cgs.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CopperGolemSorter.MOD_ID);

    public static final RegistryObject<Block> COPPER_CHEST = BLOCKS.register("copper_chest",
            () -> new CopperChestBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .randomTicks()
                    .noOcclusion()));

    public static final RegistryObject<Block> COPPER_GOLEM_STATUE = BLOCKS.register("copper_golem_statue",
            () -> new CopperGolemStatueBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(3.5F, 6.0F)
                    .sound(SoundType.COPPER)
                    .randomTicks()
                    .noOcclusion()));

    private ModBlocks() {}
}
