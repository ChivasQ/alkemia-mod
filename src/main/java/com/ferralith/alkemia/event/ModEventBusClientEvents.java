package com.ferralith.alkemia.event;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.client.JarClientItemExtensions;
import com.ferralith.alkemia.client.ManaClientFluidTypeExtensions;
import com.ferralith.alkemia.entity.renderer.JarBlockEntityRenderer;
import com.ferralith.alkemia.particle.ManaParticle;
import com.ferralith.alkemia.registries.*;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = Alkemia.MODID, value = Dist.CLIENT)
public class ModEventBusClientEvents {
    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event){
        event.registerBlockEntityRenderer(
                ModBlockEntities.JAR_BLOCK_ENTITY.get(),
                JarBlockEntityRenderer::new
        );
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(
                new JarClientItemExtensions(),
                ModItems.JAR_ITEM
        );

        event.registerFluidType(
                new ManaClientFluidTypeExtensions(),
                ModFluids.MANA_TYPE.get()
        );
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_MANA.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_MANA.get(), RenderType.translucent());
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.MANA_PARTICLE.get(), ManaParticle.Provider::new);
    }

}
