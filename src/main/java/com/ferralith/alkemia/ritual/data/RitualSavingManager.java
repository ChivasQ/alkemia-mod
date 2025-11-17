package com.ferralith.alkemia.ritual.data;

import com.ferralith.alkemia.ritual.RitualFigures;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class RitualSavingManager {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

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
}