package com.ferralith.alkemia.registries;

import com.ferralith.alkemia.Alkemia;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;
import java.util.function.Function;

public class ModRenderTypes {
    public static RenderType brightSolid(ResourceLocation texture)
    {
        return CustomRenderTypes.BRIGHT_SOLID.apply(texture);
    }

    @EventBusSubscriber(value = Dist.CLIENT, modid = Alkemia.MODID)
    public static class ModClientEvents
    {
        @SubscribeEvent
        public static void shaderRegistry(RegisterShadersEvent event) throws IOException
        {
            // Adds a shader to the list, the callback runs when loading is complete.
            event.registerShader(new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(Alkemia.MODID, "rendertype_bright_solid"),
                    DefaultVertexFormat.NEW_ENTITY), shaderInstance -> {
                ModRenderTypes.CustomRenderTypes.brightSolidShader = shaderInstance;
            });
        }
    }

    private static class CustomRenderTypes extends RenderType
    {
        // Holds the object loaded via RegisterShadersEvent
        private static ShaderInstance brightSolidShader;

        // Shader state for use in the render type, the supplier ensures it updates automatically with resource reloads
        private static final RenderStateShard.ShaderStateShard RENDERTYPE_BRIGHT_SOLID_SHADER = new RenderStateShard.ShaderStateShard(() -> brightSolidShader);

        // Dummy constructor needed to make java happy
        private CustomRenderTypes(String s, VertexFormat v, VertexFormat.Mode m, int i, boolean b, boolean b2, Runnable r, Runnable r2)
        {
            super(s, v, m, i, b, b2, r, r2);
            throw new IllegalStateException("This class is not meant to be constructed!");
        }

        // The memoize caches the output value for each input, meaning the expensive registration process doesn't have to rerun
        public static Function<ResourceLocation, RenderType> BRIGHT_SOLID = Util.memoize(CustomRenderTypes::brightSolid);

        // Defines the RenderType. Make sure the name is unique by including your MODID in the name.
        private static RenderType brightSolid(ResourceLocation locationIn)
        {
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_BRIGHT_SOLID_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setLightmapState(NO_LIGHTMAP)
                    .setOverlayState(NO_OVERLAY)
                    .createCompositeState(true);
            return create(genName("bright_solid"), DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state);
        }

        private static String genName(String s) {
            return Alkemia.MODID + "_" + s;
        }
    }


}
