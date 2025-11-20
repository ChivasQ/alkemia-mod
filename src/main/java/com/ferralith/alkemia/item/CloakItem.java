package com.ferralith.alkemia.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class CloakItem extends Item implements ICurioItem {
    public CloakItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // ticking logic here
    }
}
