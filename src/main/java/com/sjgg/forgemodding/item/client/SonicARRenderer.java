package com.sjgg.forgemodding.item.client;

import com.sjgg.forgemodding.item.custom.SonicARItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class SonicARRenderer extends GeoItemRenderer<SonicARItem> {
    public SonicARRenderer() {
        super(new SonicARModel());
    }
}
