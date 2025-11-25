package com.ferralith.alkemia.ritual.data;

import com.ferralith.alkemia.entity.PedestalBlockEntity;
import com.ferralith.alkemia.ritual.RitualFigures;
import com.ferralith.alkemia.ritual.RitualRecipeMatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;

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

    private static ItemStack getItemInPedestal(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof PedestalBlockEntity pedestalBlockEntity) {
            return pedestalBlockEntity.inventory.getStackInSlot(0);
        }
        return ItemStack.EMPTY;
    }

    private static boolean matches(ItemStack stack, RitualRecipeData.JsonIngredient ingredient) {
        if (stack.isEmpty() && ingredient.item == null) return true;
        if (stack.isEmpty()) return false;

        if (ingredient.item != null) {
            ResourceLocation itemLoc = ResourceLocation.parse(ingredient.item);
            if (!stack.is(BuiltInRegistries.ITEM.get(itemLoc))) {
                return false;
            }
        }

        else if (ingredient.tag != null) {
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, ResourceLocation.parse(ingredient.tag));
            if (!stack.is(tagKey)) {
                return false;
            }
        }

        return true;
    }

    public static RitualRecipeData findMatchingRecipe(RitualFigures playerGraph, List<BlockPos> pedestalList, Level level, BlockPos pos) {
        ItemStack mainItem = getItemInPedestal(level, pos.above());
        List<ItemStack> itemStackList = new ArrayList<>();
        for (BlockPos blockPos : pedestalList) {
            ItemStack stack = getItemInPedestal(level, blockPos);
            if (!stack.isEmpty()) {
                itemStackList.add(stack.copy());
                System.out.println("item in pedestal: " + stack);
            }
        }



        for (RitualRecipeData recipeData : RECIPES.values()) {
            RitualTemplateData templateData = getTemplate(ResourceLocation.parse(recipeData.template));
            if (templateData == null) {
                System.err.println("Missing template for recipe: " + recipeData.template);
                continue;
            }
            if (!RitualRecipeMatcher.match(playerGraph, templateData.toRitualFigures())) {
                continue;
            }
            if ( !(recipeData.main_item.item == null)) {
                if (! matches(mainItem, recipeData.main_item)) {
                    continue;
                }

                if (mainItem.getCount() < recipeData.main_item.count) {
                    continue;
                }
            } else {
                BlockEntity blockEntity = level.getBlockEntity(pos.above());
                if (blockEntity instanceof PedestalBlockEntity pedestal) {
                    if(!pedestal.inventory.getStackInSlot(0).isEmpty()) {
                        continue;
                    }
                }
            }

            if (checkInputs(recipeData.item_inputs, itemStackList)) {
                return recipeData;
            }
        }
        return null;
    }

    private static boolean checkInputs(List<RitualRecipeData.JsonIngredient> itemInputs, List<ItemStack> itemStackList) {
        if (itemInputs == null || itemInputs.isEmpty()) {
            return true;
        }

        List<ItemStack> testPool = new ArrayList<>();
        for (ItemStack s : itemStackList) {
            if (!s.isEmpty()) {
                testPool.add(s.copy());
            }
        }

        for (RitualRecipeData.JsonIngredient ingredient : itemInputs) {
            int amountNeeded = ingredient.count;

            Iterator<ItemStack> iterator = testPool.iterator();
            while (iterator.hasNext()) {
                ItemStack poolStack = iterator.next();

                if (matches(poolStack, ingredient)) {
                    int amountToTake = Math.min(amountNeeded, poolStack.getCount());

                    poolStack.shrink(amountToTake);
                    amountNeeded -= amountToTake;

                    if (poolStack.isEmpty()) {
                        iterator.remove();
                    }

                    if (amountNeeded <= 0) {
                        break;
                    }
                }
            }

            if (amountNeeded > 0) {
                return false;
            }
        }

        return true;
    }
}
