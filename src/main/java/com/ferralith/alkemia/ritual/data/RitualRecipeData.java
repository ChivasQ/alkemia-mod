package com.ferralith.alkemia.ritual.data;

import net.minecraft.world.item.Item;

import javax.annotation.Nullable;
import java.util.List;

public class RitualRecipeData {
    private String name;
    private int color;
    private RecipeType type;
    private int tier;
    private int duration;
    private int mana_cost;

    @Nullable
    private List<Item> input_items;
    @Nullable
    private Item output_item;
    @Nullable
    private int amount;

//    @Nullable
//    private MultiblockStructure input_structure;
//    @Nullable
//    private boolean shaped_structure; // hard block placement (if false just count blocks to validate structure)
//    @Nullable
//    private MultiblockStructure output_structure;

    public enum RecipeType {
        CRAFT,
        ASSEMBLE_MULTIBLOCK,
        CUSTOM //class will be called if it's registered in registry
    }
}
