package com.ferralith.alkemia.datagen;

import com.ferralith.alkemia.Alkemia;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockModelProvider extends BlockModelProvider {
    public ModBlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Alkemia.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
//        withExistingParent("mana_block", modLoc("block/mana"));

    }
}
