package com.sjgg.forgemodding.item.custom;

import com.sjgg.forgemodding.entity.SonicBulletEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SonicARItem extends Item {
    public SonicARItem(Properties pProperties) {
        super(pProperties);
    }

    // [우클릭 애니메이션] 활처럼 조준하는 모션을 취합니다.
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    // 우클릭을 최대 몇 틱 동안 지속할 수 있는지 설정 (72000틱 = 1시간)
    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    // [우클릭 시작] 누르는 순간 사용 상태(Using)로 진입합니다.
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    // [우클릭 유지 중] 매 틱마다 실행되며 실시간으로 열을 충전합니다.
    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
        if (!level.isClientSide) {
            // 우클릭을 지속한 총 시간(틱) 계산
            int duration = this.getUseDuration(stack) - count;

            // 1초(20틱)당 10 충전 -> 정확히 2틱당 1씩 충전
            if (duration % 2 == 0) {
                int currentHeat = getHeat(stack);
                if (currentHeat < 100) {
                    int nextHeat = currentHeat + 1;
                    stack.getOrCreateTag().putInt("Heat", nextHeat);

                    if (entity instanceof Player player) {
                        // 1초(20틱)마다 가벼운 충전음 재생 및 액션바에 현재 수치 표시
                        if (duration % 20 == 0 || nextHeat == 100) {
                            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                    SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.4F, 1.3F);
                        }
                        player.displayClientMessage(Component.literal("§e[열 충전 중] §c" + nextHeat + " / 100"), true);
                    }
                } else {
                    // 열이 100% 가득 찬 상태일 때의 경고 표시
                    if (entity instanceof Player player && duration % 20 == 0) {
                        player.displayClientMessage(Component.literal("§c[경고] 열(Heat)이 이미 최대치(100%)입니다!"), true);
                    }
                }
            }
        }
    }

    // [사격 로직] 좌클릭 시 서버 패킷을 통해 이 메소드가 실행됩니다.
    public void shoot(Level level, Player player, ItemStack stack) {
        int currentHeat = getHeat(stack);

        if (currentHeat <= 0) {
            return;
        }

        int currentFreq = SonicSniperItem.getFrequency(stack);

        // 1. 플레이어의 현재 시선 방향(Vector)을 가져옵니다.
        net.minecraft.world.phys.Vec3 lookVec = player.getLookAngle();

        // 2. 이펙트가 시야를 가리지 않도록 1.5블록 앞으로 스폰 위치를 밀어내고 높이를 보정합니다.
        double spawnX = player.getX() + lookVec.x * 1.5;
        double spawnY = player.getEyeY() - 0.2 + lookVec.y * 1.5; // 눈높이보다 약간 아래로
        double spawnZ = player.getZ() + lookVec.z * 1.5;

        // 3. 탄환 생성
        SonicBulletEntity bullet = new SonicBulletEntity(level, player, currentFreq, 1.0F);
        bullet.setPos(spawnX, spawnY, spawnZ);

        bullet.getPersistentData().putBoolean("IsAR", true);
        bullet.getPersistentData().putInt("AR_Heat", currentHeat);

        // 4. 총알 속도 및 발사
        bullet.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 5.0F, 0.0F);
        level.addFreshEntity(bullet);

        // 사운드 재생
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 0.35F, 2.0F);

        // 사격 성공 시 열 1 소모
        stack.getOrCreateTag().putInt("Heat", currentHeat - 1);
    }

    public static int getHeat(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("Heat")) {
            return stack.getTag().getInt("Heat");
        }
        return 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("현재 주파수: §b" + SonicSniperItem.getFrequency(stack) + " Hz"));
        tooltip.add(Component.literal("§e[Shift + 숫자 1~9] 키를 눌러 빠른 주파수 변경 가능"));
        tooltip.add(Component.literal("남은 열(Heat): §c" + getHeat(stack) + " / 100"));
        tooltip.add(Component.literal("§e[좌클릭] 연사"));
        tooltip.add(Component.literal("§7[우클릭] 열 충전 (초당 +10)"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}