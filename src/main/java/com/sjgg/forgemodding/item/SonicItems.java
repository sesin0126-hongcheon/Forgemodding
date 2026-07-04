package com.sjgg.forgemodding.item;

import com.sjgg.forgemodding.Forgemodding;
import com.sjgg.forgemodding.item.custom.SonicPistolItem;
import com.sjgg.forgemodding.item.custom.SonicSniperItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SonicItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Forgemodding.MODID);

    public static final RegistryObject<Item> SONIC_SNIPER = ITEMS.register("sonic_sniper",
            () -> new SonicSniperItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SONIC_PISTOL = ITEMS.register("sonic_pistol",
            () -> new SonicPistolItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SONIC_AR = ITEMS.register("sonic_ar",
            () -> new Item(new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
