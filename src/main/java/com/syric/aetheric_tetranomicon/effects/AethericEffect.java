package com.syric.aetheric_tetranomicon.effects;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.gui.stats.bar.GuiStatBar;
import se.mickelus.tetra.gui.stats.getter.IStatGetter;
import se.mickelus.tetra.gui.stats.getter.LabelGetterBasic;
import se.mickelus.tetra.gui.stats.getter.StatGetterEffectLevel;
import se.mickelus.tetra.gui.stats.getter.TooltipGetterNone;
import se.mickelus.tetra.items.modular.impl.holo.gui.craft.HoloStatsGui;

public class AethericEffect {
    public static final ItemEffect aetheric = ItemEffect.get("aetheric_tetranomicon:aetheric");

    @OnlyIn(Dist.CLIENT)
    public static void addBars(FMLClientSetupEvent event) {
        IStatGetter aethericGetter = new StatGetterEffectLevel(aetheric, 1.0);
        GuiStatBar aethericBar = new GuiStatBar(0, 0, 59, "tetra.stats.aetheric", 0.0, 1.0, false, aethericGetter, LabelGetterBasic.noLabel, new TooltipGetterNone("tetra.stats.aetheric.tooltip"));

        WorkbenchStatsGui.addBar(aethericBar);
        HoloStatsGui.addBar(aethericBar);
    }
}
