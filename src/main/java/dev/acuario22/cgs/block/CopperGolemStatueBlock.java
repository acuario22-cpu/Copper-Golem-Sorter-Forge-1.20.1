package dev.acuario22.cgs.block;

import dev.acuario22.cgs.entity.CopperGolemEntity;
import dev.acuario22.cgs.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public final class CopperGolemStatueBlock extends HorizontalDirectionalBlock {
    private static final VoxelShape SHAPE = box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public CopperGolemStatueBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level,
                               BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (!(held.getItem() instanceof AxeItem)) return InteractionResult.PASS;

        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            CopperGolemEntity golem = ModEntities.COPPER_GOLEM.get().create(serverLevel);
            if (golem != null) {
                golem.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
                        state.getValue(FACING).toYRot(), 0.0F);
                golem.setOxidationStage(2);
                golem.setWaxed(false);
                level.removeBlock(pos, false);
                serverLevel.addFreshEntity(golem);
                serverLevel.playSound(null, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING);
    }
}
