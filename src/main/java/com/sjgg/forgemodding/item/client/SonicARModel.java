package com.sjgg.forgemodding.item.client;

import com.sjgg.forgemodding.item.custom.SonicARItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SonicARModel extends GeoModel<SonicARItem> {
    @Override
    public ResourceLocation getModelResource(SonicARItem animatable) {
        return ResourceLocation.tryParse("forgemodding:geo/sonic_ar.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SonicARItem animatable) {
        return ResourceLocation.tryParse("forgemodding:textures/item/sonic_ar.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SonicARItem animatable) {
        return null;
    }
}