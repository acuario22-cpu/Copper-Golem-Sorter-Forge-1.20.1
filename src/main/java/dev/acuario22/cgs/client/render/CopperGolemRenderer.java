package dev.acuario22.cgs.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.acuario22.cgs.CopperGolemSorter;
import dev.acuario22.cgs.entity.CopperGolemEntity;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public final class CopperGolemRenderer extends MobRenderer<CopperGolemEntity, IronGolemModel<CopperGolemEntity>> {
    private static final ResourceLocation[] TEXTURES = {
            new ResourceLocation(CopperGolemSorter.MOD_ID, "textures/entity/copper_golem_stage_0.png"),
            new ResourceLocation(CopperGolemSorter.MOD_ID, "textures/entity/copper_golem_stage_1.png"),
            new ResourceLocation(CopperGolemSorter.MOD_ID, "textures/entity/copper_golem_stage_2.png")
    };

    public CopperGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new IronGolemModel<>(context.bakeLayer(ModelLayers.IRON_GOLEM)), 0.45F);
    }

    @Override
    public ResourceLocation getTextureLocation(CopperGolemEntity entity) {
        return TEXTURES[Math.max(0, Math.min(TEXTURES.length - 1, entity.getOxidationStage()))];
    }

    @Override
    protected void scale(CopperGolemEntity entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(0.66F, 0.66F, 0.66F);
    }
}
