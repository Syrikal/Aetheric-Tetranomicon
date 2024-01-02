package com.syric.aetheric_tetranomicon.mixin;

import com.aetherteam.aether.event.hooks.AbilityHooks;
import com.syric.aetheric_tetranomicon.AethericTetranomicon;
import com.syric.aetheric_tetranomicon.effects.AethericEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.mickelus.tetra.items.modular.ModularItem;

@Mixin(AbilityHooks.ToolHooks.class)
public class MixinToolHooks {

    @Inject(method = "reduceToolEffectiveness", at = @At("HEAD"), cancellable = true, remap = false)
    private static void injected(Player player, BlockState state, ItemStack stack, float speed, CallbackInfoReturnable<Float> cir) {
//        AethericTetranomicon.LOGGER.info("AbilityHooks mixin checking tool effectiveness");
        if (stack.getItem() instanceof ModularItem modularItem) {
//            AethericTetranomicon.LOGGER.info("AbilityHooks mixin detected modular tool");
            int aethericLevel = modularItem.getEffectLevel(stack, AethericEffect.aetheric);
//            AethericTetranomicon.LOGGER.info("AbilityHooks mixin detected Aetheric level " + aethericLevel);
            if (aethericLevel > 0) {
//                AethericTetranomicon.LOGGER.info("AbilityHooks mixin detected modular Aetheric tool, not altering speed");
                cir.cancel();
                cir.setReturnValue(speed);
            }
        }
    }

}
