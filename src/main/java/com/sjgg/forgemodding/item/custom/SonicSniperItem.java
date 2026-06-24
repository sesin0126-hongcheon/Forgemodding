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

public class SonicSniperItem extends Item {
    public SonicSniperItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!player.getCooldowns().isOnCooldown(this)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    // ---------------- [ 실시간 충전 시간 표시 UI 로직 ] ----------------
    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int count) {
        if (livingEntity instanceof Player player) {
            // 전체 시간(72000)에서 남은 시간(count)을 빼서 현재 몇 틱 동안 모았는지 계산
            int chargeTicks = this.getUseDuration(stack) - count;

            // 틱 단위를 초 단위로 변환 (예: 25틱 -> 1.2초)
            float chargeSeconds = chargeTicks / 20.0F;

            // 시각적인 진행도 바(Progress Bar) 만들기 (최대 3초 = 60틱 기준)
            // 3초를 채우면 꽉 찬 게이지가 보입니다.
            int maxBarLength = 10;
            int filledLength = Math.min(maxBarLength, (chargeTicks * maxBarLength) / 60);

            StringBuilder progressBar = new StringBuilder();
            for (int i = 0; i < maxBarLength; i++) {
                if (i < filledLength) {
                    progressBar.append("■"); // 충전된 칸
                } else {
                    progressBar.append("□"); // 남은 칸
                }
            }

            // UI 색상 동적 변경 (1초 미만은 빨간색, 1~3초는 노란색, 3초 완료는 하늘색)
            String colorCode = "§c"; // 기본 빨강
            if (chargeTicks >= 60) {
                colorCode = "§b[MAX] "; // 3초 완충시 하늘색
            } else if (chargeTicks >= 20) {
                colorCode = "§e"; // 1초 이상은 노란색
            }

            // 플레이어 화면 핫바 위(액션바)에 실시간으로 표시 (매 틱마다 갱신됨)
            player.displayClientMessage(
                    Component.literal("충전 중: " + colorCode + progressBar.toString() + " (" + String.format("%.1f", chargeSeconds) + "초)"),
                    true
            );
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            int chargeTicks = this.getUseDuration(stack) - timeLeft;

            // 최소 1초 충전 조건 확인
            if (chargeTicks < 20) {
                player.displayClientMessage(Component.literal("§c충전 시간이 부족합니다! (최소 1초 충전 필요)"), true);
                return;
            }

            if (!level.isClientSide) {
                player.getCooldowns().addCooldown(this, 20); // 쿨타임 1초
                int currentFreq = getFrequency(stack);

                // 충전 배율 계산
                float chargeMultiplier = chargeTicks / 20.0F;

                // 최대 충전 시간 3초 제한 적용
                if (chargeMultiplier > 3.0F) {
                    chargeMultiplier = 3.0F;
                }

                // 탄환 생성 및 발사
                SonicBulletEntity bullet = new SonicBulletEntity(level, player, currentFreq, chargeMultiplier);
                bullet.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 5.0F, 0.0F);
                level.addFreshEntity(bullet);

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 3.0F, 1.0F);
            }
        }
    }

    public static int getFrequency(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("Frequency")) {
            return stack.getTag().getInt("Frequency");
        }
        return 100;
    }

    public static void cycleFrequency(ItemStack stack, boolean increase) {
        int current = getFrequency(stack);
        if (increase) {
            current = (current >= 900) ? 100 : current + 100;
        } else {
            current = (current <= 100) ? 900 : current - 100;
        }
        stack.getOrCreateTag().putInt("Frequency", current);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("현재 주파수: §b" + getFrequency(stack) + " Hz"));
        tooltip.add(Component.literal("§e우클릭을 1초~3초 동안 눌러 충전 후 발사하세요. (최대 3배)"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}