package com.sjgg.forgemodding.item;

import com.sjgg.forgemodding.Forgemodding;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SonicItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Forgemodding.MODID);

    public static final RegistryObject<Item> SONIC_SNIPER = ITEMS.register("sonic_sniper",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SONIC_PISTOL = ITEMS.register("sonic_pistol",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SONIC_AR = ITEMS.register("sonic_ar",
            () -> new Item(new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
