package com.ferralith.alkemia.ritual;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class RitualFigures {
    List<Vec3> nodes = new ArrayList<>();
    List<Vector2i> joints = new ArrayList<>();
    public Vec3 blockPos;

    public RitualFigures(Vec3 pos, float radius) {
        init(pos, radius);
        blockPos = pos;
    }

    public RitualFigures(List<Vec3> nodes, List<Vector2i> ritualJoints) {
        this.nodes = nodes;
        this.joints = ritualJoints;
    }

    public List<Vec3> getNodes() {
        return nodes;
    }

    public List<Vector2i> getJoints() {
        return joints;
    }

    public void init(Vec3 pos, float radius) {
        for (int i = 0; i < 12; i++) {
            float angle = (float)Math.toRadians(i * (360.0f / 12));

            float x = (float)Math.cos(angle) * radius;
            float z = (float)Math.sin(angle) * radius;

            addNode(new Vec3(x, 0, z));
        }

        for (int i = 1; i < 12; i++) {
            _connectNodes(i-1, i);
        }
        _connectNodes(11, 0);
    }

    public Vec3 connectNodes(int nodeInd1, int nodeInd2) {
        if (nodeInd1 > nodes.size() || nodeInd2 > nodes.size()) return null;

        joints.add(new Vector2i(nodeInd1, nodeInd2));
        Vec3 node1 = nodes.get(nodeInd1);
        Vec3 node2 = nodes.get(nodeInd2);
        System.out.println(node1);
        System.out.println(node2);
        Vec3 node = new Vec3(
                (node1.x + node2.x) / 2,
                (node1.y + node2.y) / 2,
                (node1.z + node2.z) / 2);

        nodes.add(node);
        return node;
    }

    private void _connectNodes(int nodeInd1, int nodeInd2) {
        if (nodeInd1 > nodes.size() || nodeInd2 > nodes.size()) return;

        joints.add(new Vector2i(nodeInd1, nodeInd2));
    }

    public void addNode(Vec3 pos) {
        nodes.add(pos);
    }

    public Vec3 getNode(int ind) {
        return nodes.get(ind);
    }

    public int getNodeIndex(Vec3 pos) {
        for (int i = 0; i < nodes.size(); i++) {
            Vec3 node = nodes.get(i);
            if (node.distanceTo(pos) < 5) {
                return i;
            }
        }
        return -1;
    }

    public CompoundTag serializeNBT(HolderLookup.Provider registries) {
        CompoundTag nbt = new CompoundTag();

        ListTag nodesTag = new ListTag();
        for (Vec3 node : nodes) {
            CompoundTag nodeTag = new CompoundTag();
            nodeTag.putDouble("x", node.x);
            nodeTag.putDouble("y", node.y);
            nodeTag.putDouble("z", node.z);
            nodesTag.add(nodeTag);
        }
        nbt.put("nodes", nodesTag);

        ListTag jointsTag = new ListTag();
        for (Vector2i joint : joints) {
            CompoundTag jointTag = new CompoundTag();
            jointTag.putInt("n1", joint.x);
            jointTag.putInt("n2", joint.y);
            jointsTag.add(jointTag);
        }
        nbt.put("joints", jointsTag);

        return nbt;
    }

    public void deserializeNBT(HolderLookup.Provider registries, CompoundTag nbt) {
        nodes.clear();
        joints.clear();

        ListTag nodesTag = nbt.getList("nodes", Tag.TAG_COMPOUND);
        for (Tag tag : nodesTag) {
            CompoundTag nodeTag = (CompoundTag) tag;
            double x = nodeTag.getDouble("x");
            double y = nodeTag.getDouble("y");
            double z = nodeTag.getDouble("z");
            nodes.add(new Vec3(x, y, z));
        }

        ListTag jointsTag = nbt.getList("joints", Tag.TAG_COMPOUND);
        for (Tag tag : jointsTag) {
            CompoundTag jointTag = (CompoundTag) tag;
            int n1 = jointTag.getInt("n1");
            int n2 = jointTag.getInt("n2");
            joints.add(new Vector2i(n1, n2));
        }
    }
}
