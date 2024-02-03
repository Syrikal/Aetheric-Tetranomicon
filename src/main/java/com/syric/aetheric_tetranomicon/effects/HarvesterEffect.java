package com.syric.aetheric_tetranomicon.effects;

import com.aetherteam.aether.block.AetherBlockStateProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.ModularItem;

public class HarvesterEffect {
    public static final ItemEffect harvester = ItemEffect.get("aetheric_tetranomicon:harvester");

    public static ItemStack doubleDrops(ItemStack drop, ItemStack tool, BlockState state) {

//        AethericTetranomicon.LOGGER.info("Harvester effect checking whether to double drops.");


        if (tool != null && state != null) {
//            AethericTetranomicon.LOGGER.info("Harvester detected that tool and state are not null.");
            boolean noSilkTouch = tool.getEnchantmentLevel(Enchantments.SILK_TOUCH) == 0;
            boolean isDoubleDropBlock = state.getValue(AetherBlockStateProperties.DOUBLE_DROPS);
            boolean isCorrectTool = tool.isCorrectToolForDrops(state);
            boolean hasHarvesterEffect = false;

            if (tool.getItem() instanceof ModularItem modularItem) {
                int effectLevel = modularItem.getEffectLevel(tool, harvester);
                hasHarvesterEffect = effectLevel > 0;
            }

//            AethericTetranomicon.LOGGER.info(String.format("Harvester says... noSilkTouch: %s, isDoubleDropBlock: %s, isCorrectTool: %s, hasHarvesterEffect: %s", noSilkTouch, isDoubleDropBlock, isCorrectTool, hasHarvesterEffect));

            if (noSilkTouch && isDoubleDropBlock && isCorrectTool && hasHarvesterEffect) {
                drop.setCount(2 * drop.getCount());
            }
        }

//        AethericTetranomicon.LOGGER.info("Harvester effect returning drop stack of size " + drop.getCount() + ".");
        return drop;
    }
}
