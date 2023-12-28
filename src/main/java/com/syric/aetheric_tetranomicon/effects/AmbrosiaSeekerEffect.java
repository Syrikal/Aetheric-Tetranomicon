package com.syric.aetheric_tetranomicon.effects;

import com.aetherteam.aether.event.hooks.AbilityHooks;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.tools.abilities.HolystoneTool;
import com.aetherteam.aether.item.tools.holystone.HolystonePickaxeItem;
import com.syric.aetheric_tetranomicon.AethericTetranomicon;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
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
                AethericTetranomicon.LOGGER.info("detected modular holystone tool, triggering ability");
                if (!level.isClientSide() && blockState.getDestroySpeed(level, blockPos) > 0.0F && heldStack.isCorrectToolForDrops(blockState) && player.getRandom().nextInt(5) == 0) {
                    ItemEntity itemEntity = new ItemEntity(level, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, new ItemStack((ItemLike)AetherItems.AMBROSIUM_SHARD.get()));
                    level.addFreshEntity(itemEntity);
                }
            }
        }
    }

}
