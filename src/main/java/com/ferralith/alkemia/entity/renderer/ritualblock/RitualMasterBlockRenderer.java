package com.ferralith.alkemia.entity.renderer.ritualblock;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.entity.ritualblock.RitualMasterBlockEntity;
import com.ferralith.alkemia.particle.ManaParticle;
import com.ferralith.alkemia.registries.ModParticles;
import com.ferralith.alkemia.registries.ModRenderTypes;
import com.ferralith.alkemia.ritual.RitualFigures;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.awt.*;
import java.util.List;

public class RitualMasterBlockRenderer implements BlockEntityRenderer<RitualMasterBlockEntity> {
    private static final float GLOW_WIDTH = 0.05f;
    private static final float GLOW_HEIGHT_SCALE = 0.02f;
    private static final Vector4f GLOW_COLOR = new Vector4f(0.8f, 0.2f, 1.0f, 0.7f);
    private static final ResourceLocation ACTIVE = ResourceLocation.fromNamespaceAndPath(Alkemia.MODID, "textures/ritual/active.png");
    private static final ResourceLocation CHALK = ResourceLocation.fromNamespaceAndPath(Alkemia.MODID, "textures/ritual/chalk_texture.png");
    private static final RenderType CHALK_RENDER_TYPE = RenderType.entityCutoutNoCull(CHALK);
    private static final RenderType BEAM_RENDER_TYPE = RenderType.entityTranslucentEmissive(ACTIVE, true);


    @Override
    public boolean shouldRender(RitualMasterBlockEntity blockEntity, Vec3 cameraPos) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(RitualMasterBlockEntity blockEntity) {
        int radius = blockEntity.getRadius();
        Vec3 pos = blockEntity.getBlockPos().getBottomCenter();
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

        VertexConsumer lineConsumer = buffer.getBuffer(RenderType.debugQuads());
        float lineWidth = 0.1f;

        for (var conn : graph.getJoints()) {
            Vec3 p1 = graph.getNode(conn.x);
            Vec3 p2 = graph.getNode(conn.y);

            drawFlatThickLine(lineConsumer, matrix,
                    (float)p1.x, (float)p1.y, (float)p1.z,
                    (float)p2.x, (float)p2.y, (float)p2.z,
                    lineWidth);
        }
        poseStack.popPose();
        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        List<BlockPos> lockedPedestals = be.getLockedPedestals();
        int index = be.getCurrentPedestalIndex();
        float time = (float) Math.min(20, be.getConsumeTimer()) / 20;
        VertexConsumer debuglineConsumer = buffer.getBuffer(RenderType.lines());
        Matrix4f debugMatrix = poseStack.last().pose();
        if (lockedPedestals != null && !lockedPedestals.isEmpty() && lockedPedestals.size() > index) {
            BlockPos pos = lockedPedestals.get(index);
            float dx = pos.getX() - be.getBlockPos().getX();
            float dy = pos.getY() - be.getBlockPos().getY();
            float dz = pos.getZ() - be.getBlockPos().getZ();

            debuglineConsumer.addVertex(debugMatrix, dx - dx * time, (dy - dy * time) + 2.5f, dz - dz * time)
                    .setColor(1f, 1f, 1f, 1f)
                    .setNormal(0, 1, 0);
            debuglineConsumer.addVertex(debugMatrix, dx, dy+1.5f, dz)
                    .setColor(1f, 1f, 1f, 1f)
                    .setNormal(0, 1, 0);
        }
        poseStack.popPose();

        Font font = Minecraft.getInstance().gui.getFont();
        poseStack.pushPose();
        poseStack.translate(0.5, 4, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(- Minecraft.getInstance().player.yHeadRot + 180));
        poseStack.scale(0.05f,-0.05f,0.05f);
        renderText(font, be.getState().name(), 0, 0, 0.05f, poseStack, buffer, packedLight);
        poseStack.popPose();

//        if (be.getProgress() > 0 && false) {
//            VertexConsumer glowConsumer = buffer.getBuffer(ModRenderTypes.brightSolid(CHALK));
//
//            float currentProgress = be.getProgress() + partialTick;
//            float glowHeight = currentProgress * GLOW_HEIGHT_SCALE;
//
//            glowHeight = Math.min(glowHeight, 100 * GLOW_HEIGHT_SCALE);
//            float alpha = 0.7f * (currentProgress/100);
//            for (var conn : graph.getJoints()) {
//                Vec3 p1 = graph.getNode(conn.x);
//                Vec3 p2 = graph.getNode(conn.y);
//                double v = p1.distanceTo(p2);
//                poseStack.pushPose();
//                poseStack.translate(0.5, 1.01, 0.5);
//
//                drawQuad(glowConsumer, poseStack,
//                        (float) p1.x, (float) p1.y, (float) p1.z,
//                        (float) p2.x, (float) p2.y+glowHeight, (float) p2.z,
//                        (float) 0, 1-currentProgress/100,
//                        (float) v /2.56f,1f,
//                        0xF000F0,
//                        OverlayTexture.NO_OVERLAY,
//                        alpha);
//                poseStack.popPose();
//            }
//        }
    }

    private static void renderText(Font font, String string, int x, int y, float size, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        font.drawInBatch8xOutline(
                FormattedCharSequence.forward(string, Style.EMPTY),
                (float) - ((string.length() /2)+1)*5,
                0,
                RGBAtoINT(0, 0, 0, 255),
                RGBAtoINT(255, 255, 255, 255),
                pPoseStack.last().pose(),
                pBuffer,
                255
        );
    }

    public static int RGBAtoINT(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private void drawVertex(VertexConsumer buffer, PoseStack poseStack,
                            float x, float y, float z, float u, float v, int light, int overlay, float alpha) {
        buffer.addVertex(poseStack.last().pose(), x, y, z)
                .setColor(0f, 1f, 1f, alpha)
                .setUv(u, v)
                .setLight(light)
                .setOverlay(overlay)
                .setNormal(1, 0, 0);
    }

    private void drawQuad(VertexConsumer builder, PoseStack poseStack,
                          float x0, float y0, float z0,
                          float x1, float y1, float z1, float u0, float v0, float u1, float v1, int light, int overlay, float alpha) {
        drawVertex(builder, poseStack, x1, y0, z1, u0, v0, light, overlay, alpha);
        drawVertex(builder, poseStack, x1, y1, z1, u0, v1, light, overlay, alpha);
        drawVertex(builder, poseStack, x0, y1, z0, u1, v1, light, overlay, alpha);
        drawVertex(builder, poseStack, x0, y0, z0, u1, v0, light, overlay, alpha);


    }

    private void drawFlatThickLine(VertexConsumer consumer, Matrix4f matrix,
                                   float x1, float y1, float z1,
                                   float x2, float y2, float z2,
                                   float width) {

        float dx = x2 - x1;
        float dz = z2 - z1;
        float length = (float) ((float) Math.sqrt(dx * dx + dz * dz));

        float halfThickness = (float) (0.25 / 2.0);
        float px = -dz / length * (halfThickness / 2f);
        float pz = dx / length * (halfThickness / 2f);

        length = length*3.2f;
        float y = y1 + 0.001f;

        float v1x = x1 + px;
        float v1z = z1 + pz;

        float v2x = x1 - px;
        float v2z = z1 - pz;

        float v3x = x2 - px;
        float v3z = z2 - pz;

        float v4x = x2 + px;
        float v4z = z2 + pz;
        consumer.addVertex(matrix, v1x, y, v1z)
                .setColor(1f, 1f, 1f, 1f)
                .setUv(0, 0) // U=0, V=0
                .setLight(0xF000F0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(0, 1, 0);

        consumer.addVertex(matrix, v2x, y, v2z)
                .setColor(1f, 1f, 1f, 1f)
                .setUv(0, 1) // U=0, V=1
                .setLight(0xF000F0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(0, 1, 0);

        consumer.addVertex(matrix, v3x, y, v3z)
                .setColor(1f, 1f, 1f, 1f)
                .setUv(length, 1) // U=Length, V=1
                .setLight(0xF000F0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(0, 1, 0);

        consumer.addVertex(matrix, v4x, y, v4z)
                .setColor(1f, 1f, 1f, 1f)
                .setUv(length, 0) // U=Length, V=0
                .setLight(0xF000F0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(0, 1, 0);
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