package dev.acuario22.cgs.util;

import dev.acuario22.cgs.block.entity.CopperChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class InventoryAccess {
    public static final String LOCK_TAG = "CopperGolemSorterLocked";

    private static final List<ResourceLocation> GENERATIONS_CHESTS = List.of(
            new ResourceLocation("generations_core", "pokeball_chest"),
            new ResourceLocation("generations_core", "greatball_chest"),
            new ResourceLocation("generations_core", "ultraball_chest"),
            new ResourceLocation("generations_core", "masterball_chest"),
            new ResourceLocation("generation_core", "pokeball_chest"),
            new ResourceLocation("generation_core", "greatball_chest"),
            new ResourceLocation("generation_core", "ultraball_chest"),
            new ResourceLocation("generation_core", "masterball_chest")
    );

    private InventoryAccess() {}

    public static List<BlockEntity> nearbyBlockEntities(ServerLevel level, BlockPos origin,
                                                         int radius, int verticalRadius) {
        List<BlockEntity> result = new ArrayList<>();
        int minChunkX = (origin.getX() - radius) >> 4;
        int maxChunkX = (origin.getX() + radius) >> 4;
        int minChunkZ = (origin.getZ() - radius) >> 4;
        int maxChunkZ = (origin.getZ() + radius) >> 4;
        long radiusSq = (long) radius * radius;

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                if (!level.hasChunk(cx, cz)) continue;
                for (BlockEntity be : level.getChunk(cx, cz).getBlockEntities().values()) {
                    BlockPos pos = be.getBlockPos();
                    if (Math.abs(pos.getY() - origin.getY()) > verticalRadius) continue;
                    long dx = pos.getX() - origin.getX();
                    long dz = pos.getZ() - origin.getZ();
                    if (dx * dx + dz * dz > radiusSq) continue;
                    result.add(be);
                }
            }
        }

        result.sort(Comparator.comparingDouble(be -> be.getBlockPos().distSqr(origin)));
        return result;
    }

    public static boolean isLocked(BlockEntity be) {
        return be.getPersistentData().getBoolean(LOCK_TAG);
    }

    public static void setLocked(BlockEntity be, boolean locked) {
        be.getPersistentData().putBoolean(LOCK_TAG, locked);
        be.setChanged();
    }

    public static boolean isSupported(BlockEntity be) {
        if (be instanceof CopperChestBlockEntity) return true;

        ResourceLocation id = net.minecraftforge.registries.ForgeRegistries.BLOCKS.getKey(be.getBlockState().getBlock());
        if (id == null) return false;

        String path = id.getPath();
        boolean chestLike = path.contains("chest") || isGenerationsChest(be);
        if (!chestLike) return false;

        return be instanceof Container || getHandler(be) != null;
    }

    public static boolean isGenerationsChest(BlockEntity be) {
        ResourceLocation id = net.minecraftforge.registries.ForgeRegistries.BLOCKS.getKey(be.getBlockState().getBlock());
        return id != null && GENERATIONS_CHESTS.contains(id);
    }

    public static boolean isCopperInput(BlockEntity be) {
        return be instanceof CopperChestBlockEntity;
    }

    public static int matchingCount(BlockEntity be, ItemStack wanted) {
        IItemHandler handler = getHandler(be);
        if (handler != null) {
            int result = 0;
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                ItemStack stack = handler.getStackInSlot(slot);
                if (!stack.isEmpty() && ItemStack.isSameItem(stack, wanted)) result += stack.getCount();
            }
            return result;
        }

        if (be instanceof Container container) {
            int result = 0;
            for (int slot = 0; slot < container.getContainerSize(); slot++) {
                ItemStack stack = container.getItem(slot);
                if (!stack.isEmpty() && ItemStack.isSameItem(stack, wanted)) result += stack.getCount();
            }
            return result;
        }
        return 0;
    }

    public static boolean isCompletelyEmpty(BlockEntity be) {
        IItemHandler handler = getHandler(be);
        if (handler != null) {
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                if (!handler.getStackInSlot(slot).isEmpty()) return false;
            }
            return true;
        }
        return be instanceof Container container && container.isEmpty();
    }

    public static boolean canAccept(BlockEntity be, ItemStack stack) {
        ItemStack probe = stack.copy();
        probe.setCount(1);

        IItemHandler handler = getHandler(be);
        if (handler != null) {
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                if (handler.insertItem(slot, probe, true).isEmpty()) return true;
            }
            return false;
        }

        if (be instanceof Container container) {
            for (int slot = 0; slot < container.getContainerSize(); slot++) {
                ItemStack current = container.getItem(slot);
                if (!container.canPlaceItem(slot, probe)) continue;
                if (current.isEmpty()) return true;
                if (ItemStack.isSameItemSameTags(current, probe)
                        && current.getCount() < Math.min(current.getMaxStackSize(), container.getMaxStackSize())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static ItemStack insert(BlockEntity be, ItemStack input) {
        if (input.isEmpty()) return ItemStack.EMPTY;

        IItemHandler handler = getHandler(be);
        if (handler != null) {
            ItemStack remainder = input.copy();
            for (int pass = 0; pass < 2 && !remainder.isEmpty(); pass++) {
                for (int slot = 0; slot < handler.getSlots() && !remainder.isEmpty(); slot++) {
                    ItemStack current = handler.getStackInSlot(slot);
                    boolean exactStack = !current.isEmpty() && ItemStack.isSameItemSameTags(current, remainder);
                    if ((pass == 0) != exactStack) continue;
                    remainder = handler.insertItem(slot, remainder, false);
                }
            }
            return remainder;
        }

        if (be instanceof Container container) {
            ItemStack remainder = input.copy();

            for (int slot = 0; slot < container.getContainerSize() && !remainder.isEmpty(); slot++) {
                ItemStack current = container.getItem(slot);
                if (current.isEmpty() || !ItemStack.isSameItemSameTags(current, remainder)) continue;
                if (!container.canPlaceItem(slot, remainder)) continue;

                int limit = Math.min(container.getMaxStackSize(), current.getMaxStackSize());
                int room = limit - current.getCount();
                if (room <= 0) continue;

                int moved = Math.min(room, remainder.getCount());
                current.grow(moved);
                remainder.shrink(moved);
            }

            for (int slot = 0; slot < container.getContainerSize() && !remainder.isEmpty(); slot++) {
                ItemStack current = container.getItem(slot);
                if (!current.isEmpty() || !container.canPlaceItem(slot, remainder)) continue;

                int moved = Math.min(remainder.getCount(),
                        Math.min(container.getMaxStackSize(), remainder.getMaxStackSize()));
                ItemStack placed = remainder.copy();
                placed.setCount(moved);
                container.setItem(slot, placed);
                remainder.shrink(moved);
            }

            container.setChanged();
            return remainder;
        }

        return input.copy();
    }

    public static ItemStack extractFirst(CopperChestBlockEntity chest, int maxAmount) {
        for (int slot = 0; slot < chest.getContainerSize(); slot++) {
            ItemStack stack = chest.getItem(slot);
            if (stack.isEmpty()) continue;
            return chest.removeItem(slot, Math.min(maxAmount, stack.getCount()));
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    private static IItemHandler getHandler(BlockEntity be) {
        return be.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElse(null);
    }
}
