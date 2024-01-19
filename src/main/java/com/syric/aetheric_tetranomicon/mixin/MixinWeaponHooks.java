package com.syric.aetheric_tetranomicon.mixin;

import com.aetherteam.aether.event.hooks.AbilityHooks;
import com.syric.aetheric_tetranomicon.AethericTetranomicon;
import com.syric.aetheric_tetranomicon.effects.AethericEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import se.mickelus.tetra.items.modular.ModularItem;

@Mixin(AbilityHooks.WeaponHooks.class)
public class MixinWeaponHooks {

    @Inject(method = "reduceWeaponEffectiveness", at = @At("HEAD"), cancellable = true, remap = false)
    private static void injected(LivingEntity target, Entity source, float damage, CallbackInfoReturnable<Float> cir) {

        boolean aetheric = false;
//        AethericTetranomicon.LOGGER.info("WeaponHooks mixin checking weapon effectiveness. initial damage: " + damage);

        //Check for melee weapon
        if (source instanceof LivingEntity livingEntity) {
            ItemStack stack = livingEntity.getMainHandItem();
            if (!stack.isEmpty() && !stack.getAttributeModifiers(EquipmentSlot.MAINHAND).isEmpty() && !stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE).isEmpty()) {
                if (stack.getItem() instanceof ModularItem modularItem) {
//                    AethericTetranomicon.LOGGER.info("WeaponHooks mixin detected modular tool");
                    aetheric = modularItem.getEffectLevel(stack, AethericEffect.aetheric) > 0;
//                    AethericTetranomicon.LOGGER.info("AbilityHooks mixin detected Aetheric level " + modularItem.getEffectLevel(stack, AethericEffect.aetheric));
                }
            }
        }

        //Check for ranged weapon
        else if (source instanceof Projectile projectile) {
            aetheric = projectile.getTags().contains("aetheric");
            AethericTetranomicon.LOGGER.info("AbilityHooks mixin detected Aetheric projectile");
        }

        //If aetheric, return damage immediately
        if (aetheric) {
            AethericTetranomicon.LOGGER.info("AbilityHooks mixin detected modular Aetheric weapon, not altering damage of " + damage);
            cir.setReturnValue(damage);
            cir.cancel();
        }
    }
}
