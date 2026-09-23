package dev.acuario22.cgs;

import com.mojang.brigadier.Command;
import dev.acuario22.cgs.entity.CopperGolemEntity;
import dev.acuario22.cgs.registry.ModEntities;
import dev.acuario22.cgs.util.InventoryAccess;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = CopperGolemSorter.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CommonEvents {
    private CommonEvents() {}

    @SubscribeEvent
    public static void onPumpkinPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!event.getPlacedBlock().is(Blocks.CARVED_PUMPKIN)) return;

        BlockPos pumpkinPos = event.getPos();
        BlockPos copperPos = pumpkinPos.below();
        CopperVariant variant = variant(level.getBlockState(copperPos).getBlock());
        if (variant == null) return;

        CopperGolemEntity golem = ModEntities.COPPER_GOLEM.get().create(level);
        if (golem == null) return;

        level.setBlock(pumpkinPos, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(copperPos, Blocks.AIR.defaultBlockState(), 3);

        golem.moveTo(copperPos.getX() + 0.5D, copperPos.getY(),
                copperPos.getZ() + 0.5D, 0.0F, 0.0F);
        golem.setOxidationStage(variant.stage);
        golem.setWaxed(variant.waxed);
        level.addFreshEntity(golem);
        level.playSound(null, copperPos, SoundEvents.COPPER_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @SubscribeEvent
    public static void onChestLock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack held = event.getItemStack();
        if (!player.isShiftKeyDown() || !held.is(Items.COPPER_NUGGET)) return;
        if (!(player.level() instanceof ServerLevel level)) return;

        BlockEntity be = level.getBlockEntity(event.getPos());
        if (be == null || !InventoryAccess.isSupported(be)) return;

        boolean locked = !InventoryAccess.isLocked(be);
        InventoryAccess.setLocked(be, locked);
        player.sendSystemMessage(Component.translatable(
                locked ? "message.copper_golem_sorter.locked" : "message.copper_golem_sorter.unlocked"));

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    @SubscribeEvent
    public static void onCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("cgs")
                        .then(Commands.literal("scan")
                                .executes(ctx -> {
                                    ServerLevel level = ctx.getSource().getLevel();
                                    BlockPos origin = BlockPos.containing(ctx.getSource().getPosition());
                                    List<BlockEntity> all = InventoryAccess.nearbyBlockEntities(level, origin,
                                            CgsConfig.SEARCH_RADIUS.get(), CgsConfig.VERTICAL_SEARCH_RADIUS.get());

                                    int compatible = 0;
                                    int locked = 0;
                                    int generations = 0;
                                    int inputs = 0;

                                    for (BlockEntity be : all) {
                                        if (!InventoryAccess.isSupported(be)) continue;
                                        compatible++;
                                        if (InventoryAccess.isLocked(be)) locked++;
                                        if (InventoryAccess.isGenerationsChest(be)) generations++;
                                        if (InventoryAccess.isCopperInput(be)) inputs++;
                                    }

                                    int fc = compatible, fl = locked, fg = generations, fi = inputs;
                                    ctx.getSource().sendSuccess(() -> Component.literal(
                                            "Copper Golem Sorter: " + fc + " contenedores compatibles; "
                                                    + fi + " Copper Chest; "
                                                    + fg + " Generations Core; "
                                                    + fl + " bloqueados."), false);
                                    return Command.SINGLE_SUCCESS;
                                }))
        );
    }

    private static CopperVariant variant(Block block) {
        if (block == Blocks.COPPER_BLOCK) return new CopperVariant(0, false);
        if (block == Blocks.EXPOSED_COPPER) return new CopperVariant(1, false);
        if (block == Blocks.WEATHERED_COPPER) return new CopperVariant(2, false);
        if (block == Blocks.OXIDIZED_COPPER) return new CopperVariant(2, false);
        if (block == Blocks.WAXED_COPPER_BLOCK) return new CopperVariant(0, true);
        if (block == Blocks.WAXED_EXPOSED_COPPER) return new CopperVariant(1, true);
        if (block == Blocks.WAXED_WEATHERED_COPPER) return new CopperVariant(2, true);
        if (block == Blocks.WAXED_OXIDIZED_COPPER) return new CopperVariant(2, true);
        return null;
    }

    private record CopperVariant(int stage, boolean waxed) {}
}
