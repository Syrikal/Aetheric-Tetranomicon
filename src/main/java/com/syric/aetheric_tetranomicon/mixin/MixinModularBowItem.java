package com.syric.aetheric_tetranomicon.mixin;

import com.google.common.collect.ImmutableList;
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
import se.mickelus.tetra.items.modular.impl.bow.ModularBowItem;

import java.util.function.Function;

@Mixin(ModularBowItem.class)
public class MixinModularBowItem {

    @Inject(method = "fireProjectile", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;shootFromRotation(Lnet/minecraft/world/entity/Entity;FFFFF)V",
            shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void applyTags(ItemStack itemStack, Level world, ArrowItem ammoItem, ItemStack ammoStack, ImmutableList<Function<AbstractArrow, AbstractArrow>> projectileRemappers, Player player, float basePitch, float yaw, float projectileVelocity, float accuracy, int drawProgress, double strength, int powerLevel, int punchLevel, int flameLevel, int piercingLevel, boolean hasSuspend, boolean infiniteAmmo, CallbackInfo ci, AbstractArrow projectile) {
        ArrowUtil.addTags(itemStack, projectile, player, drawProgress);
    }

}