package dev.acuario22.cgs.block.entity;

import dev.acuario22.cgs.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class CopperGolemStatueBlockEntity extends BlockEntity {
    @Nullable
    private Component customName;

    public CopperGolemStatueBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COPPER_GOLEM_STATUE.get(), pos, state);
    }

    @Nullable
    public Component getCustomName() {
        return customName;
    }

    public void setCustomName(@Nullable Component name) {
        this.customName = name;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (customName != null) {
            tag.putString("CustomName", Component.Serializer.toJson(customName));
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("CustomName", 8)) {
            customName = Component.Serializer.fromJson(tag.getString("CustomName"));
        } else {
            customName = null;
        }
    }
}
