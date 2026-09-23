package dev.acuario22.cgs.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.acuario22.cgs.CopperGolemSorter;
import dev.acuario22.cgs.block.CopperChestBlock;
import dev.acuario22.cgs.block.entity.CopperChestBlockEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public final class CopperChestRenderer implements BlockEntityRenderer<CopperChestBlockEntity> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(CopperGolemSorter.MOD_ID, "copper_chest"), "main");

    private static final ResourceLocation[] TEXTURES = {
            new ResourceLocation(CopperGolemSorter.MOD_ID, "textures/entity/chest/copper_stage_0.png"),
            new ResourceLocation(CopperGolemSorter.MOD_ID, "textures/entity/chest/copper_stage_1.png"),
            new ResourceLocation(CopperGolemSorter.MOD_ID, "textures/entity/chest/copper_stage_2.png"),
            new ResourceLocation(CopperGolemSorter.MOD_ID, "textures/entity/chest/copper_stage_3.png")
    };

    private final ModelPart root;

    public CopperChestRenderer(BlockEntityRendererProvider.Context context) {
        this.root = context.bakeLayer(LAYER_LOCATION);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild(
                "bottom",
                CubeListBuilder.create()
                        .texOffs(0, 19)
                        .addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F),
                PartPose.ZERO
        );

        root.addOrReplaceChild(
                "lid",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F),
                PartPose.offset(0.0F, 9.0F, 1.0F)
        );

        root.addOrReplaceChild(
                "lock",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(7.0F, -1.0F, 0.0F, 2.0F, 4.0F, 1.0F),
                PartPose.offset(0.0F, 8.0F, 0.0F)
        );

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void render(CopperChestBlockEntity chest, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BlockState state = chest.getBlockState();
        int stage = state.hasProperty(CopperChestBlock.OXIDATION)
                ? Math.max(0, Math.min(3, state.getValue(CopperChestBlock.OXIDATION)))
                : 0;

        float rotation = state.hasProperty(CopperChestBlock.FACING)
                ? -state.getValue(CopperChestBlock.FACING).toYRot()
                : 0.0F;

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.translate(-0.5D, -0.5D, -0.5D);

        // Vanilla chest model space.
        poseStack.translate(0.0D, 1.0D, 1.0D);
        poseStack.scale(1.0F, -1.0F, -1.0F);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(TEXTURES[stage]));
        root.render(poseStack, consumer, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
