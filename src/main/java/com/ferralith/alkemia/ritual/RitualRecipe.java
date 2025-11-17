package com.ferralith.alkemia.ritual;

public interface RitualRecipe {
    boolean matches(RitualFigures graph);
    String getName();
}
