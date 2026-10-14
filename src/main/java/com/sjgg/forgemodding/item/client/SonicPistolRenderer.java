package com.sjgg.forgemodding.item.client;

import com.sjgg.forgemodding.item.custom.SonicPistolItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class SonicPistolRenderer extends GeoItemRenderer<SonicPistolItem> {
    public SonicPistolRenderer() {
        super(new SonicPistolModel());
    }
}