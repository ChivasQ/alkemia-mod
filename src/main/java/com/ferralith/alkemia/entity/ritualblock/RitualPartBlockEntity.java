package com.ferralith.alkemia.entity.ritualblock;

import com.ferralith.alkemia.entity.chalkboard.MasterChalkboardEntity;
import com.ferralith.alkemia.registries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RitualPartBlockEntity extends BlockEntity {
    private BlockPos masterPos = null;

    public RitualPartBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MASTER_RITUAL_ENTITY.get(), pos, blockState);
    }

    public void setMaster(BlockPos master) {
        this.masterPos = master;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    public RitualMasterBlockEntity getMaster(Level level) {
        //System.out.println(masterPos);
        if (masterPos == null) return null;
        BlockEntity be = level.getBlockEntity(masterPos);
        if (be instanceof RitualMasterBlockEntity master) {
            //System.out.println("GET MASTER:" + masterPos);
            return master;
        }
        return null;
    }
}
