package com.sjgg.forgemodding.events;

import com.sjgg.forgemodding.Forgemodding;
import com.sjgg.forgemodding.entity.ModEntities;
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
        // 우리의 소닉 탄환 엔티티(SonicBulletEntity)에 '형태가 없는 빈 렌더러(NoopRenderer)'를 매핑합니다.
        // 이렇게 하면 크래시가 나지 않고, 우리가 구현한 파란색 소닉붐 파티클만 멋지게 일직선으로 보입니다.
        event.registerEntityRenderer(ModEntities.SONIC_BULLET.get(), NoopRenderer::new);
    }
}