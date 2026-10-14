package com.sjgg.forgemodding.item.custom;

import com.sjgg.forgemodding.entity.SonicBulletEntity;
import com.sjgg.forgemodding.item.client.SonicARRenderer;
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
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class SonicARItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SonicARItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private SonicARRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new SonicARRenderer();
                }
                return this.renderer;
            }
        });
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
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
        if (!level.isClientSide) {
            int duration = this.getUseDuration(stack) - count;

            if (duration % 2 == 0) {
                int currentHeat = getHeat(stack);
                if (currentHeat < 100) {
                    int nextHeat = currentHeat + 1;
                    stack.getOrCreateTag().putInt("Heat", nextHeat);

                    if (entity instanceof Player player) {
                        if (duration % 20 == 0 || nextHeat == 100) {
                            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                    SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.4F, 1.3F);
                        }
                        player.displayClientMessage(Component.literal("§e[열 충전 중] §c" + nextHeat + " / 100"), true);
                    }
                } else {
                    if (entity instanceof Player player && duration % 20 == 0) {
                        player.displayClientMessage(Component.literal("§c[경고] 열(Heat)이 이미 최대치(100%)입니다!"), true);
                    }
                }
            }
        }
    }

    public void shoot(Level level, Player player, ItemStack stack) {
        int currentHeat = getHeat(stack);

        if (currentHeat <= 0) {
            return;
        }

        int currentFreq = SonicSniperItem.getFrequency(stack);
        net.minecraft.world.phys.Vec3 lookVec = player.getLookAngle();

        // 수식 깨짐 수정 완료
        double spawnX = player.getX() + lookVec.x * 1.5;
        double spawnY = player.getEyeY() - 0.2 + lookVec.y * 1.5;
        double spawnZ = player.getZ() + lookVec.z * 1.5;

        SonicBulletEntity bullet = new SonicBulletEntity(level, player, currentFreq, 1.0F);
        bullet.setPos(spawnX, spawnY, spawnZ);

        bullet.getPersistentData().putBoolean("IsAR", true);
        bullet.getPersistentData().putInt("AR_Heat", currentHeat);

        bullet.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 5.0F, 0.0F);
        level.addFreshEntity(bullet);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 0.35F, 2.0F);

        // 수식 깨짐 수정 완료
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // 애니메이션 컨트롤러 등록 공간
    }
}