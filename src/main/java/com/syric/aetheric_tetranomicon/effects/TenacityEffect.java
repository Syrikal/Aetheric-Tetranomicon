package com.syric.aetheric_tetranomicon.effects;

import com.aetherteam.aether.item.EquipmentUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.ModularItem;

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
}
