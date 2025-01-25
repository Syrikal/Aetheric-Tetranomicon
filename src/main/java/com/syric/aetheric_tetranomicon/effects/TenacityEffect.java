package com.syric.aetheric_tetranomicon.effects;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.EquipmentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
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

public class TenacityEffect {
    public static final ItemEffect tenacity_tool = ItemEffect.get("aetheric_tetranomicon:tenacity_tool");
    public static final ItemEffect tenacity_weapon = ItemEffect.get("aetheric_tetranomicon:tenacity_weapon");

    /**
     * @param event
     * Zanite tools increase their mining speed as their durability decreases.
     */
    @SubscribeEvent
    public void mineEvent(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        ItemStack heldStack = player.getMainHandItem();
        if (heldStack.getItem() instanceof ModularItem item) {
            int level = item.getEffectLevel(heldStack, tenacity_tool);
            if (level > 0 && !event.isCanceled()) {
//                AethericTetranomicon.LOGGER.info("detected modular zanite tool, triggering ability");
//                AethericTetranomicon.LOGGER.info("tool durability: " + heldStack.getDamageValue() + " out of " + heldStack.getMaxDamage());
                event.setNewSpeed((float) EquipmentUtil.calculateZaniteBuff(heldStack, event.getNewSpeed()));
            }
        }
    }

    /**
     * @param event
     * Zanite weapons increase their damage as their durability decreases.
     */
    @SubscribeEvent
    public void attackEvent(LivingDamageEvent event) {
        DamageSource damageSource = event.getSource();
        Entity sourceEntity = damageSource.getDirectEntity();

        if (sourceEntity instanceof Player player) {
            ItemStack heldStack = player.getMainHandItem();

            if (heldStack.getItem() instanceof ModularItem item) {
                int level = item.getEffectLevel(heldStack, tenacity_weapon);
                if (level > 0 && !event.isCanceled()) {
//                    AethericTetranomicon.LOGGER.info("detected modular zanite weapon, triggering ability");
//                    AethericTetranomicon.LOGGER.info("zanite weapon initial damage: " + event.getAmount());
//                    AethericTetranomicon.LOGGER.info("weapon durability: " + heldStack.getDamageValue() + " out of " + heldStack.getMaxDamage());

                    float buffedAmount = (float) EquipmentUtil.calculateZaniteBuff(heldStack, event.getAmount());

                    event.setAmount(Math.max(event.getAmount(), buffedAmount));

//                    AethericTetranomicon.LOGGER.info("zanite weapon final damage: " + event.getAmount());
                }
            }
        }
    }

    /**
     * @param event Zanite arrows increase in damage as their durability decreases.
     */
    @SubscribeEvent
    public void onArrowHit(LivingDamageEvent event) {
//        AethericTetranomicon.LOGGER.info("LivingDamageEvent detected");
        Entity source = event.getSource().getDirectEntity();

        if (source instanceof Projectile projectile) {
//            AethericTetranomicon.LOGGER.info("Damage source is projectile");

            int stackdamage = 0;
            boolean stackdamage_found = false;
            int maxdamage = 0;
            boolean maxdamage_found = false;
            float eventdamage = event.getAmount();

            for (String tag : projectile.getTags()) {
                if (tag.startsWith("stackDamage")) {
                    stackdamage = Integer.parseInt(tag.split("_")[1]);
                    stackdamage_found = true;
                } else if (tag.startsWith("maxDamage")) {
                    maxdamage = Integer.parseInt(tag.split("_")[1]);
                    maxdamage_found = true;
                }
            }

            boolean tenacity = projectile.getTags().contains("tenacity");
            boolean notCanceled = !event.isCanceled();
            boolean found_bow_durability_data = stackdamage_found && maxdamage_found;

//            AethericTetranomicon.LOGGER.info(String.format("Tenacity: %s, not cancelled: %s, nonzero tags: %s, stackDamage: %s, maxDamage: %s", tenacity, notCanceled, found_bow_durability_data, stackdamage, maxdamage));

            if (tenacity && notCanceled && found_bow_durability_data) {
                float buffedAmount = (float) (eventdamage * (2.0 * stackdamage / maxdamage + 0.5));
                float finalAmount = Math.max(event.getAmount(), buffedAmount);

//                AethericTetranomicon.LOGGER.info(String.format("Tenacious arrow hit a %s. Initial damage: %s, final damage: %s", event.getEntity().getType(), eventdamage, finalAmount));

                event.setAmount(finalAmount);
            }
        }
    }

    /**
     * @param event Zanite tools drop golden amber from golden oak logs.
     */
    @SubscribeEvent
    public void breakEvent(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level level = player.level;
        BlockPos blockPos = event.getPos();
        ItemStack heldStack = player.getMainHandItem();
        BlockState blockState = event.getState();

        if (heldStack.getItem() instanceof ModularItem item) {
            int effectlevel = item.getEffectLevel(heldStack, tenacity_tool);

            boolean notCanceled = !event.isCanceled();
            boolean zanite = effectlevel > 0;
            boolean goldenOak = blockState.is(AetherBlocks.GOLDEN_OAK_LOG.get()) || blockState.is(AetherBlocks.GOLDEN_OAK_WOOD.get());
            boolean noSilkTouch = heldStack.getEnchantmentLevel(Enchantments.SILK_TOUCH) == 0;

            if (zanite && notCanceled && goldenOak && noSilkTouch) {
                boolean two = level.getRandom().nextBoolean();
                ItemEntity itemEntity = new ItemEntity(level, (double) blockPos.getX() + 0.5, (double) blockPos.getY() + 0.5, (double) blockPos.getZ() + 0.5, new ItemStack(AetherItems.GOLDEN_AMBER.get(), two ? 2 : 1));
                level.addFreshEntity(itemEntity);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void addBars(FMLClientSetupEvent event) {
        IStatGetter tenacityToolGetter = new StatGetterEffectLevel(tenacity_tool, 1.0);
        GuiStatBar tenacityToolBar = new GuiStatBar(0, 0, 59, "tetra.stats.tenacity_tool", 0.0, 1.0, false, tenacityToolGetter, LabelGetterBasic.noLabel, new TooltipGetterNone("tetra.stats.tenacity_tool.tooltip"));

        IStatGetter tenacityWeaponGetter = new StatGetterEffectLevel(tenacity_weapon, 1.0);
        GuiStatBar tenacityWeaponBar = new GuiStatBar(0, 0, 59, "tetra.stats.tenacity_weapon", 0.0, 1.0, false, tenacityWeaponGetter, LabelGetterBasic.noLabel, new TooltipGetterNone("tetra.stats.tenacity_weapon.tooltip"));

        WorkbenchStatsGui.addBar(tenacityToolBar);
        HoloStatsGui.addBar(tenacityToolBar);
        WorkbenchStatsGui.addBar(tenacityWeaponBar);
        HoloStatsGui.addBar(tenacityWeaponBar);
    }

}
