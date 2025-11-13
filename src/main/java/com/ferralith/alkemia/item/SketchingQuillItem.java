package com.ferralith.alkemia.item;

import com.ferralith.alkemia.client.ClientSelectionData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

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

                int width = Math.abs(ClientSelectionData.pos1.getX() -  context.getClickedPos().getX());
                int height = Math.abs(ClientSelectionData.pos1.getZ() -  context.getClickedPos().getZ());
                int minX = Math.min(ClientSelectionData.pos1.getX(), context.getClickedPos().getX());
                int minY = Math.min(ClientSelectionData.pos1.getZ(), context.getClickedPos().getZ());
                int c = 0;
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int checkX = minX + x;
                        int checkZ = minY + y;
                        if (context.getLevel().getBlockState(new BlockPos(checkX, ClientSelectionData.pos1.getY()-1, checkZ)).is(BlockTags.STONE_BRICKS)) {
                            c++;

                        }
                    }
                }
                context.getPlayer().sendSystemMessage(Component.literal(String.valueOf(c)));
                ClientSelectionData.pos1 = null;
            }
        }

        return super.useOn(context);
    }


}
