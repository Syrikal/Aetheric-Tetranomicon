package com.syric.aetheric_tetranomicon.mixin;

import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.loot.functions.DoubleDrops;
import com.syric.aetheric_tetranomicon.AethericTetranomicon;
import com.syric.aetheric_tetranomicon.effects.HarvesterEffect;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import se.mickelus.tetra.items.modular.ModularItem;

@Mixin(DoubleDrops.class)
public class MixinDoubleDrops {

    @ModifyVariable(method = "run", at = @At("STORE"), ordinal = 0, remap = false)
    private ItemStack injected(ItemStack candidate) {
        if (candidate.getItem() instanceof ModularItem modularItem) {
            int harvesterLevel = modularItem.getEffectLevel(candidate, HarvesterEffect.harvester);
            if (harvesterLevel > 0) {
                AethericTetranomicon.LOGGER.info("detected modular skyroot tool");
                return AetherItems.SKYROOT_PICKAXE.get().getDefaultInstance();
            }
        }
        return candidate;
    }

}
