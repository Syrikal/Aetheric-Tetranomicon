package com.syric.aetheric_tetranomicon.mixin;

import com.syric.aetheric_tetranomicon.effects.VeridiumInfusionEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.mickelus.tetra.items.modular.ModularItem;

import java.util.List;

@Mixin(ModularItem.class)
public class MixinModularItem {

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void injected(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
        tooltip.addAll(VeridiumInfusionEffect.addTooltip(stack));
    }
}
