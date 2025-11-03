package com.ferralith.alkemia.datagen;

import com.ferralith.alkemia.Alkemia;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Alkemia.MODID)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator dataGenerator = event.getGenerator();
        PackOutput packOutput = dataGenerator.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        dataGenerator.addProvider(event.includeServer(),
                new LootTableProvider(
                        packOutput,
                        Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(
                        ModBlockLootProvider::new,
                        LootContextParamSets.BLOCK)), lookupProvider));

        BlockTagsProvider blockTagsProvider = new ModBlockTagProvider(packOutput, lookupProvider, fileHelper);
        dataGenerator.addProvider(event.includeServer(), blockTagsProvider);

        dataGenerator.addProvider(event.includeServer(), new ModItemTagProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), fileHelper));


        dataGenerator.addProvider(event.includeClient(),
                new ModItemModelProvider(
                        packOutput,fileHelper));

        dataGenerator.addProvider(event.includeClient(),
                new ModBlockStateProvider(
                        packOutput,fileHelper));

        dataGenerator.addProvider(event.includeClient(), new ModBlockModelProvider(packOutput, fileHelper));
    }
}
