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

        // 현재 날아간 거리
        double distance = this.position().distanceTo(spawnPos);

        // NBT 데이터로부터 이 탄환이 어디에서 발사되었는지 판단
        boolean isPistol = this.getPersistentData().getBoolean("IsPistol");
        boolean isAR = this.getPersistentData().getBoolean("IsAR");

        if (isPistol && distance > 20.0D) {
            this.discard();
            return;
        }
        if (isAR && distance > 25.0D) {
            this.discard();
            return;
        }

        // 20틱(1초)이 지나면 스나이퍼 탄환 등 소멸
        if (this.tickCount > 20) {
            this.discard();
            return;
        }

        // [클라이언트 사이드 이펙트 제어]
        if (this.level().isClientSide) {
            if (isPistol || isAR) {
                double distanceFromPlayer = this.position().distanceTo(this.spawnPos);

                // 2.5블록 이내 파티클 생성을 억제하여 시야 차단
                if (distanceFromPlayer >= 2.5D) {
                    this.level().addParticle(ParticleTypes.SONIC_BOOM, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
                }
            } else {
                this.level().addParticle(ParticleTypes.SONIC_BOOM, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            }
        }

        Vec3 movement = this.getDeltaMovement();

        // 1. 블록 충돌 감지
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitresult.getType() != HitResult.Type.MISS) {
            this.onHit(hitresult);
        }

        // 2. 엔티티 감지 및 대미지 처리
        AABB searchBox = this.getBoundingBox().expandTowards(movement).inflate(1.0D);
        List<Entity> targets = this.level().getEntities(this, searchBox, this::canHitEntity);

        for (Entity target : targets) {
            if (!hitEntityIds.contains(target.getId()) && target instanceof LivingEntity livingTarget) {
                if (isTargetFrequencyMatched(livingTarget)) {

                    if (isAR) {
                        int currentHeat = this.getPersistentData().getInt("AR_Heat");
                        float bonusDamage = (currentHeat / 100.0F) * 5.0F;
                        float finalDamage = 2.0F + bonusDamage;

                        livingTarget.hurt(this.damageSources().magic(), finalDamage);

                        // 맞았을 때 갑옷 내구도 감소 3
                        for (EquipmentSlot slot : EquipmentSlot.values()) {
                            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                                ItemStack armor = livingTarget.getItemBySlot(slot);
                                if (!armor.isEmpty()) {
                                    armor.hurtAndBreak(3, livingTarget, (e) -> e.broadcastBreakEvent(slot));
                                }
                            }
                        }

                        this.discard();
                        return;

                    } else if (isPistol) {
                        float distanceBonus = (float) ((20.0D - distance + 1.0D));
                        if (distanceBonus < 0.0F) distanceBonus = 0.0F;

                        float finalDamage = distanceBonus;
                        livingTarget.hurt(this.damageSources().magic(), finalDamage);

                        for (EquipmentSlot slot : EquipmentSlot.values()) {
                            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                                ItemStack armor = livingTarget.getItemBySlot(slot);
                                if (!armor.isEmpty()) {
                                    armor.hurtAndBreak(3, livingTarget, (e) -> e.broadcastBreakEvent(slot));
                                }
                            }
                        }

                        this.discard();
                        return;

                    } else {
                        // ---------------- [ Sonic Sniper 스나이퍼 로직 ] ----------------
                        float baseDamage = 5.0F + (float) ( distance * 5.0D);
                        float finalDamage = baseDamage * this.damageMultiplier;

                        livingTarget.hurt(this.damageSources().magic(), finalDamage);

                        // 맞았을 때 갑옷 내구도 감소 50
                        for (EquipmentSlot slot : EquipmentSlot.values()) {
                            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                                ItemStack armor = livingTarget.getItemBySlot(slot);
                                if (!armor.isEmpty()) {
                                    armor.hurtAndBreak(50, livingTarget, (e) -> e.broadcastBreakEvent(slot));
                                }
                            }
                        }
                    }

                    hitEntityIds.add(livingTarget.getId());
                }
            }
        }

        this.setPos(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {}

    // 3. [블록 충돌 시 소멸 로직] 벽을 만나면 뚫지 못하고 사라짐
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