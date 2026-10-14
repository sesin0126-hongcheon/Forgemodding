package com.sjgg.forgemodding.item.client;

import com.sjgg.forgemodding.item.custom.SonicGrenadeItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SonicGrenadeModel extends GeoModel<SonicGrenadeItem> {
    @Override
    public ResourceLocation getModelResource(SonicGrenadeItem animatable) {
        return ResourceLocation.tryParse("forgemodding:geo/sonic_grenade.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SonicGrenadeItem animatable) {
        return ResourceLocation.tryParse("forgemodding:textures/item/sonic_grenade.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SonicGrenadeItem animatable) {
        return null;
    }
}