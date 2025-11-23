package com.ferralith.alkemia.event;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.client.RitualDrawHandler;
import com.ferralith.alkemia.ritual.data.RitualRecipeData;
import com.ferralith.alkemia.ritual.data.RitualRecipeManager;
import com.ferralith.alkemia.ritual.data.RitualTemplateData;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Map;

@EventBusSubscriber(modid = Alkemia.MODID)
public class ModNeoForgeEvents {
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        RitualDrawHandler.handle(event);
    }

    private static Gson GSON = new Gson();

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new SimpleJsonResourceReloadListener(GSON, "ritual/template") {
            @Override
            protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager manager, ProfilerFiller profiler) {
                RitualRecipeManager.clear();
                objects.forEach((id, json) -> {
                    try {
                        RitualTemplateData data = GSON.fromJson(json, RitualTemplateData.class);
                        System.out.println("scraping template: " + id);
                        RitualRecipeManager.registerTemplate(id, data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });

        event.addListener(new SimpleJsonResourceReloadListener(GSON, "ritual/recipe") {
            @Override
            protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager manager, ProfilerFiller profiler) {
                objects.forEach((id, json) -> {
                    try {
                        RitualRecipeData data = GSON.fromJson(json, RitualRecipeData.class);
                        System.out.println("scraping recipe: " + id);
                        RitualRecipeManager.registerRecipe(id, data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }


}
