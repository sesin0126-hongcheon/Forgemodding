package com.sjgg.forgemodding.events;

import com.sjgg.forgemodding.Forgemodding;
import com.sjgg.forgemodding.item.custom.SonicSniperItem;
import com.sjgg.forgemodding.networks.ModMessages;
import com.sjgg.forgemodding.networks.PacketUpdateFrequency;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Forgemodding.MODID, value = Dist.CLIENT)
public class ClientInputHandler {
    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getMainHandItem().getItem() instanceof SonicSniperItem) {
            // Shift 키를 누르고 있는 경우
            if (Screen.hasShiftDown()) {
                event.setCanceled(true); // 핫바 스크롤 잠금

                boolean isScrollUp = event.getScrollDelta() > 0;
                // 서버에 패킷 전송
                ModMessages.sendToServer(new PacketUpdateFrequency(isScrollUp));
            }
        }
    }
}