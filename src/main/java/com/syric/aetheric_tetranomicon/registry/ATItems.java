package com.syric.aetheric_tetranomicon.registry;

import com.aetherteam.aether.item.AetherCreativeTabs;
import com.syric.aetheric_tetranomicon.AethericTetranomicon;
import net.minecraft.world.item.Item;
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
    public static final RegistryObject<Item> VALKYRIAN_SCRAP = ITEMS.register("valkyrian_scrap", () -> new Item(new Item.Properties().tab(AetherCreativeTabs.AETHER_INGREDIENTS)));
    public static final RegistryObject<Item> PHOENIX_SCRAP = ITEMS.register("phoenix_scrap", () -> new Item(new Item.Properties().tab(AetherCreativeTabs.AETHER_INGREDIENTS)));

    public ATItems() {
    }

}
