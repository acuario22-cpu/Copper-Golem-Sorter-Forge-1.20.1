package dev.acuario22.cgs;

import dev.acuario22.cgs.entity.CopperGolemEntity;
import dev.acuario22.cgs.registry.ModEntities;
import dev.acuario22.cgs.registry.ModItems;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CopperGolemSorter.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModEvents {
    private ModEvents() {}

    @SubscribeEvent
    public static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.COPPER_GOLEM.get(),
                CopperGolemEntity.createAttributes()
                        .add(Attributes.MAX_HEALTH, 20.0D)
                        .add(Attributes.MOVEMENT_SPEED, 0.25D)
                        .add(Attributes.FOLLOW_RANGE, 32.0D)
                        .build());
    }

    @SubscribeEvent
    public static void onCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.COPPER_CHEST.get());
            event.accept(ModItems.COPPER_GOLEM_STATUE.get());
        }
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.COPPER_GOLEM_SPAWN_EGG.get());
        }
    }
}
