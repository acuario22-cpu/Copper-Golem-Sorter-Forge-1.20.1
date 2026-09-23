package dev.acuario22.cgs.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.acuario22.cgs.CopperGolemSorter;
import dev.acuario22.cgs.block.CopperGolemStatueBlock;
import dev.acuario22.cgs.block.entity.CopperGolemStatueBlockEntity;
import dev.acuario22.cgs.client.model.CopperGolemModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public final class CopperGolemStatueRenderer implements BlockEntityRenderer<CopperGolemStatueBlockEntity> {
    private static final ResourceLocation[] TEXTURES = {
            new ResourceLocation(CopperGolemSorter.MOD_ID, "textures/entity/copper_golem_stage_0.png"),
            new ResourceLocation(CopperGolemSorter.MOD_ID, "textures/entity/copper_golem_stage_1.png"),
            new ResourceLocation(CopperGolemSorter.MOD_ID, "textures/entity/copper_golem_stage_2.png"),
            new ResourceLocation(CopperGolemSorter.MOD_ID, "textures/entity/copper_golem_stage_3.png")
    };

    private final CopperGolemModel model;

    public CopperGolemStatueRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new CopperGolemModel(context.bakeLayer(CopperGolemModel.LAYER_LOCATION));
    }

    @Override
    public void render(CopperGolemStatueBlockEntity statue, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BlockState state = statue.getBlockState();
        int stage = state.hasProperty(CopperGolemStatueBlock.OXIDATION)
                ? Math.max(0, Math.min(3, state.getValue(CopperGolemStatueBlock.OXIDATION)))
                : 0;

        model.root().getAllParts().forEach(ModelPart::resetPose);
        applyPose(state);

        float rotation = state.hasProperty(CopperGolemStatueBlock.FACING)
                ? -state.getValue(CopperGolemStatueBlock.FACING).toYRot()
                : 0.0F;

        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURES[stage]));
        model.root().render(poseStack, consumer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    private void applyPose(BlockState state) {
        if (!state.hasProperty(CopperGolemStatueBlock.POSE)) return;

        ModelPart root = model.root();
        ModelPart body = root.getChild("body");
        ModelPart head = body.getChild("head");
        ModelPart rightArm = body.getChild("right_arm");
        ModelPart leftArm = body.getChild("left_arm");
        ModelPart rightLeg = root.getChild("right_leg");
        ModelPart leftLeg = root.getChild("left_leg");

        switch (state.getValue(CopperGolemStatueBlock.POSE)) {
            case SITTING -> {
                body.y += 3.0F;
                head.xRot = 0.10F;
                rightArm.xRot = -1.05F;
                leftArm.xRot = -1.05F;
                rightLeg.xRot = -1.57F;
                leftLeg.xRot = -1.57F;
                rightLeg.z += 2.0F;
                leftLeg.z += 2.0F;
            }
            case RUNNING -> {
                body.zRot = -0.08F;
                head.xRot = 0.12F;
                rightArm.xRot = 1.00F;
                leftArm.xRot = -0.87F;
                rightLeg.xRot = -0.87F;
                leftLeg.xRot = 0.79F;
            }
            case STAR -> {
                rightArm.zRot = 1.92F;
                leftArm.zRot = -1.92F;
                rightLeg.zRot = 0.26F;
                leftLeg.zRot = -0.26F;
            }
            case STANDING -> {
                // Default baked pose is the vanilla standing pose.
            }
        }
    }
}
