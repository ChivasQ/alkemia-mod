package com.ferralith.alkemia.entity.renderer.ritualblock;

import com.ferralith.alkemia.entity.ritualblock.RitualMasterBlockEntity;
import com.ferralith.alkemia.ritual.RitualFigures;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class RitualMasterBlockRenderer implements BlockEntityRenderer<RitualMasterBlockEntity> {

    @Override
    public boolean shouldRender(RitualMasterBlockEntity blockEntity, Vec3 cameraPos) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(RitualMasterBlockEntity blockEntity) {
        int radius = blockEntity.getRadius();
        Vec3 pos = blockEntity.getBlockPos().getBottomCenter();
        return new AABB(pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius));
    }

    @Override
    public boolean shouldRenderOffScreen(RitualMasterBlockEntity blockEntity) {
        return true;
    }

    @Override
    public void render(RitualMasterBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int overlay) {
        poseStack.pushPose();
        BlockPos pos = be.getBlockPos();
        poseStack.translate(0.5, 1.01, 0.5);

        Matrix4f matrix = poseStack.last().pose();

        float radius = be.getRadius() - 1.5f;
        if (radius <= 0) {
            poseStack.popPose();
            return;
        }

        RitualFigures graph = be.getGraph();

        VertexConsumer lineConsumer = buffer.getBuffer(RenderType.lines());

        for (var conn : graph.getJoints()) {
            Vec3 p1 = graph.getNode(conn.x);
            Vec3 p2 = graph.getNode(conn.y);
//            System.out.println(p1);
            lineConsumer.addVertex(matrix, (float)p1.x, (float)p1.y, (float)p1.z)
                    .setColor(1f, 1f, 1f, 1f)
                    .setNormal(0, 1, 0);
            lineConsumer.addVertex(matrix, (float)p2.x, (float)p2.y, (float)p2.z)
                    .setColor(1f, 1f, 1f, 1f)
                    .setNormal(0, 1, 0);
        }

        poseStack.popPose();
    }

    private Vec3 getNodeWorldPos(int nodeIndex, float radius) {
        double angle = Math.toRadians(360.0 / 12 * nodeIndex - 90);
        double x = Math.cos(angle) * radius;
        double z = Math.sin(angle) * radius;
        return new Vec3(x, 0, z);
    }

    public RitualMasterBlockRenderer(BlockEntityRendererProvider.Context context) {
    }
}
