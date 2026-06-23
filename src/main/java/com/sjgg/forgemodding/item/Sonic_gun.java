package com.sjgg.forgemodding.item;

import com.sjgg.forgemodding.Forgemodding;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Sonic_gun {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Forgemodding.MODID);

    public static final RegistryObject<Item> SONIC_GUN = ITEMS.register("sonic_gun",
            () -> new Item(new Item.Properties()));

    public  static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
