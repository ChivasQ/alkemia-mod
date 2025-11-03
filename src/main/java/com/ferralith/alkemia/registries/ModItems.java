package com.ferralith.alkemia.registries;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.item.SketchingQuillItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Alkemia.MODID);

    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", ModBlocks.EXAMPLE_BLOCK);

    // Creates a new food item with the id "alkemia:example_id", nutrition 1 and saturation 2
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    public static final DeferredItem<BlockItem> JAR_ITEM = ITEMS.registerSimpleBlockItem("jar_item", ModBlocks.JAR_BLOCK);

    public static final DeferredHolder<Item, BucketItem> MANA_BUCKET = ITEMS.register(
            "mana_bucket",
            () -> new BucketItem(ModFluids.SOURCE_MANA.get(), new Item.Properties())
    );

    public static final DeferredItem<Item> SKETCHING_QUILL = ITEMS.register("sketching_quill", () -> new SketchingQuillItem(new Item.Properties()));

    public static final DeferredItem<Item> CHALK_ITEM = ITEMS.registerSimpleItem("chalk", new Item.Properties().stacksTo(1).durability(100));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
