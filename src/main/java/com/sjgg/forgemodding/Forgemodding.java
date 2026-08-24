package com.sjgg.forgemodding;

import com.mojang.logging.LogUtils;
import com.sjgg.forgemodding.entity.ModEntities;
import com.sjgg.forgemodding.item.SonicCreativeModTabs;
import com.sjgg.forgemodding.item.SonicItems;
import com.sjgg.forgemodding.networks.ModMessages;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Forgemodding.MODID)
public class Forgemodding {
    // MODID, 로거( 절대 건들지 말도록..) ------------------------------------------
    public static final String MODID = "forgemodding";
    private static final Logger LOGGER = LogUtils.getLogger();

    // 메인 --------------------------------------------------------------------

    public Forgemodding() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        GeckoLib.initialize();

        SonicCreativeModTabs.register(modEventBus);

        SonicItems.register(modEventBus);

        ModMessages.register();

        ModEntities.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    // ------------------------------------------------------------------------
    @SuppressWarnings("removal")
    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(SonicItems.SONIC_SNIPER);
            event.accept(SonicItems.SONIC_PISTOL);
            event.accept(SonicItems.SONIC_AR);
        }
    }
}
