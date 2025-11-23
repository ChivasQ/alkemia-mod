package com.ferralith.alkemia.event;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.client.RitualDrawHandler;
import com.ferralith.alkemia.network.data.ChalkboardPixelsData;
import com.ferralith.alkemia.network.handler.C2S_ChalkboardPixelsDataPayloadHandler;
import com.ferralith.alkemia.network.handler.S2C_ChalkboardPixelsDataPayloadHandler;
import com.ferralith.alkemia.registries.ModBlockEntities;
import com.ferralith.alkemia.registries.ModCreativeTab;
import com.ferralith.alkemia.registries.ModDataComponents;
import com.ferralith.alkemia.registries.ModItems;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

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
        event.registerItem(
                CuriosCapability.ITEM,
                (stack, context) -> new ICurio() {
                    @Override
                    public ItemStack getStack() {
                        return stack;
                    }
                },
                ModItems.CLOAK_ITEM
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

    @SubscribeEvent
    public void addAllDyes(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModCreativeTab.EXAMPLE_TAB.get()) {
            for (DyeColor color : DyeColor.values()) {
                ItemStack coloredChalk = ModItems.CHALK_ITEM.get().getDefaultInstance();
                coloredChalk.set(ModDataComponents.COLOR, (byte) color.getId());

                event.accept(coloredChalk);
            }
        }
    }

}
