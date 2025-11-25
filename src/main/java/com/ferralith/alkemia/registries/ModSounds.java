package com.ferralith.alkemia.registries;

import com.ferralith.alkemia.Alkemia;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENT =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Alkemia.MODID);

    public static final Supplier<SoundEvent> RITUAL_ACTIVATED = registerSoundEvent("zap");




    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Alkemia.MODID, name);
        return SOUND_EVENT.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENT.register(eventBus);
    }
}
