package com.ferralith.alkemia.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class DebugWand extends Item {
    public DebugWand(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();

        if (level.isClientSide) {
            System.out.println("clientside");
            System.out.println(level.getBlockEntity(pos));
        } else {
            System.out.println("serverside");
            System.out.println(level.getBlockEntity(pos));
        }
        return InteractionResult.CONSUME;
    }

}
