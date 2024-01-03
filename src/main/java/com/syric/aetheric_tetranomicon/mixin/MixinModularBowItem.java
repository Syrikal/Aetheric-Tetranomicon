package com.syric.aetheric_tetranomicon.mixin;

import com.syric.aetheric_tetranomicon.util.ArrowUtil;
import net.minecraft.world.entity.LivingEntity;
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
import se.mickelus.tetra.items.modular.impl.bow.ModularBowItem;

@Mixin(ModularBowItem.class)
public class MixinModularBowItem {

    @Inject(method = "fireArrow", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;shootFromRotation(Lnet/minecraft/world/entity/Entity;FFFFF)V",
            shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void applyTags(ItemStack itemStack, Level world, LivingEntity entity, int timeLeft, CallbackInfo ci, Player player, ItemStack ammoStack, boolean playerInfinite, int drawProgress, double strength, float velocityBonus, int suspendLevel, float projectileVelocity, ArrowItem ammoItem, boolean infiniteAmmo, int count, double multishotSpread, float accuracy, int powerLevel, int punchLevel, int flameLevel, int piercingLevel, int i, double yaw, AbstractArrow projectile) {
        ArrowUtil.addTags(itemStack, projectile, player);
    }

}