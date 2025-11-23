package com.ferralith.alkemia.entity.ritualblock;

import com.ferralith.alkemia.registries.ModAttachments;
import com.ferralith.alkemia.registries.ModBlockEntities;
import com.ferralith.alkemia.ritual.*;
import com.ferralith.alkemia.ritual.data.RitualJsonScraper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
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
import org.joml.Vector2i;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.ferralith.alkemia.block.RitualBaseBlock.*;


public class RitualMasterBlockEntity extends BlockEntity {
    private final RitualFigures graph;
    private int radius = 5;
    private boolean isActive = false;
    private int progress;
    private int cooktime = 0;
    @Nullable
    private UUID ritualID;

    public RitualMasterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MASTER_RITUAL_ENTITY.get(), pos, blockState);
        this.graph = new RitualFigures(pos.getBottomCenter().add(0,1,0), radius);
        this.progress = 0;
    }

    public void setRitualID(UUID ritualID) {
        this.ritualID = ritualID;
        setChanged();
    }

    @Nullable
    public UUID getRitualID() {
        return this.ritualID;
    }

    public boolean isActive() {
        return isActive;
    }

    public int getProgress() {
        return progress;
    }

    public int getCooktime() {
        return cooktime;
    }

    public void tick() {
        if (this.getLevel() == null) return;

        if (!level.isClientSide()) {
            if (isActive) {
                progress++;
                if (progress > 100) {
                    cooktime++;
                    progress--;
                    if (cooktime > 200) {
                        isActive = false;
                        cooktime = 0;
                    }
                }

                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //FIXME: BAD SOLUTION
            } else {
                progress = Math.max(0, progress-5);
                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }


    }




    private static final List<RitualRecipe> RECIPES = Arrays.asList(
            new SimpleRitualRecipe(Arrays.asList(
                    new Vector2i(0, 4),
                    new Vector2i(4, 8),
                    new Vector2i(8, 0),
                    new Vector2i(2, 6),
                    new Vector2i(6, 10),
                    new Vector2i(10, 2)
            ), "Star Ritual"),

            new SimpleRitualRecipe(Arrays.asList(
                    new Vector2i(0, 3),
                    new Vector2i(3, 6),
                    new Vector2i(6, 9),
                    new Vector2i(9, 0)
            ), "Multiblock structure"),

            new NestedCirclesRecipe(Arrays.asList(12, 6), "Inner Circle (6 nodes)"),
            new NestedCirclesRecipe(Arrays.asList(6, 3), "Nested Circles (6+3)")
    );

    public void checkForRitual() {
        if (level.isClientSide()) return;

        RitualJsonScraper.scrapFileNames(this.level.getServer().getResourceManager());

        if (!isActive) {
            RitualFigures ritualFigures = RitualJsonScraper.loadRitualFromResources("/data/alkemia/ritual/recipe/huh.json");

            if (RitualRecipeMatcher.match(this.graph, ritualFigures)) {
                Minecraft.getInstance().player.sendSystemMessage(
                        Component.literal("RITUAL ACTIVATED: !!")
                );
                isActive = true;
                return;
            }

            for (RitualRecipe recipe : RECIPES) {

                if (recipe.matches(this.graph)) {
                    Minecraft.getInstance().player.sendSystemMessage(
                            Component.literal("RITUAL ACTIVATED: " + recipe.getName())
                    );
                    isActive = true;
                    return;
                }
            }

            Minecraft.getInstance().player.sendSystemMessage(Component.literal("No valid ritual found"));
        }
    }


    public void onNodeClicked(Player player, int nodeIndex) {
        player.sendSystemMessage(Component.literal("node #" + nodeIndex));
        PlayerSelection cap = player.getData(ModAttachments.PLAYER_DATA_SELECTION);

        if (cap == null) return;

        BlockPos currentBlockPos = this.worldPosition;
        BlockPos storedBlockPos = cap.getActiveRitualBlockPos();
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
        nbt.putInt("cooktime", this.cooktime);
        nbt.putInt("progress", this.progress);
        nbt.putBoolean("active", this.isActive);

        if (this.ritualID != null) {
            nbt.putUUID("ritual_id", this.ritualID);
        }
        CompoundTag tag = graph.serializeNBT(registries);
        nbt.put("graph_data", tag);
        saveNbtToFile(tag, "data.json");
    }

    public static void saveNbtToFile(CompoundTag nbtData, String fileName) {
        File outputFile = new File(fileName);
        String snbtString = nbtData.toString();
        try (FileWriter writer = new FileWriter(outputFile)){

            writer.write(snbtString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag nbt = super.getUpdateTag(registries);
        nbt.putInt("cooktime", this.cooktime);
        nbt.putInt("progress", this.progress);
        nbt.putBoolean("active", this.isActive);
        nbt.put("graph_data", graph.serializeNBT(registries));
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt, HolderLookup.Provider registries) {

        super.handleUpdateTag(nbt, registries);
        if (nbt.contains("cooktime")) {
            this.cooktime = nbt.getInt("cooktime");
        }

        if (nbt.contains("progress")) {
            this.progress = nbt.getInt("progress");
        }
        if (nbt.contains("active")) {
            this.isActive = nbt.getBoolean("active");
        }
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
        cooktime = nbt.getInt("cooktime");
        progress = nbt.getInt("progress");
        isActive = nbt.getBoolean("active");

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
