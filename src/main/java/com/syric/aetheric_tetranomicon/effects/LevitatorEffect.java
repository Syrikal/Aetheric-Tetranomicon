package com.syric.aetheric_tetranomicon.effects;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.block.miscellaneous.FloatingBlock;
import com.aetherteam.aether.entity.block.FloatingBlockEntity;
import com.aetherteam.aether.item.AetherItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;
import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.stats.getter.TooltipGetterNone;
import se.mickelus.tetra.items.modular.ItemModularHandheld;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;
import se.mickelus.tetra.util.TierHelper;
import se.mickelus.tetra.util.ToolActionHelper;

import java.util.Objects;

public class LevitatorEffect {
    public static final ItemEffect levitator_tool = ItemEffect.get("aetheric_tetranomicon:levitator_tool");
    public static final ItemEffect levitator_weapon = ItemEffect.get("aetheric_tetranomicon:levitator_weapon");

    /**
     * @param event
     * Gravitite tools can be used to levitate blocks.
     */
    @SubscribeEvent
    public void rightClickBlockEvent(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack heldStack = player.getMainHandItem();

        if (heldStack.getItem() instanceof ModularItem item) {
            int effectLevel = item.getEffectLevel(heldStack, levitator_tool);

            if (effectLevel > 0) {
//                AethericTetranomicon.LOGGER.info("detected modular gravitite tool, triggering ability");
                Level level = event.getLevel();
                BlockPos blockPos = event.getPos();
                BlockState blockState = level.getBlockState(blockPos);
                InteractionHand hand = event.getHand();

                boolean playerNotSneaking = !player.isShiftKeyDown();
                boolean correctTool = isCorrectToolForDrops(heldStack, blockState);

//                AethericTetranomicon.LOGGER.info("Checking whether gravitite tool is correct for " + blockState.getBlock() + ", conclusion: " + correctTool);

                boolean blockIsFree = FloatingBlock.isFree(level.getBlockState(blockPos.above()));
                boolean notBlockEntity = level.getBlockEntity(blockPos) == null;
                boolean breakable = blockState.getDestroySpeed(level, blockPos) >= 0.0F;
                boolean notDoubleBlockHalf = !blockState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF);
                boolean notBlacklisted = !blockState.is(AetherTags.Blocks.GRAVITITE_ABILITY_BLACKLIST);

//                AethericTetranomicon.LOGGER.info(String.format("Not sneaking: %s, correct tool: %s, block free: %s, not a block entity: %s, breakable: %s, not double block half: %s, not blacklisted: %s", playerNotSneaking, correctTool, blockIsFree, notBlockEntity, breakable, notDoubleBlockHalf, notBlacklisted));

                if (playerNotSneaking && correctTool && blockIsFree && notBlockEntity && breakable && notDoubleBlockHalf && notBlacklisted) {
                    if (!level.isClientSide()) {
                        FloatingBlockEntity entity = new FloatingBlockEntity(level, (double) blockPos.getX() + 0.5, blockPos.getY(), (double) blockPos.getZ() + 0.5, blockState);
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


    /**
     * @param event
     * Gravitite weapons can be used to throw enemies into the air.
     */
    @SubscribeEvent
    public void attackEvent(LivingAttackEvent event) {
        LivingEntity target = event.getEntity();
        DamageSource damageSource = event.getSource();
        Entity sourceEntity = damageSource.getDirectEntity();

        if (sourceEntity instanceof Player player) {
            ItemStack heldStack = player.getMainHandItem();

            if (heldStack.getItem() instanceof ModularItem item) {
                int level = item.getEffectLevel(heldStack, levitator_weapon);

                boolean levitator = level > 0;
                boolean notCanceled = !event.isCanceled();
                boolean fullStrengthAttack = player.getAttackStrengthScale(1.0F) == 1.0F;
                boolean validEntity = !target.getType().is(AetherTags.Entities.UNLAUNCHABLE);
                boolean onGround = target.isOnGround() || target.isInFluidType();

//                AethericTetranomicon.LOGGER.info(String.format(
//                        "Levitator: %s, Not Canceled: %s, Full Strength Attack: %s, Valid Entity: %s, On Ground: %s",
//                        levitator, notCanceled, fullStrengthAttack, validEntity, onGround));

                if (levitator && notCanceled && fullStrengthAttack && validEntity && onGround) {
//                    AethericTetranomicon.LOGGER.info("detected modular gravitite weapon, triggering ability");
//                    AethericTetranomicon.LOGGER.info("Target's delta movement before push is " + target.getDeltaMovement());
                    target.setOnGround(false);
                    target.push(0.0, 1.0, 0.0);
//                    AethericTetranomicon.LOGGER.info("Target's delta movement after push is " + target.getDeltaMovement());
                    target.hurtMarked = true;
//                    target.setSecondsOnFire(1);

                    if (target instanceof ServerPlayer serverPlayer) {
                        serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
                    }
                }
            }
        }

    }

    /**
     * @param event Levitator arrows throw targets into the air.
     */
    @SubscribeEvent
    public void onArrowHit(ProjectileImpactEvent event) {
//        AethericTetranomicon.LOGGER.info("ProjectileImpactEvent detected");

        HitResult hitResult = event.getRayTraceResult();
        Projectile projectile = event.getProjectile();

        if (hitResult instanceof EntityHitResult entityHitResult) {
            Entity target = entityHitResult.getEntity();

            int drawProgress = 0;
            for (String tag : projectile.getTags()) {
                if (tag.startsWith("drawProgress")) {
                    drawProgress = Integer.parseInt(tag.split("_")[1]);
                }
            }

            boolean levitator = projectile.getTags().contains("levitator");
            boolean notCanceled = !event.isCanceled();
            boolean critArrow = drawProgress >= 20;
            boolean validEntity = !target.getType().is(AetherTags.Entities.UNLAUNCHABLE);
            boolean onGround = target.isOnGround();

//        AethericTetranomicon.LOGGER.info(String.format("Arrow hit detected! Not canceled: %s, Levitator: %s", notCanceled, levitator));

            if (levitator && notCanceled && critArrow && validEntity && onGround) {
//            AethericTetranomicon.LOGGER.info(String.format("Levitator arrow hit a %s", target.getType()));
                target.push(0.0, 1.0, 0.0);
                target.hurtMarked = true;
                target.setOnGround(false);
                if (target instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
                }
            }
        }
    }


    /**
     * @param event Gravitite tools drop golden amber from golden oak logs.
     */
    @SubscribeEvent
    public void breakEvent(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level level = player.level;
        BlockPos blockPos = event.getPos();
        ItemStack heldStack = player.getMainHandItem();
        BlockState blockState = event.getState();

        if (heldStack.getItem() instanceof ModularItem item) {
            int effectlevel = item.getEffectLevel(heldStack, levitator_tool);

            boolean notCanceled = !event.isCanceled();
            boolean levitator = effectlevel > 0;
            boolean goldenOak = blockState.is(AetherBlocks.GOLDEN_OAK_LOG.get()) || blockState.is(AetherBlocks.GOLDEN_OAK_WOOD.get());
            boolean noSilkTouch = heldStack.getEnchantmentLevel(Enchantments.SILK_TOUCH) == 0;

            if (levitator && notCanceled && goldenOak && noSilkTouch) {
                boolean two = level.getRandom().nextBoolean();
                ItemEntity itemEntity = new ItemEntity(level, (double) blockPos.getX() + 0.5, (double) blockPos.getY() + 0.5, (double) blockPos.getZ() + 0.5, new ItemStack(AetherItems.GOLDEN_AMBER.get(), two ? 2 : 1));
                level.addFreshEntity(itemEntity);
            }
        }
    }


    private boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        if (stack.getItem() instanceof ItemModularHandheld) {
            return ToolActionHelper.getAppropriateTools(state).stream().map((requiredTool) -> {
                return ((ItemModularHandheld) stack.getItem()).getHarvestTier(stack, requiredTool);
            }).map(TierHelper::getTier).filter(Objects::nonNull).anyMatch((tier) -> {
                return TierSortingRegistry.isCorrectTierForDrops(tier, state);
            });
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public static void addBars(FMLClientSetupEvent event) {
        IStatGetter levitatorToolGetter = new StatGetterEffectLevel(levitator_tool, 1.0);
        GuiStatBar levitatorToolBar = new GuiStatBar(0, 0, 59, "tetra.stats.levitator_tool", 0.0, 1.0, false, levitatorToolGetter, LabelGetterBasic.noLabel, new TooltipGetterNone("tetra.stats.levitator_tool.tooltip"));

        IStatGetter levitatorWeaponGetter = new StatGetterEffectLevel(levitator_weapon, 1.0);
        GuiStatBar levitatorWeaponBar = new GuiStatBar(0, 0, 59, "tetra.stats.levitator_weapon", 0.0, 1.0, false, levitatorWeaponGetter, LabelGetterBasic.noLabel, new TooltipGetterNone("tetra.stats.levitator_weapon.tooltip"));

        WorkbenchStatsGui.addBar(levitatorToolBar);
        HoloStatsGui.addBar(levitatorToolBar);
        WorkbenchStatsGui.addBar(levitatorWeaponBar);
        HoloStatsGui.addBar(levitatorWeaponBar);
    }

}
