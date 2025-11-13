package com.ferralith.alkemia.event;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.client.RitualDrawHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = Alkemia.MODID)
public class ModNeoForgeEvents {
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        System.out.println("entity interact");
        RitualDrawHandler.handle(event);
    }

}
