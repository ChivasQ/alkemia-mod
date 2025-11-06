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

        @Override
        public void encode(ByteBuf buffer, ChalkboardPixelsData data) {
            buffer.writeLong(data.pos.asLong());
            buffer.writeInt(data.pixels.size());
            for (Map.Entry<BlockPos, byte[]> entry : data.pixels.entrySet()) {
                buffer.writeLong(entry.getKey().asLong());

                buffer.writeBytes(entry.getValue());
            }
//            System.out.println("ENCODE INPUT MAP SIZE: " + data.pixels.size());
        }

        @Override
        public ChalkboardPixelsData decode(ByteBuf buffer) {
            BlockPos pos = BlockPos.of(buffer.readLong());
            int size = buffer.readInt();
//            System.out.println("DECODE INPUT BUF SIZE: " + size);
            Map<BlockPos, byte[]> outMap = new HashMap<>();
            for (int i = 0; i < size; i++) {
                BlockPos blockpos = BlockPos.of(buffer.readLong());
                byte[] bytes = new byte[256];
                buffer.readBytes(bytes);

                outMap.put(blockpos, bytes);

            }
            return new ChalkboardPixelsData(pos, outMap);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
