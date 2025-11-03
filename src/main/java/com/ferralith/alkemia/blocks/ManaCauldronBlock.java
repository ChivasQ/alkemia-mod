package com.ferralith.alkemia.blocks;

import com.ferralith.alkemia.registries.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ManaCauldronBlock extends AbstractCauldronBlock {
    public static final MapCodec<ManaCauldronBlock> CODEC = simpleCodec(ManaCauldronBlock::new);

    public ManaCauldronBlock(Properties properties) {
        super(properties, createInteractionMap());
    }

    private static CauldronInteraction.InteractionMap createInteractionMap() {
        CauldronInteraction.InteractionMap interaction = CauldronInteraction.newInteractionMap("mana");
        interaction.map().put(Items.BUCKET,  (state, level, pos, player, hand, stack) ->
                CauldronInteraction.fillBucket(
                        state,
                        level,
                        pos,
                        player,
                        hand,
                        stack,
                        new ItemStack(ModItems.MANA_BUCKET),
                        other_state -> true,
                        SoundEvents.BUCKET_FILL_LAVA
                )
        );

        return interaction;
    }

    @Override
    protected MapCodec<? extends AbstractCauldronBlock> codec() {
        return CODEC;
    }

    @Override
    public boolean isFull(BlockState blockState) {
        return true;
    }
}
