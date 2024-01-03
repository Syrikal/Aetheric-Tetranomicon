package com.syric.aetheric_tetranomicon.registry;

import com.aetherteam.aether.item.AetherCreativeTabs;
import com.aetherteam.aether.item.AetherItems;
import com.syric.aetheric_tetranomicon.AethericTetranomicon;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ATItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AethericTetranomicon.MODID);
    public static final RegistryObject<Item> VALKYRIAN_SCRAP = ITEMS.register("valkyrian_scrap", () -> new Item(new Item.Properties()));

    public ATItems() {
    }

    @SubscribeEvent
    public static void buildCreativeModeTabs(BuildCreativeModeTabContentsEvent event) {
        ResourceKey<CreativeModeTab> tab = event.getTabKey();
        if (tab == AetherCreativeTabs.AETHER_INGREDIENTS.getKey()) {
            event.getEntries().putAfter(new ItemStack(AetherItems.SWET_BALL.get()), new ItemStack((ItemLike) VALKYRIAN_SCRAP.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

    }

}
