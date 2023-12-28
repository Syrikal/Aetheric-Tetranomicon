package com.syric.aetheric_tetranomicon.registry;

import com.syric.aetheric_tetranomicon.AethericTetranomicon;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ATItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AethericTetranomicon.MODID);
    public static final RegistryObject<Item> VALKYRIAN_SCRAP = ITEMS.register("valkyrian_scrap", () -> new Item(new Item.Properties()));

    public ATItems() {
    }

}
