package com.syric.aetheric_tetranomicon.effects;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.ModularItem;

import java.util.List;

public class PhoenixEffect {
    public static final ItemEffect phoenix = ItemEffect.get("aetheric_tetranomicon:phoenix");

    /**
     * Phoenix tools auto-smelt block drops.
     * Some of this code is copied from the Aether: Lost Content Addon mod.
     */
    @SubscribeEvent
    public void destroyBlock(BlockEvent.BreakEvent event) {
        LevelAccessor levelAccessor = event.getLevel();

        if (levelAccessor instanceof ServerLevel level) {
            Player player = event.getPlayer();
            BlockState state = event.getState();
            BlockPos pos = event.getPos();
            ItemStack heldStack = player.getMainHandItem();

            if (heldStack.getItem() instanceof ModularItem item) {
                int effectLevel = item.getEffectLevel(heldStack, phoenix);

                boolean notCreative = !player.isCreative();
                boolean correctTool = player.hasCorrectToolForDrops(state);
                boolean phoenixTool = effectLevel > 0;

                if (notCreative && correctTool && phoenixTool) {
//                    AethericTetranomicon.LOGGER.info("detected modular phoenix tool, triggering ability");
                    if (event.getExpToDrop() > 0) {
                        state.getBlock().popExperience(level, pos, event.getExpToDrop());
                    }

                    List<ItemStack> drops = Block.getDrops(state, level, pos, level.getBlockEntity(pos), player, player.getMainHandItem());
                    drops.forEach((itemStack) -> {
                        Block.popResource(level.getLevel(), pos, getSmeltedResult(itemStack, level.getLevel()));
                    });
                    level.getLevel().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    /**
     * Copied from the Aether: Lost Content Addon mod
     */
    private ItemStack getSmeltedResult(ItemStack stack, Level level) {
        Container inventory = new SimpleContainer(stack);
        ItemStack output = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, inventory, level).map((furnaceRecipe) -> {
            return furnaceRecipe.assemble(inventory, level.registryAccess());
        }).orElse(stack);
        output.setCount(stack.getCount());
        return output;
    }

    /**
     * Phoenix weapons deal 120% damage to burning targets.
     */
    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        DamageSource damageSource = event.getSource();
        Entity sourceEntity = damageSource.getDirectEntity();

        if (sourceEntity instanceof Player player) {
            ItemStack heldStack = player.getMainHandItem();
            if (heldStack.getItem() instanceof ModularItem item) {
                int level = item.getEffectLevel(heldStack, phoenix);

                boolean phoenix = level > 0;
                boolean burningTarget = target.isOnFire();

                if (phoenix && burningTarget) {
//                    float storedAmount = event.getAmount();
                    event.setAmount(event.getAmount() * 1.2F);
//                    AethericTetranomicon.LOGGER.info(String.format("Phoenix effect changed damage from %s to %s", storedAmount, event.getAmount()));
                }
            }
        }
    }


    /**
     * @param event Phoenix arrows set targets on fire.
     */
    @SubscribeEvent
    public void onArrowHit(ProjectileImpactEvent event) {
//        AethericTetranomicon.LOGGER.info("ProjectileImpactEvent detected");

        HitResult hitResult = event.getRayTraceResult();
        Projectile projectile = event.getProjectile();

        if (hitResult instanceof EntityHitResult entityHitResult) {
            Entity target = entityHitResult.getEntity();

            int time = 0;

            for (String tag : projectile.getTags()) {
                if (tag.startsWith("phoenixTime")) {
                    time = Integer.parseInt(tag.split("_")[1]);
                    break;
                }
            }

            if (target.getType() == EntityType.ENDERMAN) {
                return;
            }

            boolean phoenix = projectile.getTags().contains("phoenix");
            boolean notCanceled = !event.isCanceled();
            boolean notZeroTime = time != 0;

//        AethericTetranomicon.LOGGER.info(String.format("Arrow hit detected! Not canceled: %s, Phoenix: %s", notCanceled, phoenix));

            if (phoenix && notCanceled && notZeroTime) {
//            AethericTetranomicon.LOGGER.info(String.format("Phoenix arrow hit a %s", target.getType()));
                target.setSecondsOnFire(time);
            }
        }
    }

}
