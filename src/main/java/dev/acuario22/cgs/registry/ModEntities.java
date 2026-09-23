package dev.acuario22.cgs.registry;

import dev.acuario22.cgs.CopperGolemSorter;
import dev.acuario22.cgs.entity.CopperGolemEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CopperGolemSorter.MOD_ID);

    public static final RegistryObject<EntityType<CopperGolemEntity>> COPPER_GOLEM =
            ENTITIES.register("copper_golem", () ->
                    EntityType.Builder.<CopperGolemEntity>of(CopperGolemEntity::new, MobCategory.CREATURE)
                            .sized(0.75F, 1.45F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build("copper_golem"));

    private ModEntities() {}
}
