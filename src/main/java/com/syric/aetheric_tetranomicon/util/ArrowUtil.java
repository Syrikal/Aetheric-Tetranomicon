package com.syric.aetheric_tetranomicon.util;

import com.syric.aetheric_tetranomicon.effects.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.bow.ModularBowItem;
import se.mickelus.tetra.items.modular.impl.crossbow.ModularCrossbowItem;

public class ArrowUtil {

    public static void addTags(ItemStack bowStack, AbstractArrow arrow, Player player, int drawProgress) {

        if (bowStack.getItem() instanceof ModularItem modularItem) {
            if (modularItem instanceof ModularBowItem || modularItem instanceof ModularCrossbowItem) {
                boolean aetheric = modularItem.getEffectLevel(bowStack, AethericEffect.aetheric) > 0;
                boolean ambrosia_seeker = modularItem.getEffectLevel(bowStack, AmbrosiaSeekerEffect.ambrosia_seeker_weapon) > 0;
                boolean levitator = modularItem.getEffectLevel(bowStack, LevitatorEffect.levitator_weapon) > 0;
                boolean tenacity = modularItem.getEffectLevel(bowStack, TenacityEffect.tenacity_weapon) > 0;
                boolean phoenix = modularItem.getEffectLevel(bowStack, PhoenixEffect.phoenix) > 0;


                if (aetheric) {
                    arrow.addTag("aetheric");
                }
                if (ambrosia_seeker) {
                    arrow.addTag("ambrosia_seeker");
                    arrow.addTag("drawProgress_" + drawProgress);
                }
                if (levitator) {
                    arrow.addTag("levitator");
                    arrow.addTag("drawProgress_" + drawProgress);
                }
                if (tenacity) {
                    arrow.addTag("tenacity");
                    arrow.addTag("stackDamage_" + bowStack.getDamageValue());
                    arrow.addTag("maxDamage_" + bowStack.getMaxDamage());
                }
                if (phoenix) {
                    arrow.addTag("phoenix");
                    int time = EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAMING_ARROWS, player) > 0 ? 40 : 20;
                    arrow.addTag("phoenixTime_" + time);
                }
            }
        }
    }
}
