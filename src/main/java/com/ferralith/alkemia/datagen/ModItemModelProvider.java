package com.ferralith.alkemia.datagen;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.registries.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Alkemia.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.CHALK_ITEM.asItem());
//        withExistingParent("jar_item", this.mcLoc("builtin/entity"));
        basicItem(ModItems.MANA_BUCKET.get());
        basicItem(ModItems.SKETCHING_QUILL.get());
    }

    public ItemModelBuilder blockItem(String name, ResourceLocation resourceLocation) {
        return (ItemModelBuilder)this.withExistingParent(name, resourceLocation).renderType("translucent");
    }
}
