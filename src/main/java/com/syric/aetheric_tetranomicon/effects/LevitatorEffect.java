package com.syric.aetheric_tetranomicon.effects;

import se.mickelus.tetra.effect.ItemEffect;

public class LevitatorEffect {
    public static final ItemEffect levitator = ItemEffect.get("aetheric_tetranomicon:levitator");

//    /**
//     * Gravitite tools can be used to levitate blocks.
//     * @param event
//     */
//    @SubscribeEvent
//    public void attackEvent(LivingHurtEvent event) {
//        Entity source = event.getSource().getEntity();
//
//        if (source instanceof Player player) {
//            ItemStack heldStack = player.getMainHandItem();
//
//            if (heldStack.getItem() instanceof ModularItem item) {
//                int level = item.getEffectLevel(heldStack, frostbite);
//
//                if (level > 0) {
//                    event.getEntity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 2));
//                    //player.sendMessage(new StringTextComponent("Applied Slowness 2"), player.getUUID());
//                }
//            }
//        }
//    }

}
