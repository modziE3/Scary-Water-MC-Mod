package net.modzyyy.scarywater;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PlayerInputPayload(
        boolean moving,
        boolean jumping
) implements CustomPacketPayload {

    public static final Type<PlayerInputPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    ScaryWater.MODID,
                    "player_input"
            ));

    public static final StreamCodec<ByteBuf, PlayerInputPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    PlayerInputPayload::moving,

                    ByteBufCodecs.BOOL,
                    PlayerInputPayload::jumping,

                    PlayerInputPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}