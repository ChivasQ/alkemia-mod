package com.ferralith.alkemia.item.renderer;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.registries.ModBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class JarItemRenderer extends BlockEntityWithoutLevelRenderer {
    public JarItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BlockState defaultState = ModBlocks.JAR_BLOCK.get().defaultBlockState();
        BakedModel blockModel = Minecraft.getInstance().getModelManager()
                .getBlockModelShaper().getBlockModel(defaultState);

        // TODO: render fluid overlay

        IFluidHandlerItem fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);

        FluidStack fluidStack = FluidStack.EMPTY;
        if (fluidHandler != null) {
            fluidStack = fluidHandler.getFluidInTank(0);
        }

        if (fluidStack.isEmpty()) {
            renderJar(poseStack, buffer, packedLight, packedOverlay, defaultState, blockModel);
            return;
        }


        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluidType());
        ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture();

        if (stillTexture == null) {
            return;
        }

        FluidState fluidState = fluidStack.getFluid().defaultFluidState();

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
        int tintColor = fluidTypeExtensions.getTintColor(fluidStack);

        VertexConsumer builder = buffer.getBuffer(ItemBlockRenderTypes.getRenderLayer(fluidState));

        float f = (float) fluidHandler.getFluidInTank(0).getAmount() /  (float) fluidHandler.getTankCapacity(0);

        float x0 = 0.25f;
        float y0 = 0.0625f;
        float z0 = 0.25f;
        float x1 = 0.75f;
        float y1 = 0.75f;
        float fluidHeight = (y0 + (y1 - y0) * f);

        float z1 = 0.75f;
        //top
        drawQuad(builder, poseStack, x0, fluidHeight, z0, x1, fluidHeight, z1, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(), packedLight, packedOverlay, tintColor);

        //sides
        drawQuad(builder, poseStack, x0, y0, z0, x1, fluidHeight, z0, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV(f), packedLight, packedOverlay, tintColor);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180));
        poseStack.translate(-1f, 0, -1.5f);
        drawQuad(builder, poseStack, x0, y0, z1, x1, fluidHeight, z1, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV(f), packedLight, packedOverlay, tintColor);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.translate(-1f, 0, 0);
        drawQuad(builder, poseStack, x0, y0, z0, x1, fluidHeight, z0, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV(f), packedLight, packedOverlay, tintColor);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.YN.rotationDegrees(90));
        poseStack.translate(0, 0, -1f);
        drawQuad(builder, poseStack, x0, y0, z0, x1, fluidHeight, z0, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV(f), packedLight, packedOverlay, tintColor);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.translate(0, -y0*2, -1f);
        drawQuad(builder, poseStack, x0, y0, z0, x1, y0, z1, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(), packedLight, packedOverlay, tintColor);
        poseStack.popPose();

        renderJar(poseStack, buffer, packedLight, packedOverlay, defaultState, blockModel);
    }

    private static void renderJar(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BlockState defaultState, BakedModel blockModel) {
        Minecraft.getInstance().getBlockRenderer().getModelRenderer()
                .renderModel(
                        poseStack.last(),
                        buffer.getBuffer(RenderType.translucent()),
                        defaultState,
                        blockModel,
                        1.0f, 1.0f, 1.0f,
                        packedLight,
                        packedOverlay
                );
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