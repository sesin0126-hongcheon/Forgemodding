package com.sjgg.forgemodding.init;

import com.sjgg.forgemodding.Forgemodding;
import com.sjgg.forgemodding.entity.SonicGrenadeEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Forgemodding.MODID);

    // 수류탄 엔티티 등록 (크기: 0.25블록 x 0.25블록)
    public static final RegistryObject<EntityType<SonicGrenadeEntity>> SONIC_GRENADE =
            ENTITY_TYPES.register("sonic_grenade", () -> EntityType.Builder.<SonicGrenadeEntity>of(SonicGrenadeEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build("sonic_grenade"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}