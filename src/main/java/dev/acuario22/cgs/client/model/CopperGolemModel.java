package dev.acuario22.cgs.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.acuario22.cgs.CopperGolemSorter;
import dev.acuario22.cgs.entity.CopperGolemEntity;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public final class CopperGolemModel extends HierarchicalModel<CopperGolemEntity> implements ArmedModel {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(CopperGolemSorter.MOD_ID, "copper_golem"), "main");

    private CopperGolemEntity entity;
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public CopperGolemModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = body.getChild("head");
        this.rightArm = body.getChild("right_arm");
        this.leftArm = body.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 15)
                        .addBox(-4.0F, -6.0F, -3.0F, 8.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -5.0F, 0.0F)
        );

        PartDefinition head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -5.0F, -5.0F, 8.0F, 5.0F, 10.0F, new CubeDeformation(0.015F))
                        .texOffs(56, 0)
                        .addBox(-1.0F, -2.0F, -6.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(37, 8)
                        .addBox(-1.0F, -9.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.015F))
                        .texOffs(37, 0)
                        .addBox(-2.0F, -13.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.015F)),
                PartPose.offset(0.0F, -6.0F, 0.0F)
        );


        body.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create()
                        .texOffs(36, 16)
                        .addBox(-3.0F, -1.0F, -2.0F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-4.0F, -6.0F, 0.0F)
        );

        body.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create()
                        .texOffs(50, 16)
                        .addBox(0.0F, -1.0F, -2.0F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(4.0F, -6.0F, 0.0F)
        );

        root.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create()
                        .texOffs(0, 27)
                        .addBox(-4.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -5.0F, 0.0F)
        );

        root.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create()
                        .texOffs(16, 27)
                        .addBox(0.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -5.0F, 0.0F)
        );

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(CopperGolemEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.entity = entity;

        head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        head.xRot = headPitch * ((float)Math.PI / 180F);

        float spinPhase = (ageInTicks + entity.getId() * 17.0F) % 240.0F;
        if (spinPhase < 20.0F) {
            head.yRot += (spinPhase / 20.0F) * Mth.TWO_PI;
        }

        rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.05F * limbSwingAmount;
        leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.05F * limbSwingAmount;
        rightLeg.yRot = 0.0F;
        leftLeg.yRot = 0.0F;
        rightLeg.zRot = 0.0F;
        leftLeg.zRot = 0.0F;

        rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 0.75F * limbSwingAmount;
        leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 0.75F * limbSwingAmount;
        rightArm.yRot = 0.0F;
        leftArm.yRot = 0.0F;
        rightArm.zRot = 0.0F;
        leftArm.zRot = 0.0F;

        if (!entity.getMainHandItem().isEmpty()) {
            // Modern Copper Golem carrying pose: bring the arm forward and slightly inward.
            rightArm.xRot = Math.min(rightArm.xRot, -0.87266463F);
            rightArm.yRot = -0.1134464F;
            rightArm.zRot = -0.064577185F;
        }
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
        // The arm is nested under body, so all parent transforms must be applied.
        // The scale/offset mirrors the modern Copper Golem carry transform and keeps
        // the rendered stack close to the hand instead of floating a block away.
        root.translateAndRotate(poseStack);
        body.translateAndRotate(poseStack);

        ModelPart activeArm = arm == HumanoidArm.RIGHT ? rightArm : leftArm;
        activeArm.translateAndRotate(poseStack);

        poseStack.scale(0.55F, 0.55F, 0.55F);
        poseStack.translate(-0.125F, 0.3125F, -0.1875F);
    }
}
