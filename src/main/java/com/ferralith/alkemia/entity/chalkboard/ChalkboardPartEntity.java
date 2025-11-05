package com.ferralith.alkemia.entity.chalkboard;

import com.ferralith.alkemia.registries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ChalkboardPartEntity extends BlockEntity {
    private BlockPos masterPos = null;

    public void setMaster(BlockPos master) {
        this.masterPos = master;
        setChanged();
    }

    public MasterChalkboardEntity getMaster(Level level) {
        if (masterPos == null) return null;
        BlockEntity be = level.getBlockEntity(masterPos);
        if (be instanceof MasterChalkboardEntity master) {
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

}