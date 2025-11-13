package com.ferralith.alkemia.entity.ritualblock;

import com.ferralith.alkemia.registries.ModAttachments;
import com.ferralith.alkemia.registries.ModBlockEntities;
import com.ferralith.alkemia.ritual.RitualFigures;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

import static com.ferralith.alkemia.block.RitualBaseBlock.*;


public class RitualMasterBlockEntity extends BlockEntity {
    private final RitualFigures graph;
    private int radius = 5;
    @Nullable
    private UUID ritualID;

    public RitualMasterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MASTER_RITUAL_ENTITY.get(), pos, blockState);
        this.graph = new RitualFigures(pos.getBottomCenter().add(0,1,0), radius);
    }

    public void setRitualID(UUID ritualID) {
        this.ritualID = ritualID;
        setChanged();
    }

    @Nullable
    public UUID getRitualID() {
        return this.ritualID;
    }

    public void tick() {
        if (Minecraft.getInstance().level == null) return;

//        for (var node : graph.getNodes()) {
//            Minecraft.getInstance().level.addParticle(ParticleTypes.FLAME,
//                    node.x + this.worldPosition.getX() + 0.5,
//                    node.y + this.worldPosition.getY()  + 1,
//                    node.z + this.worldPosition.getZ()  + 0.5, 0, 0.2, 0);
//        }
    }


    public void onNodeClicked(Player player, int nodeIndex) {
        player.sendSystemMessage(Component.literal("node #" + nodeIndex));
        PlayerSelection cap = player.getData(ModAttachments.PLAYER_DATA_SELECTION);

        if (cap == null) return;

        BlockPos currentBlockPos = this.worldPosition;
        BlockPos storedBlockPos = cap.getActiveRitualBlockPos();
        System.out.println(storedBlockPos);
        if (storedBlockPos == null || !storedBlockPos.equals(currentBlockPos)) {

            cap.setActiveRitualBlockPos(currentBlockPos);
            cap.setFirstSelectedNodeIndex(nodeIndex);

            player.sendSystemMessage(Component.literal("selected node #" + nodeIndex));

        } else {
            int firstIndex = cap.getFirstSelectedNodeIndex();
            int secondIndex = nodeIndex;

            cap.setActiveRitualBlockPos(null);
            cap.setFirstSelectedNodeIndex(null);

            if (firstIndex == secondIndex) {
                player.sendSystemMessage(Component.literal("canceled"));
                return;
            }
            int i = graph.getNodes().size();
            player.sendSystemMessage(Component.literal("join #" + firstIndex + " and #" + secondIndex));
            Vec3 centerPos = graph.blockPos;
            Vec3 newNode = graph.connectNodes(firstIndex, secondIndex);

            Interaction interaction = new Interaction(EntityType.INTERACTION, level);
            interaction.setCustomNameVisible(true);

            interaction.setPos(centerPos.x + newNode.x, centerPos.y + newNode.y, centerPos.z + newNode.z);
            System.out.println(interaction.getBoundingBox());
            System.out.println(interaction.getPosition(1));
            interaction.setBoundingBox(interaction.getBoundingBox().inflate(0.5));
            interaction.setCustomName(Component.literal("node" + i));

            interaction.getPersistentData().putUUID(RITUAL_MASTER_TAG, ritualID);
            interaction.getPersistentData().putInt(RITUAL_NODE_INDEX_TAG, i);
            interaction.getPersistentData().putLong(RITUAL_MATER_POS, this.worldPosition.asLong());

            level.addFreshEntity(interaction);


            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        if (this.ritualID != null) {
            nbt.putUUID("ritual_id", this.ritualID);
        }
        nbt.put("graph_data", graph.serializeNBT(registries));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag nbt = super.getUpdateTag(registries);

        nbt.put("graph_data", graph.serializeNBT(registries));
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt, HolderLookup.Provider registries) {

        super.handleUpdateTag(nbt, registries);

        if (nbt.contains("graph_data", Tag.TAG_COMPOUND)) {
            graph.deserializeNBT(registries, nbt.getCompound("graph_data"));
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        if (nbt.hasUUID("ritual_id")) {
            this.ritualID = nbt.getUUID("ritual_id");
        }
        if (nbt.contains("graph_data", Tag.TAG_COMPOUND)) {
            graph.deserializeNBT(registries, nbt.getCompound("graph_data"));
        }

    }

    public RitualFigures getGraph() {
        return graph;
    }

    public int getRadius() {
        return radius;
    }
}
