package dev.acuario22.cgs.item;

import dev.acuario22.cgs.client.render.CopperChestItemRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public final class CopperChestItem extends BlockItem {
    public CopperChestItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer renderer;

            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                // Forge can request client item extensions while Minecraft is still
                // bootstrapping. Constructing the BEWLR eagerly here may access the
                // block-entity dispatcher/entity models before they exist and crash
                // the game during "Initializing game". Create it only when the
                // renderer is actually requested.
                if (renderer == null) {
                    renderer = new CopperChestItemRenderer(CopperChestItem.this);
                }
                return renderer;
            }
        });
    }
}
