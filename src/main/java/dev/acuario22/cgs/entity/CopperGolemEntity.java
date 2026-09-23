package dev.acuario22.cgs.entity;

import dev.acuario22.cgs.CgsConfig;
import dev.acuario22.cgs.block.CopperGolemStatueBlock;
import dev.acuario22.cgs.block.entity.CopperChestBlockEntity;
import dev.acuario22.cgs.block.entity.CopperGolemStatueBlockEntity;
import dev.acuario22.cgs.registry.ModBlocks;
import dev.acuario22.cgs.util.InventoryAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CopperGolemEntity extends IronGolem {
    private static final EntityDataAccessor<Integer> DATA_OXIDATION =
            SynchedEntityData.defineId(CopperGolemEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_WAXED =
            SynchedEntityData.defineId(CopperGolemEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_FLOWER =
            SynchedEntityData.defineId(CopperGolemEntity.class, EntityDataSerializers.BOOLEAN);

    private final Map<ResourceLocation, BlockPos> destinationCache = new HashMap<>();

    private TaskPhase taskPhase = TaskPhase.IDLE;
    private BlockPos sourcePos;
    private BlockPos destinationPos;
    private int cooldown;
    private int pathTicks;
    private int oxidationProgress;

    public CopperGolemEntity(EntityType<? extends CopperGolemEntity> type, Level level) {
        super(type, level);
        setCanPickUpLoot(false);
        if (getNavigation() instanceof GroundPathNavigation groundNavigation) {
            groundNavigation.setCanOpenDoors(true);
        }
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new SortingGoal());
        // Allows sorting routes to pass through wooden doors. The goal opens
        // the door while crossing and closes it again afterward.
        goalSelector.addGoal(2, new OpenDoorGoal(this, true));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.65D, 0.001F));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_OXIDATION, 0);
        entityData.define(DATA_WAXED, false);
        entityData.define(DATA_FLOWER, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("OxidationStage", getOxidationStage());
        tag.putBoolean("Waxed", isWaxed());
        tag.putBoolean("Flower", hasFlower());
        tag.putInt("OxidationProgress", oxidationProgress);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setOxidationStage(tag.getInt("OxidationStage"));
        setWaxed(tag.getBoolean("Waxed"));
        setFlower(tag.getBoolean("Flower"));
        oxidationProgress = Math.max(0, tag.getInt("OxidationProgress"));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {
            if (cooldown > 0) cooldown--;
            tickOxidation(serverLevel);
            tickIronGolemFlower(serverLevel);
        }
    }

    private void tickOxidation(ServerLevel level) {
        if (!CgsConfig.ENABLE_OXIDATION.get() || isWaxed() || isRemoved()) return;

        if (getOxidationStage() < 3) {
            oxidationProgress++;
            if (oxidationProgress >= CgsConfig.OXIDATION_STAGE_TICKS.get()) {
                setOxidationStage(getOxidationStage() + 1);
                level.playSound(null, blockPosition(), SoundEvents.COPPER_PLACE,
                        SoundSource.NEUTRAL, 0.7F, 0.9F + getOxidationStage() * 0.04F);
            }
            return;
        }

        int average = Math.max(1, CgsConfig.STATUE_AVERAGE_TICKS.get());
        if (getRandom().nextInt(average) == 0) {
            becomeStatue(level);
        }
    }

    private void tickIronGolemFlower(ServerLevel level) {
        if (hasFlower() || tickCount % 200 != 0 || getRandom().nextInt(20) != 0) return;
        List<IronGolem> nearby = level.getEntitiesOfClass(IronGolem.class,
                getBoundingBox().inflate(4.0D),
                g -> g != this && !(g instanceof CopperGolemEntity));
        if (!nearby.isEmpty()) {
            setFlower(true);
        }
    }

    private void becomeStatue(ServerLevel level) {
        dropCarriedStack();

        BlockPos pos = blockPosition();
        if (!level.getBlockState(pos).canBeReplaced()) pos = pos.above();

        if (level.getBlockState(pos).canBeReplaced()) {
            // Statues always start in the stable standing pose. The player can cycle
            // poses afterward by interacting with the statue.
            CopperGolemStatueBlock.StatuePose pose = CopperGolemStatueBlock.StatuePose.STANDING;
            Direction facing = Direction.fromYRot(getYRot());

            BlockState statue = ModBlocks.COPPER_GOLEM_STATUE.get().defaultBlockState()
                    .setValue(CopperGolemStatueBlock.FACING, facing)
                    .setValue(CopperGolemStatueBlock.POSE, pose)
                    .setValue(CopperGolemStatueBlock.OXIDATION, 3)
                    .setValue(CopperGolemStatueBlock.WAXED, false);

            level.setBlock(pos, statue, 3);
            if (hasCustomName() && level.getBlockEntity(pos) instanceof CopperGolemStatueBlockEntity statueBe) {
                statueBe.setCustomName(getCustomName());
            }
            level.playSound(null, pos, SoundEvents.COPPER_PLACE, SoundSource.NEUTRAL, 1.0F, 0.75F);
        } else {
            spawnAtLocation(ModBlocks.COPPER_GOLEM_STATUE.get());
        }

        discard();
    }

    public int getOxidationStage() {
        return entityData.get(DATA_OXIDATION);
    }

    public void setOxidationStage(int stage) {
        entityData.set(DATA_OXIDATION, Math.max(0, Math.min(3, stage)));
        oxidationProgress = 0;
    }

    public boolean isWaxed() {
        return entityData.get(DATA_WAXED);
    }

    public void setWaxed(boolean waxed) {
        entityData.set(DATA_WAXED, waxed);
    }

    public boolean hasFlower() {
        return entityData.get(DATA_FLOWER);
    }

    public void setFlower(boolean flower) {
        entityData.set(DATA_FLOWER, flower);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);

        if (held.isEmpty() && !carriedStack().isEmpty()) {
            if (!level().isClientSide) {
                dropCarriedStack();
                level().playSound(null, blockPosition(), SoundEvents.ITEM_PICKUP,
                        SoundSource.NEUTRAL, 0.8F, 0.8F);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }

        if (held.is(Items.HONEYCOMB) && !isWaxed()) {
            if (!level().isClientSide) {
                setWaxed(true);
                level().playSound(null, blockPosition(), SoundEvents.HONEYCOMB_WAX_ON,
                        SoundSource.NEUTRAL, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) held.shrink(1);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }

        if (held.is(Items.SHEARS) && hasFlower()) {
            if (!level().isClientSide) {
                setFlower(false);
                spawnAtLocation(Items.POPPY);
                if (!player.getAbilities().instabuild) {
                    held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                }
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }

        if (held.getItem() instanceof AxeItem && (isWaxed() || getOxidationStage() > 0)) {
            if (!level().isClientSide) {
                if (isWaxed()) {
                    setWaxed(false);
                    level().playSound(null, blockPosition(), SoundEvents.AXE_WAX_OFF,
                            SoundSource.NEUTRAL, 1.0F, 1.0F);
                } else {
                    setOxidationStage(getOxidationStage() - 1);
                    level().playSound(null, blockPosition(), SoundEvents.AXE_SCRAPE,
                            SoundSource.NEUTRAL, 1.0F, 1.0F);
                }

                if (!player.getAbilities().instabuild) {
                    held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                }
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        dropCarriedStack();
        int count = 1 + getRandom().nextInt(3);
        spawnAtLocation(new ItemStack(Items.COPPER_INGOT, count));
    }

    private ItemStack carriedStack() {
        return getItemBySlot(EquipmentSlot.MAINHAND);
    }

    private void setCarriedStack(ItemStack stack) {
        setItemSlot(EquipmentSlot.MAINHAND, stack);
    }

    private void dropCarriedStack() {
        ItemStack stack = carriedStack();
        if (!stack.isEmpty()) {
            spawnAtLocation(stack.copy());
            setCarriedStack(ItemStack.EMPTY);
        }
    }

    private enum TaskPhase { IDLE, TO_SOURCE, FIND_DESTINATION, TO_DESTINATION }

    private final class SortingGoal extends Goal {
        SortingGoal() {
            setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override public boolean canUse() { return cooldown <= 0; }
        @Override public boolean canContinueToUse() { return cooldown <= 0; }

        @Override
        public void tick() {
            if (!(level() instanceof ServerLevel serverLevel)) return;

            switch (taskPhase) {
                case IDLE -> findSource(serverLevel);
                case TO_SOURCE -> tickMoveToSource(serverLevel);
                case FIND_DESTINATION -> findDestination(serverLevel);
                case TO_DESTINATION -> tickMoveToDestination(serverLevel);
            }
        }

        private void findSource(ServerLevel level) {
            if (!carriedStack().isEmpty()) {
                taskPhase = TaskPhase.FIND_DESTINATION;
                return;
            }

            List<BlockEntity> nearby = InventoryAccess.nearbyBlockEntities(level, blockPosition(),
                    CgsConfig.SEARCH_RADIUS.get(), CgsConfig.VERTICAL_SEARCH_RADIUS.get());

            int visited = 0;
            for (BlockEntity be : nearby) {
                if (!(be instanceof CopperChestBlockEntity chest)) continue;
                if (++visited > CgsConfig.MAX_CONTAINERS_PER_SEARCH.get()) break;
                if (InventoryAccess.isLocked(be) || chest.isEmpty()) continue;

                sourcePos = be.getBlockPos();
                taskPhase = TaskPhase.TO_SOURCE;
                pathTicks = 0;
                getNavigation().moveTo(sourcePos.getX() + 0.5D, sourcePos.getY(),
                        sourcePos.getZ() + 0.5D, 1.0D);
                return;
            }

            cooldown = CgsConfig.FAILED_SEARCH_COOLDOWN_TICKS.get();
        }

        private void tickMoveToSource(ServerLevel level) {
            if (sourcePos == null) {
                resetToIdle();
                return;
            }

            BlockEntity be = level.getBlockEntity(sourcePos);
            if (!(be instanceof CopperChestBlockEntity chest)
                    || InventoryAccess.isLocked(be) || chest.isEmpty()) {
                resetToIdle();
                return;
            }

            if (distanceToSqr(sourcePos.getX() + 0.5D, sourcePos.getY() + 0.5D,
                    sourcePos.getZ() + 0.5D) <= 7.0D) {
                ItemStack extracted = InventoryAccess.extractFirst(chest, CgsConfig.MAX_ITEMS_PER_TRIP.get());
                if (extracted.isEmpty()) {
                    resetToIdle();
                    return;
                }

                setCarriedStack(extracted);
                getNavigation().stop();
                taskPhase = TaskPhase.FIND_DESTINATION;
                pathTicks = 0;
                return;
            }

            pathTicks++;
            if (pathTicks > CgsConfig.PATH_TIMEOUT_TICKS.get()) {
                resetToIdle();
            } else if (getNavigation().isDone() && pathTicks % 20 == 0) {
                getNavigation().moveTo(sourcePos.getX() + 0.5D, sourcePos.getY(),
                        sourcePos.getZ() + 0.5D, 1.0D);
            }
        }

        private void findDestination(ServerLevel level) {
            ItemStack carried = carriedStack();
            if (carried.isEmpty()) {
                resetToIdle();
                return;
            }

            ResourceLocation key = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(carried.getItem());
            if (key != null) {
                BlockPos cached = destinationCache.get(key);
                if (cached != null && isGoodDestination(level, cached, carried, true)) {
                    setDestination(cached);
                    return;
                }
                destinationCache.remove(key);
            }

            List<BlockEntity> nearby = InventoryAccess.nearbyBlockEntities(level, blockPosition(),
                    CgsConfig.SEARCH_RADIUS.get(), CgsConfig.VERTICAL_SEARCH_RADIUS.get());

            BlockEntity bestMatching = null;
            BlockEntity bestEmpty = null;
            int visited = 0;

            for (BlockEntity be : nearby) {
                if (sourcePos != null && be.getBlockPos().equals(sourcePos)) continue;
                if (InventoryAccess.isCopperInput(be)) continue;
                if (InventoryAccess.isLocked(be) || !InventoryAccess.isSupported(be)) continue;
                if (++visited > CgsConfig.MAX_CONTAINERS_PER_SEARCH.get()) break;
                if (!InventoryAccess.canAccept(be, carried)) continue;

                if (InventoryAccess.matchingCount(be, carried) > 0) {
                    bestMatching = be;
                    break;
                }

                if (bestEmpty == null && CgsConfig.ALLOW_COMPLETELY_EMPTY_DESTINATIONS.get()
                        && InventoryAccess.isCompletelyEmpty(be)) {
                    bestEmpty = be;
                }
            }

            BlockEntity chosen = bestMatching != null ? bestMatching : bestEmpty;
            if (chosen == null) {
                cooldown = CgsConfig.FAILED_SEARCH_COOLDOWN_TICKS.get();
                return;
            }

            if (key != null) destinationCache.put(key, chosen.getBlockPos());
            setDestination(chosen.getBlockPos());
        }

        private boolean isGoodDestination(ServerLevel level, BlockPos pos, ItemStack carried, boolean cached) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be == null || InventoryAccess.isCopperInput(be)
                    || InventoryAccess.isLocked(be) || !InventoryAccess.isSupported(be)) return false;
            if (!InventoryAccess.canAccept(be, carried)) return false;
            if (InventoryAccess.matchingCount(be, carried) > 0) return true;
            return !cached && CgsConfig.ALLOW_COMPLETELY_EMPTY_DESTINATIONS.get()
                    && InventoryAccess.isCompletelyEmpty(be);
        }

        private void setDestination(BlockPos pos) {
            destinationPos = pos;
            taskPhase = TaskPhase.TO_DESTINATION;
            pathTicks = 0;
            getNavigation().moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 1.0D);
        }

        private void tickMoveToDestination(ServerLevel level) {
            ItemStack carried = carriedStack();
            if (carried.isEmpty()) {
                resetToIdle();
                return;
            }

            if (destinationPos == null || !isGoodDestination(level, destinationPos, carried, false)) {
                destinationPos = null;
                taskPhase = TaskPhase.FIND_DESTINATION;
                return;
            }

            if (distanceToSqr(destinationPos.getX() + 0.5D, destinationPos.getY() + 0.5D,
                    destinationPos.getZ() + 0.5D) <= 7.0D) {
                BlockEntity target = level.getBlockEntity(destinationPos);
                if (target == null) {
                    taskPhase = TaskPhase.FIND_DESTINATION;
                    return;
                }

                ItemStack remainder = InventoryAccess.insert(target, carried);
                setCarriedStack(remainder);
                getNavigation().stop();

                if (remainder.isEmpty()) {
                    sourcePos = null;
                    destinationPos = null;
                    taskPhase = TaskPhase.IDLE;
                    cooldown = 10;
                } else {
                    destinationPos = null;
                    taskPhase = TaskPhase.FIND_DESTINATION;
                }
                return;
            }

            pathTicks++;
            if (pathTicks > CgsConfig.PATH_TIMEOUT_TICKS.get()) {
                ResourceLocation key = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(carried.getItem());
                if (key != null) destinationCache.remove(key);
                destinationPos = null;
                taskPhase = TaskPhase.FIND_DESTINATION;
                pathTicks = 0;
            } else if (getNavigation().isDone() && pathTicks % 20 == 0) {
                getNavigation().moveTo(destinationPos.getX() + 0.5D, destinationPos.getY(),
                        destinationPos.getZ() + 0.5D, 1.0D);
            }
        }

        private void resetToIdle() {
            getNavigation().stop();
            sourcePos = null;
            destinationPos = null;
            taskPhase = TaskPhase.IDLE;
            pathTicks = 0;
        }
    }
}
