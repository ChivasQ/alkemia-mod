package com.ferralith.alkemia.ritual;

import org.joml.Vector2i;

import java.util.*;

public class RitualRecipeMatcher {
    public static boolean match(RitualFigures graph, RitualFigures template) {
        Map<Integer, List<Integer>> graph_adj = buildAdjacencyList(graph.getJoints());
        Map<Integer, List<Integer>> template_adj = buildAdjacencyList(template.getJoints());

        if (graph_adj.size() != template_adj.size()) return false;

        if (graph.getJoints().size() != template.getJoints().size()) return false;

        Map<Integer, Integer> mapping = new HashMap<>();
        Set<Integer> usedTemplateVertices = new HashSet<>();

        List<Integer> playerNodes = new ArrayList<>(graph_adj.keySet());
        List<Integer> templateNodes = new ArrayList<>(template_adj.keySet());

        return solve(0, playerNodes, templateNodes, graph_adj, template_adj, mapping, usedTemplateVertices);
    }

    private static boolean solve(int index,
                                 List<Integer> playerNodes,
                                 List<Integer> templateNodes,
                                 Map<Integer, List<Integer>> graphAdj,
                                 Map<Integer, List<Integer>> templateAdj,
                                 Map<Integer, Integer> mapping,
                                 Set<Integer> usedTemplateVertices) {
        if (index == playerNodes.size()) {
            return true;
        }

        int p = playerNodes.get(index);

        for (var t : templateNodes) {
            if (usedTemplateVertices.contains(t)) {
                continue;
            }

            if (checkFit(p, t, graphAdj, templateAdj, mapping)) {
                mapping.put(p, t);
                usedTemplateVertices.add(t);

                if (solve(index + 1, playerNodes, templateNodes, graphAdj, templateAdj, mapping, usedTemplateVertices)) {
                    return true;
                }

                mapping.remove(p);
                usedTemplateVertices.remove(t);
            }
        }
        return false;
    }


    private static boolean checkFit(int p, int t,
                                    Map<Integer, List<Integer>> graph,
                                    Map<Integer, List<Integer>> template,
                                    Map<Integer, Integer> mapping) {
        if (graph.get(p).size() != template.get(t).size()) return false;

        for (int neighnborP : graph.get(p)) {
            if (mapping.containsKey(neighnborP)) {
                int neighnborT = mapping.get(neighnborP);
                if (!template.get(t).contains(neighnborT)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static Map<Integer, Integer> countDegrees(Map<Integer, List<Integer>> adj) { //TODO: to create signatures for rituals in order to reduce the number of checks
        Map<Integer, Integer> degreeCounts = new HashMap<>();
        for (List<Integer> neighbors : adj.values()) {
            int degree = neighbors.size();
            degreeCounts.put(degree, degreeCounts.getOrDefault(degree, 0) + 1);
        }
        return degreeCounts;
    }

    private static Map<Integer, List<Integer>> buildAdjacencyList(List<Vector2i> edges) {
        Map<Integer, List<Integer>> adj = new HashMap<>();

        if (edges == null) return adj;

        for (Vector2i edge : edges) {
            adj.computeIfAbsent(edge.x, k -> new ArrayList<>()).add(edge.y);
            adj.computeIfAbsent(edge.y, k -> new ArrayList<>()).add(edge.x);
        }
        return adj;
    }
}
