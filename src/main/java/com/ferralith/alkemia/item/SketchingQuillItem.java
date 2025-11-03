package com.ferralith.alkemia.item;

import com.ferralith.alkemia.test.ClientSelectionData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class SketchingQuillItem extends Item {
    public SketchingQuillItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide()){
            if (ClientSelectionData.pos1 == null) {
                ClientSelectionData.pos1 = context.getClickedPos().above();
            } else {
                context.getPlayer().sendSystemMessage(Component.literal(ClientSelectionData.pos1 + " " + context.getClickedPos().above()));
                ClientSelectionData.pos1 = null;
            }
        }

        return super.useOn(context);
    }


}
