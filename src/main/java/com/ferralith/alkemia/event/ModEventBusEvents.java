package com.ferralith.alkemia.event;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.registries.ModBlockEntities;
import com.ferralith.alkemia.registries.ModItems;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class ModEventBusEvents {
    @SubscribeEvent
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.JAR_BLOCK_ENTITY.get(),
                (jarBlockEntity, context) -> jarBlockEntity.getFluidTank()
        );
        //TODO: this
//        event.registerItem(
//                Capabilities.FluidHandler.ITEM,
//                (itemStack, context) -> new FluidHandlerItemStack(itemStack, 10000),
//                ModItems.JAR_ITEM.get()
//        );
    }
}
