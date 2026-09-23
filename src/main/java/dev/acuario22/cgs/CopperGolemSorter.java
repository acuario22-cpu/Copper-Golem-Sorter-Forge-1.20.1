package dev.acuario22.cgs;

import dev.acuario22.cgs.registry.ModBlockEntities;
import dev.acuario22.cgs.registry.ModBlocks;
import dev.acuario22.cgs.registry.ModEntities;
import dev.acuario22.cgs.registry.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CopperGolemSorter.MOD_ID)
public final class CopperGolemSorter {
    public static final String MOD_ID = "copper_golem_sorter";

    public CopperGolemSorter() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModEntities.ENTITIES.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CgsConfig.SPEC);
        MinecraftForge.EVENT_BUS.register(CommonEvents.class);
    }
}
