package com.sjgg.forgemodding.networks;

import com.sjgg.forgemodding.item.custom.SonicARItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketARShoot {

    public PacketARShoot() {}

    public PacketARShoot(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof SonicARItem arItem) {
                    // [수정] 1틱 연사를 위해 서버 측 쿨타임 제한을 완전히 해제합니다.
                    arItem.shoot(player.level(), player, stack);
                }
            }
        });
        return true;
    }
}