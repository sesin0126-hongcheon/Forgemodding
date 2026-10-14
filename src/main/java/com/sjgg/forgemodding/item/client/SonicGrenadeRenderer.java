package com.sjgg.forgemodding.item.client;

import com.sjgg.forgemodding.item.custom.SonicGrenadeItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class SonicGrenadeRenderer extends GeoItemRenderer<SonicGrenadeItem> {
    public SonicGrenadeRenderer() {
        super(new SonicGrenadeModel());
    }
}