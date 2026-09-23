package dev.acuario22.cgs.client;

import dev.acuario22.cgs.CopperGolemSorter;
import dev.acuario22.cgs.client.model.CopperGolemModel;
import dev.acuario22.cgs.client.render.CopperChestRenderer;
import dev.acuario22.cgs.client.render.CopperGolemRenderer;
import dev.acuario22.cgs.registry.ModBlockEntities;
import dev.acuario22.cgs.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CopperGolemSorter.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {}

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CopperGolemModel.LAYER_LOCATION, CopperGolemModel::createBodyLayer);
        event.registerLayerDefinition(CopperChestRenderer.LAYER_LOCATION, CopperChestRenderer::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.COPPER_GOLEM.get(), CopperGolemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.COPPER_CHEST.get(), CopperChestRenderer::new);
    }
}
