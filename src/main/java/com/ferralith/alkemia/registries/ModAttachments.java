package com.ferralith.alkemia.registries;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.client.PlayerSelection;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Alkemia.MODID);

    public static final Supplier<AttachmentType<PlayerSelection>> PLAYER_DATA_SELECTION =
            ATTACHMENT_TYPES.register("player_selection", () -> AttachmentType.builder(() -> new PlayerSelection())
                    .build());

    public static void register(IEventBus event) {
        ATTACHMENT_TYPES.register(event);
    }
}
