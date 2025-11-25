package com.ferralith.alkemia.datagen;

import com.ferralith.alkemia.registries.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.common.Mod;

import java.util.Set;

public class ModBlockLootProvider extends BlockLootSubProvider {
    protected ModBlockLootProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.EXAMPLE_BLOCK.get());
        dropSelf(ModBlocks.JAR_BLOCK.get());
        dropSelf(ModBlocks.COOL_LAVA_CAULDRON.get());
        dropSelf(ModBlocks.CHALKBOARD_BLOCK.get());
        dropSelf(ModBlocks.RITUAL_BLOCK.get());
        dropSelf(ModBlocks.PEDESTAL.get());

        dropSelf(ModBlocks.PORPHYRY_STONE.get());
        dropSelf(ModBlocks.PORPHYRY_BRICKS.get());
        dropSelf(ModBlocks.SMOOTH_PORPHYRY.get());
        //see BlockLootSubProvider.java for more examples of drops
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
