package com.ferralith.alkemia.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Matrix4f;
import org.joml.Vector2i;

import java.util.Map;

public abstract class BaseChalkboardRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {

    protected BaseChalkboardRenderer(BlockEntityRendererProvider.Context context) {
    }

    protected void renderFace(PoseStack poseStack, MultiBufferSource buffer, byte[][] pixels, float angle, int light, int overlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.01, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.debugQuads());
        float pixelSize = 1.0f / 16.0f;
        Matrix4f matrix = poseStack.last().pose();

        for (int x_local = 0; x_local < 16; x_local++) {
            for (int z_local = 0; z_local < 16; z_local++) {

                byte color = pixels[x_local][z_local];
                if (color != (byte) 0) {
                    float minX = x_local * pixelSize - 0.5f;
                    float minZ = z_local * pixelSize - 0.5f;
                    float maxX = minX + pixelSize;
                    float maxZ = minZ + pixelSize;

                    vertexConsumer.addVertex(matrix, minX, 0, minZ).setColor(1f, 1f, 1f, 1f).setLight(light).setOverlay(overlay);
                    vertexConsumer.addVertex(matrix, maxX, 0, minZ).setColor(1f, 1f, 1f, 1f).setLight(light).setOverlay(overlay);
                    vertexConsumer.addVertex(matrix, maxX, 0, maxZ).setColor(1f, 1f, 1f, 1f).setLight(light).setOverlay(overlay);
                    vertexConsumer.addVertex(matrix, minX, 0, maxZ).setColor(1f, 1f, 1f, 1f).setLight(light).setOverlay(overlay);
                }
            }
        }

        poseStack.popPose();
    }
}
