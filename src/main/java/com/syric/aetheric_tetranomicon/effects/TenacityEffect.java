package com.syric.aetheric_tetranomicon.effects;

import com.aetherteam.aether.item.EquipmentUtil;
import com.syric.aetheric_tetranomicon.AethericTetranomicon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.ModularItem;

public class TenacityEffect {
    public static final ItemEffect tenacity = ItemEffect.get("aetheric_tetranomicon:tenacity");

    /**
     * Zanite tools increase their mining speed as their durability decreases.
     * @param event
     */
    @SubscribeEvent
    public void mineEvent(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        ItemStack heldStack = player.getMainHandItem();
        if (heldStack.getItem() instanceof ModularItem item) {
            int level = item.getEffectLevel(heldStack, tenacity);
            if (level > 0 && !event.isCanceled()) {
//                AethericTetranomicon.LOGGER.info("detected modular zanite tool, triggering ability");
//                AethericTetranomicon.LOGGER.info("tool durability: " + heldStack.getDamageValue() + " out of " + heldStack.getMaxDamage());
                event.setNewSpeed((float) EquipmentUtil.calculateZaniteBuff(heldStack, event.getNewSpeed()));
            }
        }
    }

}
