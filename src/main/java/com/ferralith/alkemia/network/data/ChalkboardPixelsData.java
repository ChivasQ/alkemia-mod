package com.ferralith.alkemia.network.data;

import com.ferralith.alkemia.Alkemia;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record ChalkboardPixelsData(BlockPos pos, Map<BlockPos, byte[]> pixels) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChalkboardPixelsData> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Alkemia.MODID, "chalkboard_pixels_data"));

    public static final StreamCodec<ByteBuf, ChalkboardPixelsData> STREAM_CODEC = new StreamCodec<>() {

        private static final StreamCodec<ByteBuf, byte[]> PIXEL_ARRAY_CODEC = ByteBufCodecs.byteArray(256);

        private static final StreamCodec<ByteBuf, Map<BlockPos, byte[]>> MAP_CODEC = ByteBufCodecs.map(
                HashMap::new,
                BlockPos.STREAM_CODEC,
                PIXEL_ARRAY_CODEC
        );

        @Override
        public void encode(ByteBuf buffer, ChalkboardPixelsData data) {
            BlockPos.STREAM_CODEC.encode(buffer, data.pos());
            MAP_CODEC.encode(buffer, data.pixels());
        }

        @Override
        public ChalkboardPixelsData decode(ByteBuf buffer) {
            BlockPos masterPos = BlockPos.STREAM_CODEC.decode(buffer);

            Map<BlockPos, byte[]> pixelMap = MAP_CODEC.decode(buffer);

            return new ChalkboardPixelsData(masterPos, pixelMap);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
