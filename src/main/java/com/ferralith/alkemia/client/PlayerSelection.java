package com.ferralith.alkemia.client;

import net.minecraft.core.BlockPos;
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