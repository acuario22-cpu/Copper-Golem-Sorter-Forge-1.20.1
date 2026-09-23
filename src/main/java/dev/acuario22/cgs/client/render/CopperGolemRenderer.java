package dev.acuario22.cgs.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.acuario22.cgs.CopperGolemSorter;
import dev.acuario22.cgs.client.model.CopperGolemModel;
import dev.acuario22.cgs.entity.CopperGolemEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public final class CopperGolemRenderer extends MobRenderer<CopperGolemEntity, CopperGolemModel> {
    private static final ResourceLocation[] TEXTURES = {
            new ResourceLocation(CopperGolemSorter.MOD_ID, "textures/entity/copper_golem_stage_0.png"),
            new ResourceLocation(CopperGolemSorter.MOD_ID, "textures/entity/copper_golem_stage_1.png"),
            new ResourceLocation(CopperGolemSorter.MOD_ID, "textures/entity/copper_golem_stage_2.png"),
            new ResourceLocation(CopperGolemSorter.MOD_ID, "textures/entity/copper_golem_stage_3.png")
    };

    public CopperGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new CopperGolemModel(context.bakeLayer(CopperGolemModel.LAYER_LOCATION)), 0.5F);
        addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(CopperGolemEntity entity) {
        return TEXTURES[Math.max(0, Math.min(TEXTURES.length - 1, entity.getOxidationStage()))];
    }

    @Override
    protected void scale(CopperGolemEntity entity, PoseStack poseStack, float partialTickTime) {
        super.scale(entity, poseStack, partialTickTime);
        poseStack.translate(0.0D, 1.5D, 0.0D);
    }
}
