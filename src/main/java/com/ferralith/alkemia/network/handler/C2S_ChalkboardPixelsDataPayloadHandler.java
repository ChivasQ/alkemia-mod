package com.ferralith.alkemia.network.handler;

import com.ferralith.alkemia.entity.chalkboard.MasterChalkboardEntity;
import com.ferralith.alkemia.network.data.ChalkboardPixelsData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;

public class C2S_ChalkboardPixelsDataPayloadHandler {
    public static void handleDataOnNetwork(final ChalkboardPixelsData data, final IPayloadContext context) {

        context.enqueueWork(() -> {
            Player player = context.player();
            if (player instanceof ServerPlayer serverPlayer) {
                Level level = serverPlayer.level();
                BlockPos masterPos = data.pos();
                PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunkAt(masterPos).getPos(), data);
                if (level.isLoaded(masterPos)) {
                    BlockEntity be = level.getBlockEntity(masterPos);

                    if (be instanceof MasterChalkboardEntity masterChalkboard) {
                        Map<BlockPos, byte[]> map = data.pixels();

                        for (Map.Entry<BlockPos, byte[]> entry : map.entrySet()) {
                            BlockPos pos = entry.getKey();
                            byte[] color_arr = entry.getValue();

                            for (int x = 0; x < 16; x++) {
                                for (int y = 0; y < 16; y++) {
                                    byte c = color_arr[y * 16 + x];
                                    masterChalkboard.setPixelServer(pos, x, y, c);
                                }
                            }
                        }
                        masterChalkboard.markDirtyAndSync();
                    }
                }
            }
        });
    }
}
