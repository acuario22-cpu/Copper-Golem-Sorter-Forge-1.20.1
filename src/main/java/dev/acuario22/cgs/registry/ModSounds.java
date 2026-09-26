package dev.acuario22.cgs.registry;

import dev.acuario22.cgs.CopperGolemSorter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CopperGolemSorter.MOD_ID);

    public static final RegistryObject<SoundEvent> AMBIENT = register("entity.copper_golem.ambient");
    public static final RegistryObject<SoundEvent> STEP = register("entity.copper_golem.step");
    public static final RegistryObject<SoundEvent> HURT = register("entity.copper_golem.hurt");
    public static final RegistryObject<SoundEvent> DEATH = register("entity.copper_golem.death");
    public static final RegistryObject<SoundEvent> PICKUP = register("entity.copper_golem.pickup");
    public static final RegistryObject<SoundEvent> DEPOSIT = register("entity.copper_golem.deposit");
    public static final RegistryObject<SoundEvent> WORK = register("entity.copper_golem.work");
    public static final RegistryObject<SoundEvent> OXIDIZE = register("entity.copper_golem.oxidize");
    public static final RegistryObject<SoundEvent> BECOME_STATUE = register("entity.copper_golem.become_statue");
    public static final RegistryObject<SoundEvent> REACTIVATE = register("entity.copper_golem.reactivate");
    public static final RegistryObject<SoundEvent> SPAWN = register("entity.copper_golem.spawn");

    private ModSounds() {}

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(
                new ResourceLocation(CopperGolemSorter.MOD_ID, name)));
    }
}
