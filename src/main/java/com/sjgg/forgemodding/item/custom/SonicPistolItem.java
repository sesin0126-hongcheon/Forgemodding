package com.sjgg.forgemodding.item.custom;

import com.sjgg.forgemodding.entity.SonicBulletEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SonicPistolItem extends Item {
    public SonicPistolItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!player.getCooldowns().isOnCooldown(this)) {
            if (!level.isClientSide) {
                player.getCooldowns().addCooldown(this, 2); // 0.1초 쿨타임

                // [주파수 기능 연동] 스나이퍼의 주파수 가져오기 로직과 동일하게 공유
                int currentFreq = SonicSniperItem.getFrequency(stack);
                float pistolMultiplier = 1.0F;

                SonicBulletEntity bullet = new SonicBulletEntity(level, player, currentFreq, pistolMultiplier);
                bullet.getPersistentData().putBoolean("IsPistol", true);

                bullet.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 5.0F, 0.0F);
                level.addFreshEntity(bullet);

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.5F, 1.5F);
            }
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        // [주파수 툴팁 추가] 스나이퍼와 완벽 동기화됨
        tooltip.add(Component.literal("현재 주파수: §b" + SonicSniperItem.getFrequency(stack) + " Hz"));
        tooltip.add(Component.literal("§7[속사형 권총] 우클릭 시 즉시 소닉 붐을 발사합니다."));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}