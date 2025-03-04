package com.syric.aetheric_tetranomicon;

import com.mojang.logging.LogUtils;
import com.syric.aetheric_tetranomicon.effects.*;
import com.syric.aetheric_tetranomicon.registry.ATItems;
import com.syric.aetheric_tetranomicon.util.ArrowUtil;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AethericTetranomicon.MODID)
public class AethericTetranomicon {


    // Define mod id in a common place for everything to reference
    public static final String MODID = "aetheric_tetranomicon";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public AethericTetranomicon() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Add effect registration here
        MinecraftForge.EVENT_BUS.register(new AethericEffect());
        MinecraftForge.EVENT_BUS.register(new AmbrosiaSeekerEffect());
        MinecraftForge.EVENT_BUS.register(new BrittleEffect());
        MinecraftForge.EVENT_BUS.register(new HarvesterEffect());
        MinecraftForge.EVENT_BUS.register(new LevitatorEffect());
        MinecraftForge.EVENT_BUS.register(new PhoenixEffect());
        MinecraftForge.EVENT_BUS.register(new TenacityEffect());
        MinecraftForge.EVENT_BUS.register(new ValkyrieEffect());
        MinecraftForge.EVENT_BUS.register(new VeridiumInfusionEffect());
        MinecraftForge.EVENT_BUS.register(new ArrowUtil());


        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register the Deferred Register to the mod event bus so items get registered
        ATItems.ITEMS.register(modEventBus);
        modEventBus.addListener(this::addBars);

    }

    private void addBars(final FMLClientSetupEvent event) {
        AethericEffect.addBars(event);
        AmbrosiaSeekerEffect.addBars(event);
        BrittleEffect.addBars(event);
        HarvesterEffect.addBars(event);
        LevitatorEffect.addBars(event);
        PhoenixEffect.addBars(event);
        TenacityEffect.addBars(event);
//        ValkyrieEffect.addBars(event);
        VeridiumInfusionEffect.addBars(event);
    }

}
