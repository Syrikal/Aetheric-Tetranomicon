package com.syric.aetheric_tetranomicon.mixin;

import com.aetherteam.aether.loot.modifiers.DoubleDropsModifier;
import com.syric.aetheric_tetranomicon.effects.HarvesterEffect;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.mickelus.tetra.items.modular.ModularItem;

@Mixin(DoubleDropsModifier.class)
public class MixinDoubleDropsModifier {

    @Inject(method = "doApply", at = @At("RETURN"), cancellable = true, remap = false)
    private void injected(ObjectArrayList<ItemStack> lootStacks, LootContext context, CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir) {
        Entity entity = (Entity) context.getParamOrNull(LootContextParams.DIRECT_KILLER_ENTITY);
        Entity target = (Entity) context.getParamOrNull(LootContextParams.THIS_ENTITY);

        if (entity instanceof LivingEntity livingEntity) {
            ItemStack toolStack = livingEntity.getMainHandItem();
            if (toolStack.getItem() instanceof ModularItem) {
//                AethericTetranomicon.LOGGER.info("DoubleDropsModifier mixin detected modular item.");
                cir.setReturnValue(HarvesterEffect.doubleKillDrops(livingEntity, target, lootStacks, toolStack));
                cir.cancel();
            }
        }
    }

}
