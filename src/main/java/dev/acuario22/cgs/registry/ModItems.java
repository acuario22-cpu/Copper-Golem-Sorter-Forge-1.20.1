package dev.acuario22.cgs.registry;

import dev.acuario22.cgs.CopperGolemSorter;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
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

    // Intentionally no spawn egg. The intended survival/JEI path is the Copper Chest
    // plus the copper-block + carved-pumpkin construction.
    private ModItems() {}
}
