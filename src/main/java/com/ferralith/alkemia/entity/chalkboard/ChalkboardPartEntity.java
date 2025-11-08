package com.ferralith.alkemia.entity.chalkboard;

import com.ferralith.alkemia.block.util.TickableBlockEntity;
import com.ferralith.alkemia.registries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class ChalkboardPartEntity extends BlockEntity {
    private BlockPos masterPos = null;

    public void setMaster(BlockPos master) {
        this.masterPos = master;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    public MasterChalkboardEntity getMaster(Level level) {
        //System.out.println(masterPos);
        if (masterPos == null) return null;
        BlockEntity be = level.getBlockEntity(masterPos);
        if (be instanceof MasterChalkboardEntity master) {
            //System.out.println("GET MASTER:" + masterPos);
            return master;
        }
        return null;
    }

    public ChalkboardPartEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CHALKBOARD_PART_ENTITY.get(), pPos, pBlockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (masterPos != null) {
            tag.putLong("masterPos", masterPos.asLong());
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.hasUUID("masterPos") || tag.contains("masterPos")) {
            this.masterPos = BlockPos.of(tag.getLong("masterPos"));
        }
    }


    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }



    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(
                this
        );
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        handleUpdateTag(pkt.getTag(), registries);
    }

}