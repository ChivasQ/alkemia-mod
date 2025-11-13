package com.ferralith.alkemia.entity.ritualblock;

import com.ferralith.alkemia.block.ChalkboardPartBlock;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.capabilities.EntityCapability;
import org.jetbrains.annotations.Nullable;

public class PlayerSelection {
    private BlockPos activeRitualBlockPos = null;
    private Integer firstSelectedNodeIndex = null;

    public void setActiveRitualBlockPos(@Nullable BlockPos pos) {
        this.activeRitualBlockPos = pos;
    }

    @Nullable
    public BlockPos getActiveRitualBlockPos() {
        return this.activeRitualBlockPos;
    }

    public void setFirstSelectedNodeIndex(@Nullable Integer index) {
        this.firstSelectedNodeIndex = index;
    }

    @Nullable
    public Integer getFirstSelectedNodeIndex() {
        return this.firstSelectedNodeIndex;
    }

}