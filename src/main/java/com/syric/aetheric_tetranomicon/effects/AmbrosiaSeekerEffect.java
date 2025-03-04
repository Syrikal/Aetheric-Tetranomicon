package com.syric.aetheric_tetranomicon.effects;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.item.AetherItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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

public class AmbrosiaSeekerEffect {
    public static final ItemEffect ambrosia_seeker_tool = ItemEffect.get("aetheric_tetranomicon:ambrosia_seeker_tool");
    public static final ItemEffect ambrosia_seeker_weapon = ItemEffect.get("aetheric_tetranomicon:ambrosia_seeker_weapon");

    /**
     * Holystone tools can drop ambrosia when used.
     */
    @SubscribeEvent
    public void breakEvent(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level level = player.level();
        BlockPos blockPos = event.getPos();
        ItemStack heldStack = player.getMainHandItem();
        BlockState blockState = event.getState();

        if (heldStack.getItem() instanceof ModularItem item) {
            int effectlevel = item.getEffectLevel(heldStack, ambrosia_seeker_tool);

            if (!event.isCanceled() && effectlevel > 0) {
//                AethericTetranomicon.LOGGER.info("detected modular holystone tool, triggering ability");
                if (!level.isClientSide() && blockState.getDestroySpeed(level, blockPos) > 0.0F && heldStack.isCorrectToolForDrops(blockState) && player.getRandom().nextInt(50) == 0) {
                    ItemEntity itemEntity = new ItemEntity(level, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, new ItemStack(AetherItems.AMBROSIUM_SHARD.get()));
                    level.addFreshEntity(itemEntity);
                }
            }
        }
    }

    /**
     * Holystone weapons can drop ambrosia when used.
     */
    @SubscribeEvent
    public void attackEvent(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        DamageSource damageSource = event.getSource();
        Entity sourceEntity = damageSource.getDirectEntity();

        if (sourceEntity instanceof Player player) {
            ItemStack heldStack = player.getMainHandItem();

            if (heldStack.getItem() instanceof ModularItem item) {
                int level = item.getEffectLevel(heldStack, ambrosia_seeker_weapon);

                boolean ambrosia_seeker = level > 0;
                boolean notCanceled = !event.isCanceled();
                boolean fullStrengthAttack = player.getAttackStrengthScale(1.0F) == 1.0F;
                boolean validEntity = !target.getType().is(AetherTags.Entities.NO_AMBROSIUM_DROPS);
                boolean randomTrigger = target.level().getRandom().nextInt(25) == 0;


                if (ambrosia_seeker && notCanceled && fullStrengthAttack && validEntity && randomTrigger) {
//                    AethericTetranomicon.LOGGER.info("detected modular holystone weapon, triggering ability");
                    target.spawnAtLocation(AetherItems.AMBROSIUM_SHARD.get());
                }
            }
        }
    }

    /**
     * Ambrosia seeker arrows can drop ambrosia when they hit.
     */
    @SubscribeEvent
    public void onArrowHit(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        Entity sourceEntity = event.getSource().getDirectEntity();

        if (sourceEntity instanceof Projectile projectile) {

            int drawProgress = 0;
            for (String tag : projectile.getTags()) {
                if (tag.startsWith("drawProgress")) {
                    drawProgress = Integer.parseInt(tag.split("_")[1]);
                }
            }

            boolean ambrosia_seeker = projectile.getTags().contains("ambrosia_seeker");
            boolean notCanceled = !event.isCanceled();
            boolean critArrow = drawProgress >= 20;
            boolean validEntity = !target.getType().is(AetherTags.Entities.NO_AMBROSIUM_DROPS);
            boolean randomTrigger = target.level().getRandom().nextInt(25) == 0;

            if (ambrosia_seeker && notCanceled && critArrow && validEntity && randomTrigger) {
                target.spawnAtLocation(AetherItems.AMBROSIUM_SHARD.get());
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void addBars(FMLClientSetupEvent event) {
        IStatGetter ambrosiaToolGetter = new StatGetterEffectLevel(ambrosia_seeker_tool, 1.0);
        GuiStatBar ambrosiaToolBar = new GuiStatBar(0, 0, 59, "tetra.stats.ambrosia_tool", 0.0, 1.0, false, ambrosiaToolGetter, LabelGetterBasic.noLabel, new TooltipGetterNone("tetra.stats.ambrosia_tool.tooltip"));

        IStatGetter ambrosiaWeaponGetter = new StatGetterEffectLevel(ambrosia_seeker_weapon, 1.0);
        GuiStatBar ambrosiaWeaponBar = new GuiStatBar(0, 0, 59, "tetra.stats.ambrosia_weapon", 0.0, 1.0, false, ambrosiaWeaponGetter, LabelGetterBasic.noLabel, new TooltipGetterNone("tetra.stats.ambrosia_weapon.tooltip"));

        WorkbenchStatsGui.addBar(ambrosiaToolBar);
        HoloStatsGui.addBar(ambrosiaToolBar);
        WorkbenchStatsGui.addBar(ambrosiaWeaponBar);
        HoloStatsGui.addBar(ambrosiaWeaponBar);
    }

}
