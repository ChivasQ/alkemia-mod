package com.ferralith.alkemia.ritual.data;

import com.ferralith.alkemia.ritual.RitualFigures;
import org.joml.Vector2i;

import java.util.*;

public class RitualTemplateData {
    private List<Integer> nodes;

    private List<JointData> joints;

    public static class JointData {
        public int from;
        public int to;

        public JointData() {}

        public JointData(int from, int to) { this.from = from; this.to = to; }
    }

    public List<Integer> getNodes() { return nodes; }
    public List<JointData> getJoints() { return joints; }

    public RitualFigures toRitualFigures() {
        List<Vector2i> ritualJoints = new ArrayList<>();
        if (joints != null) {
            for (JointData joint : joints) {
                ritualJoints.add(new Vector2i(joint.from, joint.to));
            }
        }

        return new RitualFigures(null, ritualJoints);
    }

    public RitualTemplateData(RitualFigures graph) {
        Set<Integer> nodeSet = new HashSet<>();
        this.joints = new ArrayList<>();

        if (graph.getJoints() != null) {
            for (Vector2i joint : graph.getJoints()) {
                this.joints.add(new JointData(joint.x, joint.y));

                nodeSet.add(joint.x);
                nodeSet.add(joint.y);
            }
        }

        this.nodes = new ArrayList<>(nodeSet);
        Collections.sort(this.nodes);
    }
}