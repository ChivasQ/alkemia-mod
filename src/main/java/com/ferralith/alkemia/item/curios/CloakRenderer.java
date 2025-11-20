package com.ferralith.alkemia.item.curios;

import com.ferralith.alkemia.client.ModModelLayers;
import com.ferralith.alkemia.client.ModTextures;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class CloakRenderer implements ICurioRenderer {
    private CloakModel model;

    public CloakRenderer() {
        this.model = new CloakModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModModelLayers.CAPE_LAYER));
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack,
                                                                          SlotContext slotContext,
                                                                          PoseStack poseStack,
                                                                          RenderLayerParent<T, M> renderLayerParent,
                                                                          MultiBufferSource buffer,
                                                                          int light, float limbSwing,
                                                                          float limbSwingAmount, float partialTicks,
                                                                          float ageInTicks, float netHeadYaw,
                                                                          float headPitch) {
        poseStack.pushPose();
//        poseStack.translate(0D, 1D, 0D);
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(ModTextures.CAPE_TEXTURE));

        ICurioRenderer.followBodyRotations(slotContext.entity(), this.model);
        this.model.renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }


}
