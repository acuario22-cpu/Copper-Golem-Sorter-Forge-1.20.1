package dev.acuario22.cgs.block;

import dev.acuario22.cgs.util.CopperWeathering;
import dev.acuario22.cgs.block.entity.CopperChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public final class CopperChestBlock extends BaseEntityBlock {
    public static final net.minecraft.world.level.block.state.properties.DirectionProperty FACING =
            HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty OXIDATION = IntegerProperty.create("oxidation", 0, 3);
    public static final BooleanProperty WAXED = BooleanProperty.create("waxed");
    private static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);

    public CopperChestBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OXIDATION, 0)
                .setValue(WAXED, false));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CopperChestBlockEntity(pos, state);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return !state.getValue(WAXED) && state.getValue(OXIDATION) < 3;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        CopperWeathering.randomTick(state, level, pos, random, OXIDATION, WAXED);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);

        if (held.is(Items.HONEYCOMB) && !state.getValue(WAXED)) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(WAXED, true), 3);
                level.playSound(null, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) held.shrink(1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (held.getItem() instanceof AxeItem) {
            if (state.getValue(WAXED)) {
                if (!level.isClientSide) {
                    level.setBlock(pos, state.setValue(WAXED, false), 3);
                    level.playSound(null, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                    damageAxe(player, hand, held);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            int oxidation = state.getValue(OXIDATION);
            if (oxidation > 0) {
                if (!level.isClientSide) {
                    level.setBlock(pos, state.setValue(OXIDATION, oxidation - 1), 3);
                    level.playSound(null, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    damageAxe(player, hand, held);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MenuProvider provider) {
                NetworkHooks.openScreen(serverPlayer, provider, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static void damageAxe(Player player, InteractionHand hand, ItemStack held) {
        if (!player.getAbilities().instabuild) {
            held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CopperChestBlockEntity chest) {
                Containers.dropContents(level, pos, chest);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, moving);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OXIDATION, WAXED);
    }
}
