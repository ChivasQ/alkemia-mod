package com.ferralith.alkemia.event;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.client.JarClientItemExtensions;
import com.ferralith.alkemia.entity.renderer.JarBlockEntityRenderer;
import com.ferralith.alkemia.registries.ModBlockEntities;
import com.ferralith.alkemia.registries.ModItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
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
    }

}
