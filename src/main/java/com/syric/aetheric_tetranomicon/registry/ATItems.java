package com.syric.aetheric_tetranomicon.registry;

import com.aetherteam.aether.item.AetherCreativeTabs;
import com.aetherteam.aether.item.AetherItems;
import com.syric.aetheric_tetranomicon.AethericTetranomicon;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(
        modid = "aetheric_tetranomicon",
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ATItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AethericTetranomicon.MODID);
    public static final RegistryObject<Item> VALKYRIAN_SCRAP = ITEMS.register("valkyrian_scrap", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PHOENIX_SCRAP = ITEMS.register("phoenix_scrap", () -> new Item(new Item.Properties()));

    public ATItems() {
    }

    @SubscribeEvent
    public static void buildCreativeModeTabs(BuildCreativeModeTabContentsEvent event) {
        CreativeModeTab tab = event.getTab();
        if (tab == AetherCreativeTabs.AETHER_INGREDIENTS.get()) {
            event.getEntries().putAfter(new ItemStack(AetherItems.SWET_BALL.get()), new ItemStack(VALKYRIAN_SCRAP.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.getEntries().putAfter(new ItemStack(VALKYRIAN_SCRAP.get()), new ItemStack(PHOENIX_SCRAP.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

}
