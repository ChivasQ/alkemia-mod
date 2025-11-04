package com.ferralith.alkemia.client;

import com.ferralith.alkemia.registries.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class QuillSelectionCancel {
    public static void cancelIfNeeded() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.isPaused()) {
            return;
        }

        if (ClientSelectionData.pos1 == null) {
            return;
        }

        Player player = mc.player;
        boolean isHoldingQuill = player.getMainHandItem().is(ModItems.SKETCHING_QUILL.get()) ||
                player.getOffhandItem().is(ModItems.SKETCHING_QUILL.get());

        if (!isHoldingQuill) {
            ClientSelectionData.pos1 = null;

            player.displayClientMessage(
                    Component.translatable("message.alkemia.selection_cancelled"),
                    true
            );
        }
    }
}
