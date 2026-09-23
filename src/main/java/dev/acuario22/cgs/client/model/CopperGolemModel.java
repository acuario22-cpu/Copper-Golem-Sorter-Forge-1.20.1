package dev.acuario22.cgs.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.acuario22.cgs.CopperGolemSorter;
import dev.acuario22.cgs.entity.CopperGolemEntity;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
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

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart flower;

    public CopperGolemModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.flower = head.getChild("flower");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -7.0F, -4.0F, 8.0F, 7.0F, 8.0F),
                PartPose.offset(0.0F, 10.0F, 0.0F));

        head.addOrReplaceChild("rod",
                CubeListBuilder.create()
                        .texOffs(40, 0)
                        .addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F)
                        .texOffs(40, 8)
                        .addBox(-3.0F, -5.5F, -0.5F, 6.0F, 1.0F, 1.0F),
                PartPose.offset(0.0F, -7.0F, 0.0F));

        head.addOrReplaceChild("flower",
                CubeListBuilder.create()
                        .texOffs(48, 0)
                        .addBox(1.5F, -2.0F, -1.0F, 3.0F, 3.0F, 2.0F),
                PartPose.offset(0.0F, -6.0F, 0.0F));

        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 16)
                        .addBox(-5.0F, -8.0F, -3.0F, 10.0F, 8.0F, 6.0F)
                        .texOffs(32, 16)
                        .addBox(-3.0F, -6.5F, -3.8F, 6.0F, 4.0F, 1.0F),
                PartPose.offset(0.0F, 18.0F, 0.0F));

        root.addOrReplaceChild("right_arm",
                CubeListBuilder.create()
                        .texOffs(0, 31)
                        .addBox(-3.0F, -1.0F, -2.0F, 3.0F, 8.0F, 4.0F),
                PartPose.offset(-5.0F, 11.0F, 0.0F));

        root.addOrReplaceChild("left_arm",
                CubeListBuilder.create()
                        .texOffs(14, 31)
                        .mirror()
                        .addBox(0.0F, -1.0F, -2.0F, 3.0F, 8.0F, 4.0F),
                PartPose.offset(5.0F, 11.0F, 0.0F));

        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create()
                        .texOffs(28, 31)
                        .addBox(-2.5F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F),
                PartPose.offset(-2.0F, 18.0F, 0.0F));

        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create()
                        .texOffs(44, 31)
                        .addBox(-1.5F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F),
                PartPose.offset(2.0F, 18.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(CopperGolemEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        head.xRot = headPitch * ((float)Math.PI / 180F);

        rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.2F * limbSwingAmount;
        leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.2F * limbSwingAmount;
        rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 0.8F * limbSwingAmount;
        leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 0.8F * limbSwingAmount;
        rightArm.zRot = 0.0F;
        leftArm.zRot = 0.0F;

        if (!entity.getMainHandItem().isEmpty()) {
            rightArm.xRot = -1.15F;
            rightArm.zRot = -0.12F;
        }

        flower.visible = entity.hasFlower();
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
        ModelPart part = arm == HumanoidArm.RIGHT ? rightArm : leftArm;
        part.translateAndRotate(poseStack);
        poseStack.translate(0.0D, 0.35D, -0.05D);
    }
}
