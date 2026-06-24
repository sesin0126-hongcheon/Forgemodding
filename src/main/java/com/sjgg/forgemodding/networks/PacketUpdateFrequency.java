package com.sjgg.forgemodding.networks;

import com.sjgg.forgemodding.item.custom.SonicSniperItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateFrequency {
    private final boolean increase;

    public PacketUpdateFrequency(boolean increase) {
        this.increase = increase;
    }

    public PacketUpdateFrequency(FriendlyByteBuf buf) {
        this.increase = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(increase);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof SonicSniperItem) {
                    SonicSniperItem.cycleFrequency(stack, increase);
                    player.displayClientMessage(
                            Component.literal("주파수 변경: §b" + SonicSniperItem.getFrequency(stack) + " Hz"), true
                    );
                }
            }
        });
        return true;
    }
}