package com.ferralith.alkemia.block.util;

import com.ferralith.alkemia.entity.chalkboard.ChalkboardPartEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

public interface TickableBlockEntity {
    void tick();

}
