package com.sjgg.forgemodding.item.custom;

import com.sjgg.forgemodding.entity.SonicBulletEntity;
import com.sjgg.forgemodding.item.client.SonicSniperRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
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
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class SonicSniperItem extends Item implements GeoItem {
    // GeckoLib 애니메이션 캐시 추가
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SonicSniperItem(Properties pProperties) {
        super(pProperties);
        // 멀티플레이 애니메이션 동기화 등록
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    // ---------------- [ GeckoLib 구현 필수 메서드 ] ----------------
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // 추후 발사/재장전/대기 3D 애니메이션을 제어할 때 여기에 컨트롤러를 등록합니다.
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private SonicSniperRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new SonicSniperRenderer();
                }
                return this.renderer;
            }
        });
    }

    // ---------------- [ 기존 총기 기능 로직 ] ----------------
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
            int chargeTicks = this.getUseDuration(stack) - count;

            float chargeSecondsRaw = chargeTicks / 20.0F;
            float chargeSeconds = Math.round(chargeSecondsRaw * 10.0F) / 10.0F;

            int maxBarLength = 10;
            int filledLength = Math.min(maxBarLength, (chargeTicks * maxBarLength) / 100);

            StringBuilder progressBar = new StringBuilder();
            for (int i = 0; i < maxBarLength; i++) {
                if (i < filledLength) {
                    progressBar.append("■");
                } else {
                    progressBar.append("□");
                }
            }

            String colorCode = "§c";
            if (chargeTicks >= 100) {
                colorCode = "§b[MAX] ";
            } else if (chargeTicks >= 20) {
                colorCode = "§e";
            }

            if (chargeSeconds > 5.0F) {
                chargeSeconds = 5.0F;
            }

            String formattedTime = String.format(java.util.Locale.US, "%.1f", chargeSeconds);

            player.displayClientMessage(
                    Component.literal("충전 중: " + colorCode + progressBar.toString() + " (" + formattedTime + "초)"),
                    true
            );
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            int chargeTicks = this.getUseDuration(stack) - timeLeft;

            if (chargeTicks < 20) {
                player.displayClientMessage(Component.literal("§c충전 시간 부족! (최소 1초 충전)"), true);
                return;
            }

            if (!level.isClientSide) {
                player.getCooldowns().addCooldown(this, 20);
                int currentFreq = getFrequency(stack);

                float chargeMultiplier = chargeTicks / 20.0F;

                if (chargeMultiplier > 5.0F) {
                    chargeMultiplier = 5.0F;
                }

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
        tooltip.add(Component.literal("주파수: §b" + getFrequency(stack) + " Hz"));
        tooltip.add(Component.literal("§e[Shift + 숫자 1~9] 키를 눌러 빠른 주파수 변경 가능"));
        tooltip.add(Component.literal("§e1초~5초 이상 충전 후 발사 (충전시간 비례 데미지 증가)"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}