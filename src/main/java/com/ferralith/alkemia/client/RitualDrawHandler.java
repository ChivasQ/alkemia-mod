package com.ferralith.alkemia.client;

import com.ferralith.alkemia.block.RitualBaseBlock;
import com.ferralith.alkemia.entity.ritualblock.RitualMasterBlockEntity;
import com.ferralith.alkemia.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import oshi.util.platform.unix.openbsd.FstatUtil;

import java.util.UUID;

public class RitualDrawHandler {
    public static void handle(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (level.isClientSide()) {
            return;
        }
        if (player.getMainHandItem().is(ModItems.CHALK_ITEM)) {
            if (event.getTarget() instanceof Interaction interaction) {

                CompoundTag nbt = interaction.getPersistentData();

                if (nbt.contains(RitualBaseBlock.RITUAL_MASTER_TAG)) {

                    UUID ritualID = nbt.getUUID(RitualBaseBlock.RITUAL_MASTER_TAG);
                    int nodeIndex = nbt.getInt(RitualBaseBlock.RITUAL_NODE_INDEX_TAG);

                    long posLong = nbt.getLong(RitualBaseBlock.RITUAL_MATER_POS);
                    BlockPos masterPos = BlockPos.of(posLong);

                    BlockEntity be = level.getBlockEntity(masterPos);
//                System.out.println(masterPos);
                    if (be instanceof RitualMasterBlockEntity master) {

                        master.onNodeClicked(player, nodeIndex);
                        event.setCancellationResult(InteractionResult.SUCCESS);
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
