package com.ferralith.alkemia.datagen;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.registries.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Alkemia.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.EXAMPLE_BLOCK);
        ModelFile jar_model = this.models().withExistingParent("jar_block", this.modLoc("block/jar")).renderType("translucent");
        simpleBlockWithItem(ModBlocks.JAR_BLOCK.get(), jar_model);

        simpleBlock(
                ModBlocks.MANA_BLOCK.get(),
                models().getExistingFile(mcLoc("lava"))
        );

    }

    private void blockWithItem(DeferredBlock<Block> exampleBlock) {
        simpleBlockWithItem(exampleBlock.get(), cubeAll(exampleBlock.get()));
    }

}
