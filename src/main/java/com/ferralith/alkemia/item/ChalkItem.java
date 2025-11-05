package com.ferralith.alkemia.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.WoolCarpetBlock;

public class ChalkItem extends Item implements ItemColor {
    private final DyeColor color;

    public ChalkItem(DyeColor color, Properties properties) {
        super(properties);
        this.color = color;
    }


    @Override
    public int getColor(ItemStack itemStack, int i) {
        return 0;
    }
}
