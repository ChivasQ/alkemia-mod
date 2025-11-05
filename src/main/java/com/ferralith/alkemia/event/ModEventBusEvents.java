package com.ferralith.alkemia.event;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.network.data.ChalkboardPixelsData;
import com.ferralith.alkemia.network.handler.C2S_ChalkboardPixelsDataPayloadHandler;
import com.ferralith.alkemia.network.handler.S2C_ChalkboardPixelsDataPayloadHandler;
import com.ferralith.alkemia.registries.ModBlockEntities;
import com.ferralith.alkemia.registries.ModDataComponents;
import com.ferralith.alkemia.registries.ModItems;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModEventBusEvents {
    @SubscribeEvent
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.JAR_BLOCK_ENTITY.get(),
                (jarBlockEntity, context) -> jarBlockEntity.getFluidTank()
        );
        //TODO: this
        event.registerItem(
                Capabilities.FluidHandler.ITEM,
                (itemStack, context) -> new FluidHandlerItemStack(
                        ModDataComponents.FLUID_CONTENT,
                        itemStack,
                        10000
                ),
                ModItems.JAR_ITEM.get()
        );
    }

    @SubscribeEvent
    public void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Alkemia.MODID);
        registrar.playBidirectional(
                ChalkboardPixelsData.TYPE,
                ChalkboardPixelsData.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        S2C_ChalkboardPixelsDataPayloadHandler::handleDataOnNetwork,
                        C2S_ChalkboardPixelsDataPayloadHandler::handleDataOnNetwork
                )
        );
    }
}
