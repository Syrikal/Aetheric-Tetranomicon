package com.syric.aetheric_tetranomicon.effects;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.item.AetherItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;
import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.stats.getter.TooltipGetterNone;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

public class ValkyrieEffect {
    public static final ItemEffect valkyrie = ItemEffect.get("aetheric_tetranomicon:valkyrie");

    /**
     * @param event Valkyrian tools drop golden amber from golden oak logs.
     */
    @SubscribeEvent
    public void breakEvent(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level level = player.level();
        BlockPos blockPos = event.getPos();
        ItemStack heldStack = player.getMainHandItem();
        BlockState blockState = event.getState();

        if (heldStack.getItem() instanceof ModularItem item) {
            int effectlevel = item.getEffectLevel(heldStack, valkyrie);

            boolean notCanceled = !event.isCanceled();
            boolean valkyrian = effectlevel > 0;
            boolean goldenOak = blockState.is(AetherBlocks.GOLDEN_OAK_LOG.get()) || blockState.is(AetherBlocks.GOLDEN_OAK_WOOD.get());
            boolean noSilkTouch = !EnchantmentHelper.hasSilkTouch(heldStack);

            if (valkyrian && notCanceled && goldenOak && noSilkTouch) {
                boolean two = level.getRandom().nextBoolean();
                ItemEntity itemEntity = new ItemEntity(level, (double) blockPos.getX() + 0.5, (double) blockPos.getY() + 0.5, (double) blockPos.getZ() + 0.5, new ItemStack(AetherItems.GOLDEN_AMBER.get(), two ? 2 : 1));
                level.addFreshEntity(itemEntity);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void addBars(FMLClientSetupEvent event) {
        IStatGetter valkyrieGetter = new StatGetterEffectLevel(valkyrie, 1.0);
        GuiStatBar valkyrieBar = new GuiStatBar(0, 0, 59, "tetra.stats.valkyrie", 0.0, 1.0, false, valkyrieGetter, LabelGetterBasic.noLabel, new TooltipGetterNone("tetra.stats.valkyrie.tooltip"));

        WorkbenchStatsGui.addBar(valkyrieBar);
        HoloStatsGui.addBar(valkyrieBar);
    }
}
