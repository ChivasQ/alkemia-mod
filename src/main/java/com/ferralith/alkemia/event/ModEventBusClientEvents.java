package com.ferralith.alkemia.event;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.client.*;
import com.ferralith.alkemia.client.renderer.SketchingQuillSelectionRenderer;
import com.ferralith.alkemia.entity.renderer.JarBlockEntityRenderer;
import com.ferralith.alkemia.entity.renderer.chalkboard.MasterChalkboardRenderer;
import com.ferralith.alkemia.entity.renderer.chalkboard.PartChalkboardRenderer;
import com.ferralith.alkemia.entity.renderer.ritualblock.RitualMasterBlockRenderer;
import com.ferralith.alkemia.item.curios.CloakModel;
import com.ferralith.alkemia.particle.ManaParticle;
import com.ferralith.alkemia.registries.*;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.DyeColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

@EventBusSubscriber(modid = Alkemia.MODID, value = Dist.CLIENT)
public class ModEventBusClientEvents {
    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event){
        event.registerBlockEntityRenderer(
                ModBlockEntities.JAR_BLOCK_ENTITY.get(),
                JarBlockEntityRenderer::new
        );
        event.registerBlockEntityRenderer(
                ModBlockEntities.MASTER_CHALKBOARD_ENTITY.get(),
                MasterChalkboardRenderer::new
        );
        event.registerBlockEntityRenderer(
                ModBlockEntities.CHALKBOARD_PART_ENTITY.get(),
                PartChalkboardRenderer::new
        );

        event.registerBlockEntityRenderer(
                ModBlockEntities.MASTER_RITUAL_ENTITY.get(),
                RitualMasterBlockRenderer::new
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
    @SubscribeEvent
    public static void RenderLevelStageEvent(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            SketchingQuillSelectionRenderer.render(event);
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        QuillSelectionCancel.cancelIfNeeded();
        DrawHandler.handleDrawing();
    }

//    @SubscribeEvent
//    public static void onClick(InputEvent.MouseButton event) {
//
//    }


    @SubscribeEvent
    public static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event) {
        event.register(
                (itemStack, tintIndex) -> {
                    Byte colorId = itemStack.get(ModDataComponents.COLOR.get());
                    if (colorId == null) {
                        colorId = (byte) 0;
                    }

                    DyeColor dyeColor = DyeColor.byId((int) colorId);
                    return dyeColor.getTextureDiffuseColor();

                },
                ModItems.CHALK_ITEM.get()
        );
    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.CAPE_LAYER, CloakModel::createBodyLayer);
    }

}
