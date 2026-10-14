package com.sjgg.forgemodding.events;

import com.sjgg.forgemodding.Forgemodding;
import com.sjgg.forgemodding.entity.ModEntities;
import com.sjgg.forgemodding.init.ModEntityTypes;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// bus = Mod.EventBusSubscriber.Bus.MOD 로 설정해야 엔티티 렌더러 등록 이벤트를 잡을 수 있습니다.
@Mod.EventBusSubscriber(modid = Forgemodding.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SONIC_BULLET.get(), NoopRenderer::new);
        EntityRenderers.register(ModEntityTypes.SONIC_GRENADE.get(), ThrownItemRenderer::new);
    }
}