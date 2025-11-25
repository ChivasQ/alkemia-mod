package com.ferralith.alkemia.entity.ritualblock;

import com.ferralith.alkemia.client.PlayerSelection;
import com.ferralith.alkemia.entity.PedestalBlockEntity;
import com.ferralith.alkemia.mixin.InteractionAccessor;
import com.ferralith.alkemia.registries.ModAttachments;
import com.ferralith.alkemia.registries.ModBlockEntities;
import com.ferralith.alkemia.registries.ModSounds;
import com.ferralith.alkemia.ritual.*;
import com.ferralith.alkemia.ritual.data.RitualRecipeData;
import com.ferralith.alkemia.ritual.data.RitualRecipeManager;
import com.ibm.icu.impl.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2i;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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

    public void checkForRitual() {
        if (level.isClientSide()) return;

        if (!isActive) {
//            isActive = true;

            List<BlockPos> nearbyPedestals = getItemsInNearbyPedestals(getLevel(), getBlockPos());
            System.out.println("Non-empty pedestals" + nearbyPedestals.size());
            RitualRecipeData recipe = RitualRecipeManager.findMatchingRecipe(this.graph, nearbyPedestals, getLevel(), getBlockPos());
            if (recipe == null) return;
            Minecraft.getInstance().player.sendSystemMessage(Component.literal(recipe.template));

            if (recipe.results.getFirst().type == RitualRecipeData.RecipeType.CRAFT) {
                level.playSound(null, getBlockPos(), ModSounds.RITUAL_ACTIVATED.get(), SoundSource.BLOCKS);
                removeItemsFromPedestals(recipe, nearbyPedestals);

                System.out.println(recipe.results.getFirst().data);
                String item_str = recipe.results.getFirst().data.getAsString();
                ResourceLocation item_loc = ResourceLocation.parse(item_str);
                Item item = BuiltInRegistries.ITEM.get(item_loc);
                putItemInPedestal(new ItemStack(item), getBlockPos().above());
            }
        }
    }

    private void putItemInPedestal(ItemStack itemStack, BlockPos blockPos) {
        BlockEntity blockEntity = getLevel().getBlockEntity(blockPos);
        if (blockEntity instanceof PedestalBlockEntity pedestal) {
            pedestal.clearInventory();
            pedestal.inventory.insertItem(0, itemStack, false);
        }
    }

    private void removeItemsFromPedestals(RitualRecipeData recipe, List<BlockPos> nearbyPedestals) {
        List<ResourceLocation> neededItems = new ArrayList<>();
        for (RitualRecipeData.JsonIngredient ingredient : recipe.item_inputs) {
            if (ingredient.item != null) {
                for(int i=0; i < ingredient.count; i++) {
                    neededItems.add(ResourceLocation.parse(ingredient.item));
                }
            }
        }

        for (BlockPos pedestalPos : nearbyPedestals) {
            BlockEntity blockEntity = getLevel().getBlockEntity(pedestalPos);
            if (blockEntity instanceof PedestalBlockEntity pedestal) {
                ItemStack stackInSlot = pedestal.inventory.getStackInSlot(0);

                if (stackInSlot.isEmpty()) continue;

                Iterator<ResourceLocation> iterator = neededItems.iterator();
                while (iterator.hasNext()) {
                    ResourceLocation neededLoc = iterator.next();

                    if (stackInSlot.is(BuiltInRegistries.ITEM.get(neededLoc))) {
                        iterator.remove();

                        pedestal.inventory.extractItem(0, 1, false);

                        pedestal.setChanged();

                        BlockState pedestalState = getLevel().getBlockState(pedestalPos);
                        getLevel().sendBlockUpdated(pedestalPos, pedestalState, pedestalState, 3);

                        break;
                    }
                }
            }
        }
    }

    private List<BlockPos> getItemsInNearbyPedestals(Level level, BlockPos blockPos) {
        List<BlockPos> posList = new ArrayList<>();

        int radius1 = radius+1;
        for (int x = - radius1; x < radius1; x++) {
            for (int z = - radius1; z < radius1; z++) {
                BlockPos pos = blockPos.offset(x, 1, z);
                if (z == 0 && x == 0) continue;
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof PedestalBlockEntity pedestalBlockEntity) {
                    if (!pedestalBlockEntity.inventory.getStackInSlot(0).isEmpty()) {
                        posList.add(pos);
                    }
                }
            }
        }

        return posList;
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

            spawnInteractionEntity(centerPos, newNode, i);


            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void spawnInteractionEntity(Vec3 centerPos, Vec3 newNode, int i) {
        Interaction interaction = new Interaction(EntityType.INTERACTION, level);
        interaction.setCustomNameVisible(true);

        interaction.setPos(centerPos.x + newNode.x, centerPos.y + newNode.y, centerPos.z + newNode.z);
        ((InteractionAccessor) interaction).invokeSetWidth(0.5f);
        ((InteractionAccessor) interaction).invokeSetHeight(0.25f);
        interaction.setCustomName(Component.literal("node" + i));

        interaction.getPersistentData().putUUID(RITUAL_MASTER_TAG, ritualID);
        interaction.getPersistentData().putInt(RITUAL_NODE_INDEX_TAG, i);
        interaction.getPersistentData().putLong(RITUAL_MATER_POS, this.worldPosition.asLong());

        level.addFreshEntity(interaction);
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
