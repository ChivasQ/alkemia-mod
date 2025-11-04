package com.ferralith.alkemia.entity.chalkboard;

import com.ferralith.alkemia.network.data.ChalkboardPixelsData;
import com.ferralith.alkemia.network.handler.ChalkboardPixelsDataServerPayloadHandler;
import com.ferralith.alkemia.registries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector2i;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MasterChalkboardEntity extends BlockEntity {
    private static final int BLOCK_PIXELS = 16;
    private final Map<BlockPos, byte[][]> pixels = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    private final Map<BlockPos, byte[]> dirtyBlocksBuffer = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    private long lastBufferSendTime = 0;

    public MasterChalkboardEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MASTER_CHALKBOARD_ENTITY.get(), pos, blockState);
    }

    public void setPixel(BlockPos blockPos, int localX, int localY, byte color) {
        byte[][] blockPixels = pixels.computeIfAbsent(
                blockPos,
                k -> new byte[BLOCK_PIXELS][BLOCK_PIXELS]
        );

        blockPixels[localY][localX] = color;

        byte[] dirtyBlockPixels = this.dirtyBlocksBuffer.computeIfAbsent(
                blockPos,
                k -> new byte[BLOCK_PIXELS*BLOCK_PIXELS]
        );
        dirtyBlockPixels[localY * 16 + localX] = color;

    }

    public Map<BlockPos, byte[][]> getPixels() {
        return pixels;
    }

    public void markDirtyAndSync() {
        if (level != null && !level.isClientSide) {
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void setPixelClient(BlockPos blockPos, int localX, int localY, byte value, int radius) {
        setPixel(blockPos, localX, localY, value);


//        for (int i = -1; i < 2; i++) {
//            for (int j = -1; j < 2; j++) {
//                int finalX = localX + i;
//                int finalY = localY + j;
//                this.pixelBuffer.computeIfAbsent()
//            }
//        }
    }

    @OnlyIn(Dist.CLIENT)
    public void clearClient() {
        dirtyBlocksBuffer.clear();
    }

    @OnlyIn(Dist.CLIENT)
    public void tickClient() {
        long now = System.currentTimeMillis();
        if (!this.dirtyBlocksBuffer.isEmpty() && (now - this.lastBufferSendTime > 250)) {

            PacketDistributor.sendToServer(new ChalkboardPixelsData(this.worldPosition, dirtyBlocksBuffer));

            clearClient();
            this.lastBufferSendTime = now;
        }
    }

    public byte[][] getBlockPixels(BlockPos blockPos) {
        return pixels.getOrDefault(blockPos, new byte[BLOCK_PIXELS][BLOCK_PIXELS]);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        for (Map.Entry<BlockPos, byte[][]> entry : pixels.entrySet()) {
            BlockPos pos = entry.getKey();
            byte[][] colorArr = entry.getValue();

            byte[] pixelData = new byte[256];
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    pixelData[y * 16 + x] = colorArr[x][y];
                }
            }
            tag.putByteArray("data_" + pos.asLong(), pixelData);
        }
        System.out.println("ho");
        System.out.println(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        System.out.println("hi");
        System.out.println(tag);
        pixels.clear();

        for (String key : tag.getAllKeys()) {
            System.out.println("tag " + key);
            if (key.startsWith("data_")) {
                long posLong = Long.parseLong(key.substring(5));
                BlockPos pos = BlockPos.of(posLong);
                System.out.println(pos);
                byte[] pixelData = tag.getByteArray(key);

                byte[][] colorArr = new byte[16][16];
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        colorArr[x][y] = pixelData[y * 16 + x];
                    }
                }

                pixels.put(pos, colorArr);
            }
        }
    }
    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        saveAdditional(tag, registries);
    }
}
