package com.ferralith.alkemia.registries;

import com.ferralith.alkemia.Alkemia;
import com.mojang.serialization.Codec;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.awt.*;
import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Alkemia.MODID);

    public static final Supplier<DataComponentType<SimpleFluidContent>> FLUID_CONTENT =
            DATA_COMPONENT_TYPES.register(
                    "fluid_content",
                    () -> DataComponentType.<SimpleFluidContent>builder()
                            .persistent(SimpleFluidContent.CODEC)
                            .networkSynchronized(SimpleFluidContent.STREAM_CODEC)
                            .build()
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Byte>> COLOR = DATA_COMPONENT_TYPES.register(
            "color",
            () -> DataComponentType.<Byte>builder()
                    .persistent(Codec.BYTE)
                    .networkSynchronized(ByteBufCodecs.BYTE)
                    .build()
    );

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
