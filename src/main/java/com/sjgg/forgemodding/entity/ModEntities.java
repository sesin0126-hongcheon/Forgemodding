package com.sjgg.forgemodding.entity;

import com.sjgg.forgemodding.Forgemodding;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Forgemodding.MODID);

    // 소닉 탄환 엔티티 타입 등록
    public static final RegistryObject<EntityType<SonicBulletEntity>> SONIC_BULLET =
            ENTITY_TYPES.register("sonic_bullet",
                    () -> EntityType.Builder.<SonicBulletEntity>of(SonicBulletEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F) // 탄환 크기 설정 (가로, 세로)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("sonic_bullet"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}