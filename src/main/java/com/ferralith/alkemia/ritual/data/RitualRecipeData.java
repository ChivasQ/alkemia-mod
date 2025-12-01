package com.ferralith.alkemia.ritual.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;
import java.util.List;

public class RitualRecipeData {
    @Nullable
    public String id;
    public String template;
    public int color;
    public int tier;
    public int duration;
    public int mana_cost;

    @Nullable
    public JsonIngredient main_item;
    @Nullable
    public List<JsonIngredient> item_inputs;

    public List<JsonResult> results;


//    @Nullable
//    private MultiblockStructure input_structure;
//    @Nullable
//    private boolean shaped_structure; // hard block placement (if false just count blocks to validate structure)
//    @Nullable
//    private MultiblockStructure output_structure;

    public static class JsonIngredient {
        @Nullable public String item; // "minecraft:diamond"
        @Nullable public String tag;  // "forge:gems/diamond"
        public int count = 1;
        @Nullable public String nbt;

        public JsonIngredient() {
        }

        public JsonIngredient(@Nullable String item, @Nullable String tag, int count, @Nullable String nbt) {
            this.item = item;
            this.tag = tag;
            this.count = count;
            this.nbt = nbt;
        }
    }

    public static class JsonResult {
        public RecipeType type;

        public JsonElement data; //process depends on type (JsonObject)

        public JsonResult() {
        }

        public JsonResult(RecipeType type, JsonElement data) {
            this.type = type;
            this.data = data;
        }
    }

    public RitualRecipeData() {
    }

    public enum RecipeType {
        CRAFT,
        ASSEMBLE_MULTIBLOCK,
        CUSTOM //class will be called if it's registered in registry
    }
}
