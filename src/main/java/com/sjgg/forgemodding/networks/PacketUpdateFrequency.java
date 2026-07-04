package com.sjgg.forgemodding.networks;

import com.sjgg.forgemodding.item.custom.SonicSniperItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import com.sjgg.forgemodding.item.custom.SonicPistolItem;

import java.util.function.Supplier;

public class PacketUpdateFrequency {
    private final int targetFrequency;

    // 특정 주파수(100, 200 등)로 바로 세팅할 때 사용하는 생성자
    public PacketUpdateFrequency(int targetFrequency) {
        this.targetFrequency = targetFrequency;
    }

    public PacketUpdateFrequency(FriendlyByteBuf buf) {
        this.targetFrequency = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(targetFrequency);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();

                // 스나이퍼나 권총을 들고 있다면 해당 주파수로 즉시 교체
                if (stack.getItem() instanceof SonicSniperItem || stack.getItem() instanceof SonicPistolItem) {
                    stack.getOrCreateTag().putInt("Frequency", targetFrequency);

                    player.displayClientMessage(
                            Component.literal("주파수 변경: §b" + targetFrequency + " Hz"), true
                    );
                }
            }
        });
        return true;
    }
}