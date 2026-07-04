package com.sjgg.forgemodding.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class SonicBulletEntity extends Projectile {
    private int bulletFrequency = 100;
    private Vec3 spawnPos;
    private float damageMultiplier = 1.0F;
    private final List<Integer> hitEntityIds = new ArrayList<>();

    public SonicBulletEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    public SonicBulletEntity(Level level, LivingEntity shooter, int frequency, float multiplier) {
        super(ModEntities.SONIC_BULLET.get(), level);
        this.setOwner(shooter);
        this.setPos(shooter.getX(), shooter.getEyeY() - 0.1D, shooter.getZ());
        this.spawnPos = this.position();
        this.bulletFrequency = frequency;
        this.damageMultiplier = multiplier;
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void tick() {
        super.tick();
        if (this.spawnPos == null) this.spawnPos = this.position();

        // 20틱(1초)이 지나면 100블록 지남 -> 소멸
        if (this.tickCount > 20) {
            this.discard();
            return;
        }

        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.SONIC_BOOM, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }

        Vec3 movement = this.getDeltaMovement();

        // 1. [블록 충돌 감지 활성화]
        // 탄환이 이동하는 궤적 내에 블록(벽)이 있는지 검사합니다.
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitresult.getType() != HitResult.Type.MISS) {
            // 무언가(블록 혹은 엔티티)에 부딪혔다면 onHit 호출 -> 여기서 블록이면 탄환이 소멸합니다.
            this.onHit(hitresult);
        }

        // 2. 엔티티 감지 및 관통 대미지 처리 (엔티티끼리는 여전히 일직선상에서 관통됩니다)
        AABB searchBox = this.getBoundingBox().expandTowards(movement).inflate(1.0D);
        List<Entity> targets = this.level().getEntities(this, searchBox, this::canHitEntity);

        for (Entity target : targets) {
            if (!hitEntityIds.contains(target.getId()) && target instanceof LivingEntity livingTarget) {
                if (isTargetFrequencyMatched(livingTarget)) {
                    double distance = this.position().distanceTo(spawnPos);

                    float baseDamage = 20.0F + (float) ((distance / 2.0D) * 2.0D);
                    float finalDamage = baseDamage * this.damageMultiplier;

                    livingTarget.hurt(this.damageSources().magic(), finalDamage);

                    // 갑옷 내구도 감소
                    for (EquipmentSlot slot : EquipmentSlot.values()) {
                        if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                            ItemStack armor = livingTarget.getItemBySlot(slot);
                            if (!armor.isEmpty()) {
                                armor.hurtAndBreak(50, livingTarget, (e) -> e.broadcastBreakEvent(slot));
                            }
                        }
                    }
                    hitEntityIds.add(livingTarget.getId());
                }
            }
        }

        this.setPos(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);
    }

    // 엔티티 충돌 시 처리 (이미 위에서 전반적인 관통 데미지를 계산하므로 여기서는 비워둡니다)
    @Override
    protected void onHitEntity(EntityHitResult result) {}

    // 3. [블록 충돌 시 소멸 로직 추가]
    // 탄환이 날아가다가 벽이나 바닥(블록)에 부딪히면 실행
    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    private boolean isTargetFrequencyMatched(LivingEntity target) {
        return true;
    }
}