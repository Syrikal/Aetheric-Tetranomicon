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
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
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
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

import java.util.List;
import java.util.Optional;

public class PhoenixEffect {
    public static final ItemEffect phoenix = ItemEffect.get("aetheric_tetranomicon:phoenix");

    /**
     * Phoenix tools auto-smelt block drops.
     * Code from Aether: Lost Content used as reference.
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

                    for (ItemStack drop : drops) {
                        Container container = new SimpleContainer(drop);
                        Optional<SmeltingRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, container, level);
                        if (recipe.isPresent()) {
                            ItemStack smelted_stack = recipe.get().getResultItem(level.registryAccess());
                            smelted_stack.setCount(drop.getCount());
                            Block.popResource(level, pos, smelted_stack);
                        } else {
                            Block.popResource(level, pos, drop);
                        }
                    }
                    level.getLevel().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
        }
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

    @OnlyIn(Dist.CLIENT)
    public static void addBars(FMLClientSetupEvent event) {
        IStatGetter phoenixGetter = new StatGetterEffectLevel(phoenix, 1.0);
        GuiStatBar phoenixBar = new GuiStatBar(0, 0, 59, "tetra.stats.phoenix", 0.0, 1.0, false, phoenixGetter, LabelGetterBasic.noLabel, new TooltipGetterNone("tetra.stats.phoenix.tooltip"));

        WorkbenchStatsGui.addBar(phoenixBar);
        HoloStatsGui.addBar(phoenixBar);
    }
    
}
