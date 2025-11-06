package com.ferralith.alkemia.entity.renderer;

import com.ferralith.alkemia.entity.chalkboard.ChalkboardPartEntity;
import com.ferralith.alkemia.entity.chalkboard.MasterChalkboardEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class PartChalkboardRenderer extends BaseChalkboardRenderer<ChalkboardPartEntity>{
    public PartChalkboardRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ChalkboardPartEntity chalkboardPartEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1) {
        double angle = Minecraft.getInstance().level.getGameTime();
        MasterChalkboardEntity masterBE = chalkboardPartEntity.getMaster(Minecraft.getInstance().level);
        //System.out.println(masterBE);
        if (masterBE != null) {
            masterBE.tickClient();

            byte[][] pixels = masterBE.getBlockPixels(chalkboardPartEntity.getBlockPos());
            renderFace(poseStack, multiBufferSource, pixels, 0, i, i1);
        }
    }
}
