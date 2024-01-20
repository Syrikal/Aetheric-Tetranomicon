package com.syric.aetheric_tetranomicon.mixin;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public class MixinAbstractArrow {
    @Shadow
    protected boolean inGround;
    @Shadow
    protected int inGroundTime;

    public MixinAbstractArrow() {
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;tick()V", shift = At.Shift.AFTER))
    private void tick(CallbackInfo ci) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;

        boolean phoenix = arrow.getTags().contains("phoenix");
        boolean serverSide = !arrow.level().isClientSide;

        if (phoenix && serverSide) {
            if (this.inGround) {
                if (this.inGroundTime % 5 == 0) {
                    for (int i = 0; i < 1; ++i) {
                        this.spawnParticles(arrow);
                    }
                }
            } else {
                for (int i = 0; i < 2; ++i) {
                    this.spawnParticles(arrow);
                }
            }
        }
    }

    private void spawnParticles(AbstractArrow arrow) {
        Level var3 = arrow.level();
        if (var3 instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLAME, arrow.getX() + serverLevel.getRandom().nextGaussian() / 5.0, arrow.getY() + serverLevel.getRandom().nextGaussian() / 3.0, arrow.getZ() + serverLevel.getRandom().nextGaussian() / 5.0, 1, 0.0, 0.0, 0.0, 0.0);
        }

    }
}