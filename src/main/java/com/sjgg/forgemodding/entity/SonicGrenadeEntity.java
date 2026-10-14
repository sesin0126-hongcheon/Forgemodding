package com.sjgg.forgemodding.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

public class SonicGrenadeEntity extends ThrowableItemProjectile {
    private int fuse = 40; // 최대 2초 (장애물이 없을 때 터지는 안전 타이머)

    public SonicGrenadeEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public SonicGrenadeEntity(Level level, LivingEntity shooter) {
        super(com.sjgg.forgemodding.init.ModEntityTypes.SONIC_GRENADE.get(), shooter, level);
    }

    public SonicGrenadeEntity(Level level, double x, double y, double z) {
        super(com.sjgg.forgemodding.init.ModEntityTypes.SONIC_GRENADE.get(), x, y, z, level);
    }

    @Override
    protected Item getDefaultItem() {
        return com.sjgg.forgemodding.item.SonicItems.SONIC_GRENADE.get();
    }

    // [엔티티 제외] 플레이어나 몹 같은 엔티티와는 충돌하지 않고 통과합니다.
    @Override
    protected boolean canHitEntity(Entity entity) {
        return false;
    }

    // [블록 충돌 감지] 벽이나 바닥에 부딪히는 순간 즉시 실행됩니다.
    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide()) {
            explode();
            this.discard(); // 폭발 후 수류탄 엔티티 제거
        }
    }

    @Override
    public void tick() {
        super.tick();

        // 장애물에 부딪히지 않고 공중을 날더라도 2초가 지나면 강제 폭발
        if (!this.level().isClientSide()) {
            this.fuse--;
            if (this.fuse <= 0) {
                explode();
                this.discard();
            }
        }
    }

    private void explode() {
        Level level = this.level();
        if (level.isClientSide()) return;

        // 1. 폭발 효과 (지형은 파괴하지 않음)
        level.explode(this, this.getX(), this.getY(), this.getZ(), 3.0F, false, Level.ExplosionInteraction.NONE);

        // 2. 폭발 반경 5블록 이내의 엔티티에게 10의 피해 적용
        double radius = 5.0D;
        AABB area = this.getBoundingBox().inflate(radius);

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area)) {
            if (this.distanceToSqr(entity) <= radius * radius) {
                entity.hurt(level.damageSources().explosion(this, this.getOwner() instanceof LivingEntity living ? living : null), 10.0F);
            }
        }
    }
}