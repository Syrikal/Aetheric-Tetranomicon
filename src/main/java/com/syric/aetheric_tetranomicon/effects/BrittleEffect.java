package com.syric.aetheric_tetranomicon.effects;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.ModularItem;

public class BrittleEffect {
    public static final ItemEffect brittle_tool = ItemEffect.get("aetheric_tetranomicon:brittle_tool");
    public static final ItemEffect brittle_weapon = ItemEffect.get("aetheric_tetranomicon:brittle_weapon");

    /**
     * @param event
     * Skyjade tools decrease their mining speed as their durability decreases.
     */
    @SubscribeEvent
    public void mineEvent(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        ItemStack heldStack = player.getMainHandItem();
        if (heldStack.getItem() instanceof ModularItem item) {
            int level = item.getEffectLevel(heldStack, brittle_tool);
            if (level > 0 && !event.isCanceled()) {
//                AethericTetranomicon.LOGGER.info("detected modular skyjade tool, triggering ability");
//                AethericTetranomicon.LOGGER.info("tool durability: " + heldStack.getDamageValue() + " out of " + heldStack.getMaxDamage());
                event.setNewSpeed((float) calculateSkyjadeBuff(heldStack, event.getNewSpeed()));
            }
        }
    }

    /**
     * @param event
     * Skyjade weapons decrease their damage as their durability decreases.
     */
    @SubscribeEvent
    public void attackEvent(LivingDamageEvent event) {
        DamageSource damageSource = event.getSource();
        Entity sourceEntity = damageSource.getDirectEntity();

        if (sourceEntity instanceof Player player) {
            ItemStack heldStack = player.getMainHandItem();

            if (heldStack.getItem() instanceof ModularItem item) {
                int level = item.getEffectLevel(heldStack, brittle_weapon);
                if (level > 0 && !event.isCanceled()) {
//                    AethericTetranomicon.LOGGER.info("detected modular skyjade weapon, triggering ability");
//                    AethericTetranomicon.LOGGER.info("zanite weapon initial damage: " + event.getAmount());
//                    AethericTetranomicon.LOGGER.info("weapon durability: " + heldStack.getDamageValue() + " out of " + heldStack.getMaxDamage());
                    float buffedAmount = (float) calculateSkyjadeBuff(heldStack, event.getAmount());
                    event.setAmount(Math.max(event.getAmount(), buffedAmount));
//                    AethericTetranomicon.LOGGER.info("buffed amount: " + buffedAmount);
//                    AethericTetranomicon.LOGGER.info("skyjade weapon final damage: " + event.getAmount());
                }
            }
        }
    }

    private static double calculateSkyjadeBuff(ItemStack stack, double baseValue) {
        return baseValue / (2.0 * (double) stack.getDamageValue() / (double) stack.getMaxDamage() + 0.5);
    }

}
