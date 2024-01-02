package com.syric.aetheric_tetranomicon.effects;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.miscellaneous.FloatingBlock;
import com.aetherteam.aether.entity.block.FloatingBlockEntity;
import com.syric.aetheric_tetranomicon.AethericTetranomicon;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.ModularItem;

public class LevitatorEffect {
    public static final ItemEffect levitator = ItemEffect.get("aetheric_tetranomicon:levitator");

    /**
     * Gravitite tools can be used to levitate blocks.
     * @param event
     */
    @SubscribeEvent
    public void rightClickBlockEvent(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack heldStack = player.getMainHandItem();

        if (heldStack.getItem() instanceof ModularItem item) {
            int effectLevel = item.getEffectLevel(heldStack, levitator);

            if (effectLevel > 0) {
                AethericTetranomicon.LOGGER.info("detected modular gravitite tool, triggering ability");
                Level level = event.getLevel();
                BlockPos blockPos = event.getPos();
                BlockState blockState = level.getBlockState(blockPos);
                InteractionHand hand = event.getHand();

                boolean playerNotSneaking = !player.isShiftKeyDown();
                boolean correctTool = heldStack.isCorrectToolForDrops(blockState);
                boolean blockIsFree = FloatingBlock.isFree(level.getBlockState(blockPos.above()));
                boolean notBlockEntity = level.getBlockEntity(blockPos) == null;
                boolean breakable = blockState.getDestroySpeed(level, blockPos) >= 0.0F;
                boolean notDoubleBlockHalf = !blockState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF);
                boolean notBlacklisted = !blockState.is(AetherTags.Blocks.GRAVITITE_ABILITY_BLACKLIST);

                AethericTetranomicon.LOGGER.info(String.format("Not sneaking: %s, correct tool: %s, block free: %s, not a block entity: %s, breakable: %s, not double block half: %s, not blacklisted: %s", playerNotSneaking, correctTool, blockIsFree, notBlockEntity, breakable, notDoubleBlockHalf, notBlacklisted));

                if (playerNotSneaking && correctTool && blockIsFree && notBlockEntity && breakable && notDoubleBlockHalf && notBlacklisted) {
                    if (!level.isClientSide()) {
                        FloatingBlockEntity entity = new FloatingBlockEntity(level, (double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, blockState);
                        entity.setNatural(false);
                        if (blockState.is(BlockTags.ANVIL)) {
                            entity.setHurtsEntities(2.0F, 40);
                        }

                        level.addFreshEntity(entity);
                        level.setBlockAndUpdate(blockPos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
                        heldStack.hurtAndBreak(4, player, (p) -> p.broadcastBreakEvent(hand));
                    } else {
                        player.swing(hand);
                    }

//                        event.setResult(InteractionResult.sidedSuccess(level.isClientSide()));
                }

//                return false;
            }

        }
    }


//    /**
//     * Gravitite weapons can be used to throw enemies into the air.
//     * @param event
//     */
//    @SubscribeEvent
//    public void rightClickBlockEvent(PlayerInteractEvent.RightClickBlock event) {
//        Player player = event.getEntity();
//        ItemStack heldStack = player.getMainHandItem();
//
//        if (heldStack.getItem() instanceof ModularItem item) {
//            int effectLevel = item.getEffectLevel(heldStack, levitator);
//
//            if (effectLevel > 0) {
//                AethericTetranomicon.LOGGER.info("detected modular gravitite tool, triggering ability");
//                Level level = event.getLevel();
//                BlockPos blockPos = event.getPos();
//                BlockState blockState = level.getBlockState(blockPos);
//                InteractionHand hand = event.getHand();
//
//                boolean playerNotSneaking = !player.isShiftKeyDown();
//                boolean correctTool = heldStack.isCorrectToolForDrops(blockState);
//                boolean blockIsFree = FloatingBlock.isFree(level.getBlockState(blockPos.above()));
//                boolean notBlockEntity = level.getBlockEntity(blockPos) == null;
//                boolean breakable = blockState.getDestroySpeed(level, blockPos) >= 0.0F;
//                boolean notDoubleBlockHalf = !blockState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF);
//                boolean notBlacklisted = !blockState.is(AetherTags.Blocks.GRAVITITE_ABILITY_BLACKLIST);
//
//                AethericTetranomicon.LOGGER.info(String.format("Not sneaking: %s, correct tool: %s, block free: %s, not a block entity: %s, breakable: %s, not double block half: %s, not blacklisted: %s", playerNotSneaking, correctTool, blockIsFree, notBlockEntity, breakable, notDoubleBlockHalf, notBlacklisted));
//
//                if (playerNotSneaking && correctTool && blockIsFree && notBlockEntity && breakable && notDoubleBlockHalf && notBlacklisted) {
//                    if (!level.isClientSide()) {
//                        FloatingBlockEntity entity = new FloatingBlockEntity(level, (double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, blockState);
//                        entity.setNatural(false);
//                        if (blockState.is(BlockTags.ANVIL)) {
//                            entity.setHurtsEntities(2.0F, 40);
//                        }
//
//                        level.addFreshEntity(entity);
//                        level.setBlockAndUpdate(blockPos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
//                        heldStack.hurtAndBreak(4, player, (p) -> {
//                            p.broadcastBreakEvent(hand);
//                        });
//                    } else {
//                        player.swing(hand);
//                    }
//
////                        event.setResult(InteractionResult.sidedSuccess(level.isClientSide()));
//                }
//
////                return false;
//            }
//
//        }
//    }

}
