package com.syric.aetheric_tetranomicon.util;

import com.syric.aetheric_tetranomicon.effects.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.AbstractArrow;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.bow.ModularBowItem;
import se.mickelus.tetra.items.modular.impl.crossbow.ModularCrossbowItem;

public class ArrowUtil {

    public static void addTags(ItemStack bowStack, AbstractArrow arrow, Player player) {

        if (bowStack.getItem() instanceof ModularItem modularItem) {
            if (modularItem instanceof ModularBowItem || modularItem instanceof ModularCrossbowItem) {
                boolean aetheric = modularItem.getEffectLevel(bowStack, AethericEffect.aetheric) > 0;
                boolean ambrosia_seeker = modularItem.getEffectLevel(bowStack, AmbrosiaSeekerEffect.ambrosia_seeker_weapon) > 0;
                boolean harvester = modularItem.getEffectLevel(bowStack, HarvesterEffect.harvester) > 0;
                boolean levitator = modularItem.getEffectLevel(bowStack, LevitatorEffect.levitator_weapon) > 0;
                boolean tenacity = modularItem.getEffectLevel(bowStack, TenacityEffect.tenacity_weapon) > 0;


                if (aetheric) {
                    arrow.addTag("aetheric");
                }
                if (ambrosia_seeker) {
                    arrow.addTag("ambrosia_seeker");
                }
                if (harvester) {
                    arrow.addTag("harvester");
                }
                if (levitator) {
                    arrow.addTag("levitator");
                }
                if (tenacity) {
                    arrow.addTag("tenacity");
                }
            }
        }
    }
}
