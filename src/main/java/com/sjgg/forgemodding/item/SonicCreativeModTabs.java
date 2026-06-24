package com.sjgg.forgemodding.item;

import com.sjgg.forgemodding.Forgemodding;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SonicCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Forgemodding.MODID);

    public static final RegistryObject<CreativeModeTab> SONIC_ITEMS = CREATIVE_MODE_TABS.register("sonic_items",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(SonicItems.SONIC_SNIPER.get()))
                    .title(Component.translatable("creativetab.sonic_items"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(SonicItems.SONIC_SNIPER.get());
                        pOutput.accept(SonicItems.SONIC_PISTOL.get());
                        pOutput.accept(SonicItems.SONIC_AR.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
