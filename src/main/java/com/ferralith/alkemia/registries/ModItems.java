package com.ferralith.alkemia.registries;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.item.ChalkItem;
import com.ferralith.alkemia.item.CloakItem;
import com.ferralith.alkemia.item.DebugWand;
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
    public static final DeferredItem<BlockItem> CHALKBOARD_ITEM = ITEMS.registerSimpleBlockItem("chalkboard_item", ModBlocks.CHALKBOARD_BLOCK);
    public static final DeferredItem<BlockItem> RITUAL_ITEM = ITEMS.registerSimpleBlockItem("ritual_item", ModBlocks.RITUAL_BLOCK);
    public static final DeferredItem<BlockItem> JAR_ITEM = ITEMS.registerSimpleBlockItem("jar_item", ModBlocks.JAR_BLOCK);
    public static final DeferredHolder<Item, BucketItem> MANA_BUCKET = ITEMS.register("mana_bucket", () -> new BucketItem(ModFluids.SOURCE_MANA.get(), new Item.Properties()));
    public static final DeferredItem<Item> SKETCHING_QUILL = ITEMS.register("sketching_quill", () -> new SketchingQuillItem(new Item.Properties()));
    public static final DeferredItem<Item> CHALK_ITEM = ITEMS.register("chalk", () ->  new ChalkItem(new Item.Properties().stacksTo(1).durability(100)));
    public static final DeferredItem<Item> DEBUG_WAND = ITEMS.register("debug_wand", () ->  new DebugWand(new Item.Properties()));
    public static final DeferredItem<Item> CLOAK_ITEM = ITEMS.register("cloak", CloakItem::new);



    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
