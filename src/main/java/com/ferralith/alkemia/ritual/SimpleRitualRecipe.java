package com.ferralith.alkemia.ritual;

import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.List.copyOf;

public class SimpleRitualRecipe implements RitualRecipe{
    private final Set<Set<Set<Integer>>> allRotations;
    private final String name;
    private final int NODE_COUNT = 12;

    public SimpleRitualRecipe(List<Vector2i> requiredJoints, String name) {
        this.allRotations = genAllRotations(requiredJoints);
        this.name = name;
    }

    @Override
    public boolean matches(RitualFigures playerGraph) {
        //if nodes more than n
        List<Vector2i> joints = copyOf(playerGraph.getJoints());
        System.out.println(joints);
        List<Vector2i> out = new ArrayList<>();
        for (int i = 12; i < joints.size(); i++) {
            out.add(joints.get(i));
        }


        Set<Set<Integer>> playerSet = normalizeJoints(out);

        return this.allRotations.contains(playerSet);
    }

    @Override
    public String getName() {
        return name;
    }

    private Set<Set<Set<Integer>>> genAllRotations(List<Vector2i> baseJoints) {
        Set<Set<Set<Integer>>> rotations = new HashSet<>();


        for (int rotation = 0; rotation < NODE_COUNT; rotation++) {
            List<Vector2i> rotatedList = new ArrayList<>();

            for (Vector2i joint : baseJoints) {
                int n1 = (joint.x + rotation) % NODE_COUNT;
                int n2 = (joint.y + rotation) % NODE_COUNT;
                rotatedList.add(new Vector2i(n1, n2));
            }
            
            rotations.add(normalizeJoints(rotatedList));
        }
        return rotations;
    }

    private Set<Set<Integer>> normalizeJoints(List<Vector2i> joints) {
        return joints.stream()
                .map(joint -> Set.of(Math.min(joint.x, joint.y), Math.max(joint.x, joint.y)))
                .collect(Collectors.toSet());
    }
}
