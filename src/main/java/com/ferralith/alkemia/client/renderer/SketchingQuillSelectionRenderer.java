package com.ferralith.alkemia.client.renderer;

import ca.weblite.objc.Client;
import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.test.ClientSelectionData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SketchingQuillSelectionRenderer {
    private static final ResourceLocation BORDER_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Alkemia.MODID, "textures/misc/border.png");

    private static RenderType getCustomBorderRenderType(ResourceLocation texture) {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(RenderType.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                .setWriteMaskState(RenderType.COLOR_DEPTH_WRITE)
                .createCompositeState(true);

        return RenderType.create("custom_border_quads",
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 1000,
                state);
    }

    public static void render(RenderLevelStageEvent event) {
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        Camera camera = event.getCamera();
        double camX = camera.getPosition().x;
        double camY = camera.getPosition().y;
        double camZ = camera.getPosition().z;

        BlockPos pos1 = ClientSelectionData.pos1;
        BlockPos pos2 = null;
        if (pos1 == null) return;
        HitResult hitResult = Minecraft.getInstance().hitResult;
        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            pos2 = ((BlockHitResult) hitResult).getBlockPos().above();
        } else {
            return;
        }

        BlockPos min = new BlockPos(
                Math.min(pos1.getX(), Math.min(pos2.getX(), Math.min(pos1.getX()+1, pos2.getX()+1))),
                Math.min(pos1.getY()-1,pos2.getY()-1),
                Math.min(pos1.getZ(), Math.min(pos2.getZ(), Math.min(pos1.getZ()+1, pos2.getZ()+1)))
        );
        BlockPos max = new BlockPos(
                Math.max(pos1.getX(), Math.max(pos2.getX(), Math.max(pos1.getX()+1, pos2.getX()+1))),
                Math.max(pos1.getY(), pos2.getY()),
                Math.max(pos1.getZ(), Math.max(pos2.getZ(), Math.max(pos1.getZ()+1, pos2.getZ()+1)))
        );

        Vec3 pos1v = new Vec3(min.getX(), min.getY(), min.getZ());
        Vec3 pos2v = new Vec3(max.getX(), max.getY(), max.getZ());
        AABB selectionBox = new AABB(pos1v, pos2v);

        poseStack.pushPose();
        poseStack.translate(-camX, -camY, -camZ);


        Matrix4f matrix = poseStack.last().pose();

        RenderType renderType = getCustomBorderRenderType(BORDER_TEXTURE);
        VertexConsumer consumer = buffer.getBuffer(RenderType.DEBUG_QUADS);

        float r = 1.0f;
        float g = 0.8f;
        float b = 0.0f;
        float a = 0.7f;
        int light = 0xF000F0;

        float thickness = 0.1f;


        // Bottom XZ plane
        drawTexturedLine(matrix, consumer, (float)selectionBox.minX, (float)selectionBox.minY, (float)selectionBox.minZ, (float)selectionBox.maxX, (float)selectionBox.minY, (float)selectionBox.minZ, thickness, r, g, b, a, light);
        drawTexturedLine(matrix, consumer, (float)selectionBox.minX, (float)selectionBox.minY, (float)selectionBox.maxZ, (float)selectionBox.maxX, (float)selectionBox.minY, (float)selectionBox.maxZ, thickness, r, g, b, a, light);
        drawTexturedLine(matrix, consumer, (float)selectionBox.minX, (float)selectionBox.minY, (float)selectionBox.minZ, (float)selectionBox.minX, (float)selectionBox.minY, (float)selectionBox.maxZ, thickness, r, g, b, a, light);
        drawTexturedLine(matrix, consumer, (float)selectionBox.maxX, (float)selectionBox.minY, (float)selectionBox.minZ, (float)selectionBox.maxX, (float)selectionBox.minY, (float)selectionBox.maxZ, thickness, r, g, b, a, light);

        // Top XZ plane
        drawTexturedLine(matrix, consumer, (float)selectionBox.minX, (float)selectionBox.maxY, (float)selectionBox.minZ, (float)selectionBox.maxX, (float)selectionBox.maxY, (float)selectionBox.minZ, thickness, r, g, b, a, light);
        drawTexturedLine(matrix, consumer, (float)selectionBox.minX, (float)selectionBox.maxY, (float)selectionBox.maxZ, (float)selectionBox.maxX, (float)selectionBox.maxY, (float)selectionBox.maxZ, thickness, r, g, b, a, light);
        drawTexturedLine(matrix, consumer, (float)selectionBox.minX, (float)selectionBox.maxY, (float)selectionBox.minZ, (float)selectionBox.minX, (float)selectionBox.maxY, (float)selectionBox.maxZ, thickness, r, g, b, a, light);
        drawTexturedLine(matrix, consumer, (float)selectionBox.maxX, (float)selectionBox.maxY, (float)selectionBox.minZ, (float)selectionBox.maxX, (float)selectionBox.maxY, (float)selectionBox.maxZ, thickness, r, g, b, a, light);

        // Vertical edges
        drawTexturedLine(matrix, consumer, (float)selectionBox.minX, (float)selectionBox.minY, (float)selectionBox.minZ, (float)selectionBox.minX, (float)selectionBox.maxY, (float)selectionBox.minZ, thickness, r, g, b, a, light);
        drawTexturedLine(matrix, consumer, (float)selectionBox.maxX, (float)selectionBox.minY, (float)selectionBox.minZ, (float)selectionBox.maxX, (float)selectionBox.maxY, (float)selectionBox.minZ, thickness, r, g, b, a, light);
        drawTexturedLine(matrix, consumer, (float)selectionBox.minX, (float)selectionBox.minY, (float)selectionBox.maxZ, (float)selectionBox.minX, (float)selectionBox.maxY, (float)selectionBox.maxZ, thickness, r, g, b, a, light);
        drawTexturedLine(matrix, consumer, (float)selectionBox.maxX, (float)selectionBox.minY, (float)selectionBox.maxZ, (float)selectionBox.maxX, (float)selectionBox.maxY, (float)selectionBox.maxZ, thickness, r, g, b, a, light);

        poseStack.popPose();
        poseStack.popPose();
    }

    private static void drawTexturedLine(Matrix4f matrix, VertexConsumer consumer,
                                         float x1, float y1, float z1,
                                         float x2, float y2, float z2,
                                         float thickness,
                                         float r, float g, float b, float a, int light) {

        Vector3f p1 = new Vector3f(x1, y1, z1);
        Vector3f p2 = new Vector3f(x2, y2, z2);

        Vector3f lineDir = new Vector3f(p2).sub(p1).normalize();

        Vector3f perpendicular = new Vector3f();


        if (Math.abs(lineDir.y) < 0.001f) {
            perpendicular.set(-lineDir.z, 0, lineDir.x).normalize();
        }
        else if (Math.abs(lineDir.x) < 0.001f && Math.abs(lineDir.z) < 0.001f) {
            perpendicular.set(1, 0, 0);
        }

        else {

            perpendicular.set(lineDir).cross(0, 1, 0).normalize();

            if (perpendicular.lengthSquared() < 0.001f) {
                perpendicular.set(1, 0, 0);
            }
        }

        perpendicular.mul(thickness / 2.0f);

        Vector3f v1 = new Vector3f(p1).sub(perpendicular);
        Vector3f v2 = new Vector3f(p1).add(perpendicular);
        Vector3f v3 = new Vector3f(p2).add(perpendicular);
        Vector3f v4 = new Vector3f(p2).sub(perpendicular);

        float u0 = 0.0f;
        float u1 = 1.0f;
        float v0 = 0.0f;
        float v11 = 1.0f;

        consumer.addVertex(matrix, v1.x, v1.y, v1.z).setColor(r, g, b, a).setUv(u0, v0).setLight(light);
        consumer.addVertex(matrix, v2.x, v2.y, v2.z).setColor(r, g, b, a).setUv(u0, v11).setLight(light);
        consumer.addVertex(matrix, v3.x, v3.y, v3.z).setColor(r, g, b, a).setUv(u1, v11).setLight(light);
        consumer.addVertex(matrix, v4.x, v4.y, v4.z).setColor(r, g, b, a).setUv(u1, v0).setLight(light);
    }

    private static void drawVertex(VertexConsumer buffer, PoseStack poseStack,
                                   float x, float y, float z,
                                   int color) {
        buffer.addVertex(poseStack.last().pose(), x, y, z)
                .setColor(1f,1f,1f,1f)
                .setNormal(1, 0, 0);
    }

    private static void drawQuad(VertexConsumer builder, PoseStack poseStack,
                                 float x0, float y0, float z0,
                                 float x1, float y1, float z1,
                                 int color) {
        drawVertex(builder, poseStack, x0, y0, z0, color);
        drawVertex(builder, poseStack, x0, y1, z1, color);
        drawVertex(builder, poseStack, x1, y1, z1, color);
        drawVertex(builder, poseStack, x1, y0, z0, color);
        drawVertex(builder, poseStack, x0, y0, z0, color);


    }
}
