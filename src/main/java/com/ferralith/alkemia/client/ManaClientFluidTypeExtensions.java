package com.ferralith.alkemia.client;

import com.ferralith.alkemia.Alkemia;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

public class ManaClientFluidTypeExtensions implements IClientFluidTypeExtensions {
        final ResourceLocation MANA_STILL = ResourceLocation.fromNamespaceAndPath(Alkemia.MODID,"block/mana_still");
        final ResourceLocation MANA_FLOW = ResourceLocation.fromNamespaceAndPath(Alkemia.MODID,"block/mana_flow");

        @Override
        public ResourceLocation getStillTexture() {
            return MANA_STILL;
        }

        @Override
        public ResourceLocation getFlowingTexture() {
            return MANA_FLOW;
        }

}
