package dev.acuario22.cgs.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.acuario22.cgs.item.CopperChestItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public final class CopperChestItemRenderer extends BlockEntityWithoutLevelRenderer {
    private final BlockEntity chest;
    private final BlockEntityRenderDispatcher dispatcher;

    public CopperChestItemRenderer(CopperChestItem item) {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());

        Block block = item.getBlock();
        this.chest = Objects.requireNonNull(
                ((EntityBlock) block).newBlockEntity(BlockPos.ZERO, block.defaultBlockState())
        );
        this.dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {
        // Render the exact same stage-0 block entity used in the world.
        // This guarantees that the held item, inventory icon and JEI entry
        // use the unoxidized Copper Chest texture instead of the old yellow model.
        dispatcher.renderItem(chest, poseStack, buffer, packedLight, packedOverlay);
    }
}
