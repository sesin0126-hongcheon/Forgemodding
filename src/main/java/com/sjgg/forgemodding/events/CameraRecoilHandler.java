package com.sjgg.forgemodding.events;

import com.sjgg.forgemodding.Forgemodding;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Forgemodding.MODID, value = Dist.CLIENT)
public class CameraRecoilHandler {
    private static float recoilAngle = 0.0F;
    private static float prevRecoilAngle = 0.0F;
    private static float recoilVelocity = 0.0F;

    // 외부(총 발사 시)에서 반동을 추가하는 메서드
    public static void addRecoil(float amount) {
        recoilVelocity += amount;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.ClientTickEvent.Phase.END) {
            prevRecoilAngle = recoilAngle;

            // 반동 물리 계산 (확 튀어올랐다가 부드럽게 제자리로 돌아오는 스프링 감쇠 효과)
            recoilAngle += recoilVelocity;
            recoilVelocity *= 0.5F; // 속도 감쇠
            recoilAngle *= 0.75F;   // 원래 자리로 돌아오는 복귀력
        }
    }

    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        float partialTick = Minecraft.getInstance().getPartialTick();
        // 프레임 사이에 보간(Interpolation)을 거쳐 화면을 아주 부드럽게 밀어올림
        float currentRecoil = prevRecoilAngle + (recoilAngle - prevRecoilAngle) * partialTick;

        event.setPitch(event.getPitch() - currentRecoil);
    }
}