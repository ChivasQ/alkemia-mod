package com.ferralith.alkemia.ritual;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2i;

import java.util.*;
import java.util.stream.Collectors;

public class NestedCirclesRecipe implements RitualRecipe{
    private final List<Integer> circleNodeCounts;
    private final String name;

    public NestedCirclesRecipe(List<Integer> circleNodeCounts, String name) {
        this.circleNodeCounts = new ArrayList<>(circleNodeCounts);
        Collections.sort(this.circleNodeCounts);
        this.name = name;
    }


    @Override
    public boolean matches(RitualFigures graph) {
        Map<Integer, List<Integer>> adj = buildAdjacencyList(graph.joints);
        Set<Integer> allNodes = new HashSet<>();
        adj.forEach((node, neighbors) -> {
            allNodes.add(node);
            allNodes.addAll(neighbors);
        });
        Set<Integer> visitedNodes = new HashSet<>();
        List<Integer> foundCycleSizes = new ArrayList<>();
        for (int node : allNodes) {
            if (!visitedNodes.contains(node)) {
                List<Integer> componentNodes = new ArrayList<>();
                findConnectedComponent(node, adj, visitedNodes, componentNodes);

                if (isPerfectCycle(componentNodes, graph.joints)) {
                    foundCycleSizes.add(componentNodes.size());
                } else {

//                    if (!componentNodes.isEmpty()) {
//                        return false;
//                    }
                }
            }
        }

        Collections.sort(foundCycleSizes);
        Minecraft.getInstance().player.sendSystemMessage(Component.literal(foundCycleSizes.toString()));
        return foundCycleSizes.equals(this.circleNodeCounts);
    }

    private void findConnectedComponent(int startNode, Map<Integer, List<Integer>> adj,
                                        Set<Integer> visitedNodes, List<Integer> componentNodes) {

        Queue<Integer> queue = new LinkedList<>();
        queue.add(startNode);
        visitedNodes.add(startNode);

        while (!queue.isEmpty()) {
            int node = queue.poll();
            componentNodes.add(node);

            for (int neighbor : adj.getOrDefault(node, Collections.emptyList())) {
                if (!visitedNodes.contains(neighbor)) {
                    visitedNodes.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }



    private boolean isPerfectCycle(List<Integer> nodes, List<Vector2i> allJoints) {

        Set<Integer> nodeSet = new HashSet<>(nodes);
        List<Vector2i> edges = allJoints.stream()
                .filter(joint -> nodeSet.contains(joint.x) && nodeSet.contains(joint.y))
                .collect(Collectors.toList());

        if (edges.size() != nodes.size()) return false;

        Map<Integer, Integer> degreeMap = new HashMap<>();
        for (int node : nodes) degreeMap.put(node, 0);

        for (Vector2i edge : edges) {
            degreeMap.put(edge.x, degreeMap.get(edge.x) + 1);
            degreeMap.put(edge.y, degreeMap.get(edge.y) + 1);
        }

        for (int degree : degreeMap.values()) {
            if (degree != 2) return false;
        }

        Set<Integer> visited = new HashSet<>();
        Stack<Integer> stack = new Stack<>();
        stack.push(nodes.get(0));
        visited.add(nodes.get(0));

        Map<Integer, List<Integer>> adjacencyList = buildAdjacencyList(edges);
        while (!stack.isEmpty()) {
            int node = stack.pop();
            for (int neighbor : adjacencyList.getOrDefault(node, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    stack.push(neighbor);
                }
            }
        }

        return visited.size() == nodes.size();
    }

    private void searchLoops(Map<Integer, List<Integer>> adjacencyList) {

    }

    private Map<Integer, List<Integer>> buildAdjacencyList(List<Vector2i> edges) {
        Map<Integer, List<Integer>> adj = new HashMap<>();
        for (Vector2i edge : edges) {
            adj.computeIfAbsent(edge.x, k -> new ArrayList<>()).add(edge.y);
            adj.computeIfAbsent(edge.y, k -> new ArrayList<>()).add(edge.x);
        }
        return adj;
    }

}
