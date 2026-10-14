package com.sjgg.forgemodding.item.custom;

import com.sjgg.forgemodding.entity.SonicBulletEntity;
import com.sjgg.forgemodding.item.client.SonicPistolRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class SonicPistolItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SonicPistolItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private SonicPistolRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new SonicPistolRenderer();
                }
                return this.renderer;
            }
        });
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            int currentFreq = SonicSniperItem.getFrequency(stack);
            Vec3 lookVec = player.getLookAngle();

            // 수식 깨짐 수정 완료
            double spawnX = player.getX() + lookVec.x * 1.5;
            double spawnY = player.getEyeY() - 0.2 + lookVec.y * 1.5;
            double spawnZ = player.getZ() + lookVec.z * 1.5;

            SonicBulletEntity bullet = new SonicBulletEntity(level, player, currentFreq, 1.0F);
            bullet.setPos(spawnX, spawnY, spawnZ);

            bullet.getPersistentData().putBoolean("IsPistol", true);
            bullet.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 5.0F, 0.0F);
            level.addFreshEntity(bullet);
        }

        player.getCooldowns().addCooldown(this, 5);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // 애니메이션 컨트롤러 등록 공간
    }
}