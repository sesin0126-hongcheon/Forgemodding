package com.sjgg.forgemodding.item.client;

import com.sjgg.forgemodding.item.custom.SonicPistolItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SonicPistolModel extends GeoModel<SonicPistolItem> {
    @Override
    public ResourceLocation getModelResource(SonicPistolItem animatable) {
        return ResourceLocation.tryParse("forgemodding:geo/sonic_pistol.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SonicPistolItem animatable) {
        return ResourceLocation.tryParse("forgemodding:textures/item/sonic_pistol.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SonicPistolItem animatable) {
        return null;
    }
}