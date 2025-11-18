package com.ferralith.alkemia.entity.renderer.ritualblock;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.entity.ritualblock.RitualMasterBlockEntity;
import com.ferralith.alkemia.ritual.RitualFigures;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class RitualMasterBlockRenderer implements BlockEntityRenderer<RitualMasterBlockEntity> {
    private static final float GLOW_WIDTH = 0.05f;
    private static final float GLOW_HEIGHT_SCALE = 0.02f;
    private static final Vector4f GLOW_COLOR = new Vector4f(0.8f, 0.2f, 1.0f, 0.7f);
    private static final ResourceLocation ACTIVE = ResourceLocation.fromNamespaceAndPath(Alkemia.MODID, "textures/ritual/active.png");
    private static final RenderType BEAM_RENDER_TYPE = RenderType.entityTranslucentEmissive(ACTIVE, false);


    @Override
    public boolean shouldRender(RitualMasterBlockEntity blockEntity, Vec3 cameraPos) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(RitualMasterBlockEntity blockEntity) {
        int radius = blockEntity.getRadius();
        Vec3 pos = blockEntity.getBlockPos().getBottomCenter();
        // Расширяем bounding box, чтобы свечение было видно
        return new AABB(pos.add(-radius, -radius, -radius), pos.add(radius, radius * 2, radius));
    }

    @Override
    public boolean shouldRenderOffScreen(RitualMasterBlockEntity blockEntity) {
        return true;
    }

    @Override
    public void render(RitualMasterBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int overlay) {
        poseStack.pushPose();

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

            lineConsumer.addVertex(matrix, (float)p1.x, (float)p1.y, (float)p1.z)
                    .setColor(1f, 1f, 1f, 1f)
                    .setNormal(0, 1, 0);
            lineConsumer.addVertex(matrix, (float)p2.x, (float)p2.y, (float)p2.z)
                    .setColor(1f, 1f, 1f, 1f)
                    .setNormal(0, 1, 0);
        }
        poseStack.popPose();

        if (be.getProgress() > 0) {
            VertexConsumer glowConsumer = buffer.getBuffer(BEAM_RENDER_TYPE);

            float currentProgress = be.getProgress() + partialTick;
            float glowHeight = currentProgress * GLOW_HEIGHT_SCALE;

            glowHeight = Math.min(glowHeight, 100 * GLOW_HEIGHT_SCALE);

            for (var conn : graph.getJoints()) {
                Vec3 p1 = graph.getNode(conn.x);
                Vec3 p2 = graph.getNode(conn.y);
                poseStack.pushPose();
                poseStack.translate(0.5, 1.01, 0.5);

                drawQuad(glowConsumer, poseStack,
                        (float) p1.x, (float) p1.y, (float) p1.z,
                        (float) p2.x, (float) p2.y+glowHeight, (float) p2.z, 0, 0, 1,1, packedLight, overlay);
                poseStack.popPose();
            }
        }
    }

    private void drawVertex(VertexConsumer buffer, PoseStack poseStack,
                            float x, float y, float z, float u, float v, int light, int overlay) {
        buffer.addVertex(poseStack.last().pose(), x, y, z)
                .setColor(0f, 1f, 1f, 0.7f)
                .setUv(u, v)
                .setLight(light)
                .setOverlay(overlay)
                .setNormal(1, 0, 0);
    }

    private void drawQuad(VertexConsumer builder, PoseStack poseStack,
                          float x0, float y0, float z0,
                          float x1, float y1, float z1, float u0, float v0, float u1, float v1, int light, int overlay) {
        drawVertex(builder, poseStack, x1, y0, z1, u0, v0, light, overlay);
        drawVertex(builder, poseStack, x1, y1, z1, u0, v1, light, overlay);
        drawVertex(builder, poseStack, x0, y1, z0, u1, v1, light, overlay);
        drawVertex(builder, poseStack, x0, y0, z0, u1, v0, light, overlay);


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