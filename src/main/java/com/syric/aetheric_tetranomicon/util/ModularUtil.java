package com.syric.aetheric_tetranomicon.util;

import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.ModularBladedItem;
import se.mickelus.tetra.items.modular.impl.ModularDoubleHeadedItem;
import se.mickelus.tetra.items.modular.impl.ModularSingleHeadedItem;

public class ModularUtil {

    public static boolean isModularMeleeWeapon(ItemStack itemStack) {
        if ((itemStack.getItem() instanceof ModularItem item)) {

            if (item instanceof ModularSingleHeadedItem) {
                for (String s : item.getMajorModuleKeys()) {
                    if (s.startsWith("spearhead")) {
                        return true;
                    }
                }
            }
            if (item instanceof ModularBladedItem) {
                for (String s : item.getMajorModuleKeys()) {
                    if (s.startsWith("throwing_knife") || s.startsWith("basic_blade") || s.startsWith("heavy_blade") || s.startsWith("short_blade")) {
                        return true;
                    }
                }
            }
            if (item instanceof ModularDoubleHeadedItem) {
                for (String s : item.getMajorModuleKeys()) {
                    if (s.startsWith("basic_axe")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isModularTool(ItemStack itemStack) {
        if ((itemStack.getItem() instanceof ModularItem item)) {

            if (item instanceof ModularDoubleHeadedItem) {
                return true;
            }
            if (item instanceof ModularSingleHeadedItem) {
                for (String s : item.getMajorModuleKeys()) {
                    if (s.startsWith("basic_shovel")) {
                        return true;
                    }
                }
            }
            if (item instanceof ModularBladedItem) {
                for (String s : item.getMajorModuleKeys()) {
                    if (s.startsWith("machete")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
