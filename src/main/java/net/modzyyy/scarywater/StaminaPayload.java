package net.modzyyy.scarywater;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record StaminaPayload(double stamina) implements CustomPacketPayload {

    public static final Type<StaminaPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    ScaryWater.MODID,
                    "stamina"
            ));

    public static final StreamCodec<ByteBuf, StaminaPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.DOUBLE,
                    StaminaPayload::stamina,
                    StaminaPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}