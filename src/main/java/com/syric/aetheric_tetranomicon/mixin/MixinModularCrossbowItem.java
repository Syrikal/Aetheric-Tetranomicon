package com.syric.aetheric_tetranomicon.mixin;

import com.syric.aetheric_tetranomicon.util.ArrowUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import se.mickelus.tetra.items.modular.impl.crossbow.ModularCrossbowItem;

@Mixin(ModularCrossbowItem.class)
public class MixinModularCrossbowItem {

    @Inject(method = "fireProjectile", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;setCritArrow(Z)V",
            shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void applyTags(Level world, ItemStack crossbowStack, ItemStack ammoStack, Player player, double yaw, boolean isDupe, CallbackInfo ci, boolean isDupe2, double strength, float velocityBonus, float projectileVelocity, ArrowItem ammoItem, AbstractArrow projectile) {
        ArrowUtil.addTags(crossbowStack, projectile, player);
    }

}
