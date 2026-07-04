package com.sjgg.forgemodding.events;

import com.sjgg.forgemodding.Forgemodding;
import com.sjgg.forgemodding.item.custom.SonicSniperItem;
import com.sjgg.forgemodding.item.custom.SonicPistolItem;
import com.sjgg.forgemodding.networks.ModMessages;
import com.sjgg.forgemodding.networks.PacketUpdateFrequency;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Forgemodding.MODID, value = Dist.CLIENT)
public class ClientInputHandler {

    // 현재 어떤 숫자 키가 주파수 조작용으로 잠겨 있는지 상태를 저장하는 변수 (물려있는 키 코드 저장)
    private static int lockedNumericKeyCode = -1;

    // [1] 마우스 휠 스크롤 기능
    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            ItemStack stack = mc.player.getMainHandItem();
            if (stack.getItem() instanceof SonicSniperItem || stack.getItem() instanceof SonicPistolItem) {
                if (Screen.hasShiftDown()) {
                    event.setCanceled(true); // 핫바 스크롤 취소

                    int currentFreq = SonicSniperItem.getFrequency(stack);
                    boolean isScrollUp = event.getScrollDelta() > 0;

                    int nextFreq;
                    if (isScrollUp) {
                        nextFreq = (currentFreq >= 900) ? 100 : currentFreq + 100;
                    } else {
                        nextFreq = (currentFreq <= 100) ? 900 : currentFreq - 100;
                    }

                    ModMessages.sendToServer(new PacketUpdateFrequency(nextFreq));
                }
            }
        }
    }

    // [2] Shift를 떼도 숫자를 떼기 전까지 슬롯 체인지를 방지하는 로직 적용
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        ItemStack stack = mc.player.getMainHandItem();
        if (stack.getItem() instanceof SonicSniperItem || stack.getItem() instanceof SonicPistolItem) {

            int keyCode = event.getKey();

            // 입력된 키가 숫자 1~9 혹은 누메릭 패드 1~9인지 확인
            boolean isNumberKey = (keyCode >= InputConstants.KEY_1 && keyCode <= InputConstants.KEY_9) ||
                    (keyCode >= InputConstants.KEY_NUMPAD1 && keyCode <= InputConstants.KEY_NUMPAD9);

            if (isNumberKey) {
                // 1. 키를 처음 누른 순간(PRESS)이고 Shift가 눌려있다면 -> 이 숫자 키를 '슬롯 이동 불가 상태'로 잠금 처리
                if (event.getAction() == InputConstants.PRESS && Screen.hasShiftDown()) {
                    lockedNumericKeyCode = keyCode;

                    int numberPressed = -1;
                    if (keyCode >= InputConstants.KEY_1 && keyCode <= InputConstants.KEY_9) {
                        numberPressed = keyCode - InputConstants.KEY_1 + 1;
                    } else {
                        numberPressed = keyCode - InputConstants.KEY_NUMPAD1 + 1;
                    }

                    // 처음 누른 타이밍에 주파수 패킷 발송
                    int calculatedFreq = numberPressed * 100;
                    ModMessages.sendToServer(new PacketUpdateFrequency(calculatedFreq));
                }

                // 2. 현재 누르고 있는 숫자 키가 잠금된 키라면 (Shift를 뗐더라도 숫자 키를 아직 누르고 있는 상태 포함)
                if (lockedNumericKeyCode == keyCode) {

                    // Shift 키의 누름 상태 백업
                    boolean isShiftPressedDown = mc.options.keyShift.isDown();

                    // 핫바 슬롯 체인지 입력을 완전히 무력화(소모)
                    for (KeyMapping hotbarKey : mc.options.keyHotbarSlots) {
                        while (hotbarKey.consumeClick()) {
                            // 슬롯 이동 비활성화
                        }
                    }

                    // Shift 상태 원상복구 및 플레이어 웅크리기 유지
                    mc.options.keyShift.setDown(isShiftPressedDown);
                    if (isShiftPressedDown && mc.player != null) {
                        mc.player.setShiftKeyDown(true);
                    }

                    // 3. 사용자가 마침내 숫자 키에서 손가락을 완전히 뗐다면(RELEASE) -> 잠금 해제
                    if (event.getAction() == InputConstants.RELEASE) {
                        lockedNumericKeyCode = -1;
                    }
                }
            }
        } else {
            // 소닉 무기를 들고 있지 않다면 잠금 변수 초기화
            lockedNumericKeyCode = -1;
        }
    }
}