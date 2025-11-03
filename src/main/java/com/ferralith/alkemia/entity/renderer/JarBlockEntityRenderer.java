package com.ferralith.alkemia.entity.renderer;

import com.ferralith.alkemia.entity.JarBlockEntity;
import com.ferralith.alkemia.registries.ModFluids;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class JarBlockEntityRenderer implements BlockEntityRenderer<JarBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public JarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(JarBlockEntity jarBlockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight, int combinedOverlay) {
        FluidStack fluidStack = jarBlockEntity.getFluidTank().getFluid();

        if (jarBlockEntity.getFluidTank().getFluidAmount() == 0) {
            return;
        }
        
        Level level = jarBlockEntity.getLevel();
        if (level == null) {
            System.out.println(fluidStack + " " + fluidStack.getFluidType());
            return;
        }
        BlockPos pos = jarBlockEntity.getBlockPos();

        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluidType());
        ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture();

        if (stillTexture == null) {
            System.out.println(fluidStack + " " + fluidStack.getFluidType() + " " + stillTexture.toString());
            return;
        }

        FluidState fluidState = fluidStack.getFluid().defaultFluidState();

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
        int tintColor = fluidTypeExtensions.getTintColor(fluidState, level, pos);

        VertexConsumer builder = multiBufferSource.getBuffer(ItemBlockRenderTypes.getRenderLayer(fluidState));

        float f = (float) jarBlockEntity.getFluidTank().getFluidAmount() /  (float) jarBlockEntity.getFluidTank().getCapacity();

        float x0 = 0.25f;
        float y0 = 0.0625f;
        float z0 = 0.25f;
        float x1 = 0.75f;
        float y1 = 0.75f;
        float fluidHeight = y0 + (y1 - y0) * f;

        float z1 = 0.75f;
        //top
        drawQuad(builder, poseStack, x0, fluidHeight, z0, x1, fluidHeight, z1, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(), combinedLight, combinedOverlay, tintColor);

        //sides
        drawQuad(builder, poseStack, x0, y0, z0, x1, fluidHeight, z0, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV(f), combinedLight, combinedOverlay, tintColor);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180));
        poseStack.translate(-1f, 0, -1.5f);
        drawQuad(builder, poseStack, x0, y0, z1, x1, fluidHeight, z1, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV(f), combinedLight, combinedOverlay, tintColor);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(-1f, 0, 0);
        drawQuad(builder, poseStack, x0, y0, z0, x1, fluidHeight, z0, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV(f), combinedLight, combinedOverlay, tintColor);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.YN.rotationDegrees(90));
        poseStack.translate(0, 0, -1f);
        drawQuad(builder, poseStack, x0, y0, z0, x1, fluidHeight, z0, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV(f), combinedLight, combinedOverlay, tintColor);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.translate(0, -y0*2, -1f);
        drawQuad(builder, poseStack, x0, y0, z0, x1, y0, z1, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(), combinedLight, combinedOverlay, tintColor);
        poseStack.popPose();
    }

    private void drawVertex(VertexConsumer buffer, PoseStack poseStack,
                           float x, float y, float z,
                           float u, float v,
                           int light, int overlay, int color) {
        buffer.addVertex(poseStack.last().pose(), x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setLight(light)
                .setOverlay(overlay)
                .setNormal(1, 0, 0);
    }

    private void drawQuad(VertexConsumer builder, PoseStack poseStack,
                          float x0, float y0, float z0,
                          float x1, float y1, float z1,
                          float u0, float v0,
                          float u1, float v1,
                          int light, int overlay, int color) {
        drawVertex(builder, poseStack, x0, y0, z0, u0, v0, light, overlay, color);
        drawVertex(builder, poseStack, x0, y1, z1, u0, v1, light, overlay, color);
        drawVertex(builder, poseStack, x1, y1, z1, u1, v1, light, overlay, color);
        drawVertex(builder, poseStack, x1, y0, z0, u1, v0, light, overlay, color);


    }
}
