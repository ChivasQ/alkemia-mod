package com.ferralith.alkemia.ritual.data;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.ritual.RitualFigures;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class RitualJsonScraper {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();



    public static List<String> scrapRecipes(ResourceManager resourceManager) {
        Map<ResourceLocation, Resource> resources = resourceManager.listResources("ritual/recipe",
                location -> location.getNamespace().equals("alkemia") && location.getPath().endsWith(".json")
        );

        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            ResourceLocation id = entry.getKey();
            Resource resource = entry.getValue();

            try (var reader = resource.openAsReader()) {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void saveRecipeToFile(RitualRecipeData data, Path filePath) {
        try {
            String jsonString = GSON.toJson(data);

            Files.createDirectories(filePath.getParent());

            Files.writeString(filePath, jsonString, StandardCharsets.UTF_8);

            System.out.println("Recipe saved to: " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RitualRecipeData loadRecipeFromResources(String resourcePath) {

        try (InputStream stream = Alkemia.class.getResourceAsStream(resourcePath)) {

            if (stream == null) {
                System.err.println("File not found: " + resourcePath);
                return null;
            }
            try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                return GSON.fromJson(reader, RitualRecipeData.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveRitualToFile(RitualFigures graph, Path filePath) {

        try {
            RitualTemplateData dataToSave = new RitualTemplateData(graph);

            String jsonString = GSON.toJson(dataToSave);

            Files.createDirectories(filePath.getParent());

            Files.writeString(filePath, jsonString, StandardCharsets.UTF_8);

            System.out.println("Ritual saved to: " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RitualFigures loadRitualFromResources(String resourcePath) {

        try (InputStream stream = Alkemia.class.getResourceAsStream(resourcePath)) {

            if (stream == null) {
                System.err.println("File not found: " + resourcePath);
                return null;
            }
            try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                RitualTemplateData ritualTemplateData = GSON.fromJson(reader, RitualTemplateData.class);
                return ritualTemplateData.toRitualFigures();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}