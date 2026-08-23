package com.sjgg.forgemodding.item.custom;

import com.sjgg.forgemodding.entity.SonicBulletEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SonicPistolItem extends Item {
    public SonicPistolItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            int currentFreq = SonicSniperItem.getFrequency(stack);

            Vec3 lookVec = player.getLookAngle();
            double spawnX = player.getX() + lookVec.x * 1.5;
            double spawnY = player.getEyeY() - 0.2 + lookVec.y * 1.5;
            double spawnZ = player.getZ() + lookVec.z * 1.5;

            SonicBulletEntity bullet = new SonicBulletEntity(level, player, currentFreq, 1.0F);
            bullet.setPos(spawnX, spawnY, spawnZ);

            bullet.getPersistentData().putBoolean("IsPistol", true);
            bullet.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 5.0F, 0.0F);
            level.addFreshEntity(bullet);
        }

        player.getCooldowns().addCooldown(this, 5); // 권총 쿨타임
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}