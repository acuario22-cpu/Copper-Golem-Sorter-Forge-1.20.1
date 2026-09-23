package dev.acuario22.cgs.registry;

import dev.acuario22.cgs.CopperGolemSorter;
import dev.acuario22.cgs.block.entity.CopperChestBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CopperGolemSorter.MOD_ID);

    public static final RegistryObject<BlockEntityType<CopperChestBlockEntity>> COPPER_CHEST =
            BLOCK_ENTITIES.register("copper_chest", () ->
                    BlockEntityType.Builder.of(CopperChestBlockEntity::new, ModBlocks.COPPER_CHEST.get()).build(null));

    private ModBlockEntities() {}
}
