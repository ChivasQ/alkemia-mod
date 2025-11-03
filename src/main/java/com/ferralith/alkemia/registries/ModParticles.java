package com.ferralith.alkemia.registries;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.fluid.ManaFluid;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Alkemia.MODID);

    public static final Supplier<SimpleParticleType> MANA_PARTICLE = PARTICLE_TYPES.register("mana_particle", () -> new SimpleParticleType(true));



    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
