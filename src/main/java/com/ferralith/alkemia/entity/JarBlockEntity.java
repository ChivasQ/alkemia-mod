package com.ferralith.alkemia.entity;

import com.ferralith.alkemia.block.JarBlock;
import com.ferralith.alkemia.registries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;

public class JarBlockEntity extends BlockEntity {
    private final FluidTank fluidTank;

    public JarBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.JAR_BLOCK_ENTITY.get(), pos, blockState);
        this.fluidTank = new FluidTank(10000) {
            @Override
            protected void onContentsChanged() {
                if (level != null && !level.isClientSide()) {

                    setChanged();
                    int currentLightLevel = fluidTank.getFluid().getFluidType().getLightLevel();

                    int stateLightLevel = getBlockState().getValue(JarBlock.LIGHT_LEVEL);

                    if (currentLightLevel != stateLightLevel) {
                        level.setBlock(worldPosition,
                                getBlockState().setValue(JarBlock.LIGHT_LEVEL, currentLightLevel),
                                3);
                    } else {
                        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                    }
                }
            }
        };
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        nbt.put("fluidTank", fluidTank.writeToNBT(registries, new CompoundTag()));
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        if (nbt.contains("fluidTank")) {
            fluidTank.readFromNBT(registries, nbt.getCompound("fluidTank"));
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

}
