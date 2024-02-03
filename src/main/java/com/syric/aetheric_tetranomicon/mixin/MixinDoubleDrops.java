package com.syric.aetheric_tetranomicon.mixin;

import com.aetherteam.aether.loot.functions.DoubleDrops;
import com.syric.aetheric_tetranomicon.effects.HarvesterEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.mickelus.tetra.items.modular.ModularItem;

@Mixin(DoubleDrops.class)
public class MixinDoubleDrops {

    @Inject(method = "run", at = @At("RETURN"), cancellable = true)
    private void injected(ItemStack dropStack, LootContext context, CallbackInfoReturnable<ItemStack> cir) {

//        AethericTetranomicon.LOGGER.info("DoubleDrops mixin checking whether to double drops. Initial drop size: " + dropStack.getCount());
//        AethericTetranomicon.LOGGER.info("CIR attempting to return stack of size: " + cir.getReturnValue().getCount());


        ItemStack toolStack = context.getParamOrNull(LootContextParams.TOOL);
        BlockState blockState = context.getParamOrNull(LootContextParams.BLOCK_STATE);

        assert toolStack != null;
        if (toolStack.getItem() instanceof ModularItem) {
//            AethericTetranomicon.LOGGER.info("DoubleDrops mixin detected modular item.");
            cir.setReturnValue(HarvesterEffect.doubleDrops(dropStack, toolStack, blockState));
            cir.cancel();
//            AethericTetranomicon.LOGGER.info("DoubleDrops mixin has set drop size to " + cir.getReturnValue().getCount());
        }
//        AethericTetranomicon.LOGGER.info("DoubleDrops mixin did not detect modular item. Theoretically, should do nothing.");

    }

}
