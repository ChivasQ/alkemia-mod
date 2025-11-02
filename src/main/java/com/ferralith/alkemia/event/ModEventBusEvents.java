package com.ferralith.alkemia.event;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.registries.ModBlockEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class ModEventBusEvents {
    @SubscribeEvent
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.JAR_BLOCK_ENTITY.get(),
                (jarBlockEntity, context) -> jarBlockEntity.getFluidTank()
        );
    }
}
