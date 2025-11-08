package com.ferralith.alkemia.entity.renderer;

import com.ferralith.alkemia.entity.chalkboard.MasterChalkboardEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.joml.Vector2i;

import java.util.Map;

public class MasterChalkboardRenderer extends BaseChalkboardRenderer<MasterChalkboardEntity> {
    public MasterChalkboardRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(MasterChalkboardEntity masterBE, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight, int combinedOverlay) {
        double angle = Minecraft.getInstance().level.getGameTime();
        masterBE.tickClient();

        byte[][] pixels = masterBE.getBlockPixels(masterBE.getBlockPos());

        renderFace(poseStack, multiBufferSource, pixels, 0, combinedLight, combinedOverlay);

    }
}
