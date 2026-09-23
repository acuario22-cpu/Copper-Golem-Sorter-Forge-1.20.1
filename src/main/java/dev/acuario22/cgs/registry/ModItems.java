package dev.acuario22.cgs.registry;

import dev.acuario22.cgs.CopperGolemSorter;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CopperGolemSorter.MOD_ID);

    public static final RegistryObject<Item> COPPER_CHEST = ITEMS.register("copper_chest",
            () -> new BlockItem(ModBlocks.COPPER_CHEST.get(), new Item.Properties()));

    public static final RegistryObject<Item> COPPER_GOLEM_STATUE = ITEMS.register("copper_golem_statue",
            () -> new BlockItem(ModBlocks.COPPER_GOLEM_STATUE.get(), new Item.Properties()));

    public static final RegistryObject<Item> COPPER_GOLEM_SPAWN_EGG = ITEMS.register("copper_golem_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.COPPER_GOLEM, 0xB87333, 0x4D9786, new Item.Properties()));

    private ModItems() {}
}
