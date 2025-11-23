package com.ferralith.alkemia.ritual.data;

import com.ferralith.alkemia.ritual.RitualFigures;
import com.ferralith.alkemia.ritual.RitualRecipeMatcher;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RitualRecipeManager {
    private static final Map<ResourceLocation, RitualRecipeData> RECIPES = new HashMap<>();
    private static final Map<ResourceLocation, RitualTemplateData> TEMPLATES = new HashMap<>();

    public static void registerRecipe(ResourceLocation id, RitualRecipeData recipe) {
        RECIPES.put(id, recipe);
    }

    public static void registerTemplate(ResourceLocation id, RitualTemplateData recipe) {
        TEMPLATES.put(id, recipe);
    }

    public static RitualRecipeData getRecipe(ResourceLocation id) { return RECIPES.get(id); }
    public static RitualTemplateData getTemplate(ResourceLocation id) { return TEMPLATES.get(id); }
    public static Collection<RitualRecipeData> getRecipeEntries() { return RECIPES.values(); }
    public static Collection<RitualTemplateData> getTemplateEntries() { return TEMPLATES.values(); }
    public static void clear() {
        TEMPLATES.clear();
        RECIPES.clear();
    }


    public static RitualRecipeData findMatchingRecipe(RitualFigures playerGraph) {
        for (RitualRecipeData recipeData : RECIPES.values()) {
            RitualTemplateData templateData = getTemplate(ResourceLocation.parse(recipeData.template));

            if (templateData == null) {
                System.err.println("Missing template for recipe: " + recipeData.template);
                continue;
            }

            RitualFigures templateGraph = templateData.toRitualFigures();

            if (RitualRecipeMatcher.match(playerGraph, templateGraph)) {
                return recipeData;
            }
        }
        return null;
    }
}
