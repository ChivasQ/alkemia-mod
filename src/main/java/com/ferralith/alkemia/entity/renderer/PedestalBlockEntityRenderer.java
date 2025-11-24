package com.ferralith.alkemia.entity.renderer;

import com.ferralith.alkemia.entity.PedestalBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;

public class PedestalBlockEntityRenderer implements BlockEntityRenderer<PedestalBlockEntity> {
    public PedestalBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(PedestalBlockEntity entity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack stack = entity.inventory.getStackInSlot(0);
        poseStack.pushPose();
        float rotation = entity.getRenderingRotation(v);

        poseStack.translate(0.5,1.35 + Math.sin(Math.toRadians(rotation))/10, 0.5);

        if (stack.getItem() instanceof BlockItem) {
            poseStack.scale(1, 1, 1);
        } else {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        }


        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.mulPose(Axis.XP.rotationDegrees(rotation));

        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, getLightLevel(entity.getLevel(),
                entity.getBlockPos()), OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, entity.getLevel(), 1);

        poseStack.popPose();
    }

    private int getLightLevel(Level level, BlockPos blockPos) {
        int blight = level.getBrightness(LightLayer.BLOCK, blockPos);
        int slight = level.getBrightness(LightLayer.SKY, blockPos);
        return LightTexture.pack(blight, slight);
    }


}
