package com.ferralith.alkemia.registries;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.fluid.ManaFluid;
import com.ferralith.alkemia.fluid.ManaFluidType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, Alkemia.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, Alkemia.MODID);

    public static final DeferredHolder<Fluid, ManaFluid.Flowing> FLOWING_MANA = FLUIDS.register(
            "flowing_mana",
            ManaFluid.Flowing::new
    );

    public static final DeferredHolder<Fluid, ManaFluid.Source> SOURCE_MANA = FLUIDS.register(
            "source_mana",
            ManaFluid.Source::new
    );

    public static final DeferredHolder<FluidType, ManaFluidType> MANA_TYPE = FLUID_TYPES.register(
            "mana",
            ManaFluidType::new
    );

    private void registerFluid(String name) {

    }

    public static void register(IEventBus bus) {
        ModFluids.FLUIDS.register(bus);
        ModFluids.FLUID_TYPES.register(bus);
    }
}
