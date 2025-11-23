package com.ferralith.alkemia.entity.chalkboard;

import com.ferralith.alkemia.network.data.ChalkboardPixelsData;
import com.ferralith.alkemia.registries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

public class MasterChalkboardEntity extends BlockEntity {
    private static final int BLOCK_PIXELS = 16;
    private Map<BlockPos, byte[][]> pixels = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    private final Map<BlockPos, byte[]> dirtyBlocksBuffer = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    private long lastBufferSendTime = 0;

    public MasterChalkboardEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MASTER_CHALKBOARD_ENTITY.get(), pos, blockState);
    }

    public void getEntries() {
        //System.out.println(pixels.entrySet());
    }

    public void setPixelServer(BlockPos blockPos, int localX, int localY, byte color) {
        if (pixels == null) return;

        byte[][] blockPixels = pixels.computeIfAbsent(
                blockPos,
                k -> new byte[BLOCK_PIXELS][BLOCK_PIXELS]
        );

        if (blockPixels[localY][localX] != color) {
            blockPixels[localY][localX] = color;
            setChanged();
        }
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
        if (pixels == null) return;

        // TODO: radius logic

        byte[][] blockPixels = pixels.computeIfAbsent(
                blockPos,
                k -> new byte[BLOCK_PIXELS][BLOCK_PIXELS]
        );

        if (blockPixels[localY][localX] == value) {
            return;
        }

        blockPixels[localY][localX] = value;
    }

    @OnlyIn(Dist.CLIENT)
    public void setGlobalPixelClient(int globalX, int globalZ, byte value, int radius) {
        if (pixels == null) return;

        for (int dx = -radius/2; dx <= radius/2; dx++) {
            for (int dz = -radius/2; dz <= radius/2; dz++) {

                int currentGlobalX = globalX + dx;
                int currentGlobalZ = globalZ + dz;

                int blockX = Math.floorDiv(currentGlobalX, 16);
                int blockZ = Math.floorDiv(currentGlobalZ, 16);
                int localX = Math.floorMod(currentGlobalX, 16);
                int localZ = Math.floorMod(currentGlobalZ, 16);

                BlockPos blockPos = new BlockPos(blockX, this.worldPosition.getY(), blockZ);

                byte[][] blockPixels = pixels.computeIfAbsent(
                        blockPos,
                        k -> new byte[BLOCK_PIXELS][BLOCK_PIXELS]
                );



                if (blockPixels[localX][localZ] == value) {
                    continue;
                }

                blockPixels[localX][localZ] = value;

                byte[] dirtyBlockPixels = this.dirtyBlocksBuffer.computeIfAbsent(
                        blockPos,
                        k -> new byte[BLOCK_PIXELS * BLOCK_PIXELS]
                );

                for (int y = 0; y < 16; y++) {
                    System.arraycopy(blockPixels[y], 0, dirtyBlockPixels, y * 16 + 0, 16);

                }
            }
        }
    }
    @OnlyIn(Dist.CLIENT)
    public void clearClient() {
        dirtyBlocksBuffer.clear();
    }

    @OnlyIn(Dist.CLIENT)
    public void tickClient() {
        if (pixels == null) return;

        long now = System.currentTimeMillis();
        if (!this.dirtyBlocksBuffer.isEmpty() && (now - this.lastBufferSendTime > 250)) {
            //System.out.println("SIZE OF MAP BEFORE SENDING TO SERVER: " + this.dirtyBlocksBuffer.size());
            Map<BlockPos, byte[]> tmpCopy = new HashMap<>(this.dirtyBlocksBuffer);
            PacketDistributor.sendToServer(new ChalkboardPixelsData(this.worldPosition, tmpCopy));

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
        if (pixels == null) return;
        for (Map.Entry<BlockPos, byte[][]> entry : pixels.entrySet()) {
            BlockPos pos = entry.getKey();
            byte[][] colorArr = entry.getValue();

            byte[] pixelData = new byte[256];
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    pixelData[y * 16 + x] = colorArr[y][x];
                }
            }
            tag.putByteArray("data_" + pos.asLong(), pixelData);
        }
//        System.out.println("ho");
//        System.out.println(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
//        System.out.println("hi");
//        System.out.println(tag);
        pixels.clear();

        for (String key : tag.getAllKeys()) {
            if (key.startsWith("data_")) {
                long posLong = Long.parseLong(key.substring(5));
                BlockPos pos = BlockPos.of(posLong);
                byte[] pixelData = tag.getByteArray(key);

                byte[][] colorArr = new byte[16][16];
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        colorArr[y][x] = pixelData[y * 16 + x];
                    }
                }

                pixels.put(pos, colorArr);
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        //System.out.println("Sending update tag with " + pixels.size() + " blocks");
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        loadAdditional(tag, registries);

        if (level != null && level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
        }
    }

    public void removeBlock(BlockPos pos) {
        pixels.remove(pos);
    }

    public void getRidOfMap() {
        pixels = null;
    }

}
