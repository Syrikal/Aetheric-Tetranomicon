package com.syric.aetheric_tetranomicon.effects;

import com.aetherteam.aether.event.hooks.AbilityHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.ModularItem;

public class AmbrosiaSeekerEffect {
    public static final ItemEffect ambrosia_seeker = ItemEffect.get("aetheric_tetranomicon:ambrosia_seeker");

    /**
     * Holystone tools can drop ambrosia when used.
     */
    @SubscribeEvent
    public void breakEvent(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level level = player.level();
        BlockPos blockPos = event.getPos();
        ItemStack heldStack = player.getMainHandItem();
        BlockState blockState = event.getState();

        if (heldStack.getItem() instanceof ModularItem item) {
            int effectlevel = item.getEffectLevel(heldStack, ambrosia_seeker);

            if (!event.isCanceled() && effectlevel > 0) {
                AbilityHooks.ToolHooks.handleHolystoneToolAbility(player, level, blockPos, heldStack, blockState);
            }
        }
    }
}
