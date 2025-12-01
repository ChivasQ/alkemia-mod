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
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.level.block.LevelEvent;
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

    public enum RitualState {
        IDLE,           // Ожидание
        STARTING,       // Подготовка (визуал)
        CONSUMING,      // Поедание предметов по одному
        CRAFTING,       // Финальная обработка
        FINISHED        // Спавн результата
    }

    private RitualState state = RitualState.IDLE;

    private int stateTimer = 0;
    private int currentPedestalIndex = 0;
    private int consumeTimer = 0;

    private static final int CONSUME_DELAY = 60;

    private List<BlockPos> lockedPedestals = new ArrayList<>();
    private RitualRecipeData currentRecipe;

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


    public List<BlockPos> getLockedPedestals() {
        return lockedPedestals;
    }

    public void tick() {
        if (level == null || level.isClientSide()) return;

        if (state == RitualState.IDLE) return;

        stateTimer++;
        System.out.println(state);
        switch (state) {
            case STARTING:
                if (stateTimer >= 20) {
                    state = RitualState.CONSUMING;
                    stateTimer = 0;
                    currentPedestalIndex = 0;
                    consumeTimer = 0;
                    sync();
                }
                break;

            case CONSUMING:
                consumeTimer++;

                if (consumeTimer >= CONSUME_DELAY) {
                    consumeTimer = 0;
                    processNextPedestal();
                }
                sync();
                break;

            case CRAFTING:
                if (stateTimer >= 40) {
                    finishRitual();
                }
                break;

            case FINISHED:
                resetRitual();
                break;
        }
    }

    public int getConsumeTimer() {
        return consumeTimer;
    }

    public int getCurrentPedestalIndex() {
        return currentPedestalIndex;
    }

    private void processNextPedestal() {
        if (currentPedestalIndex >= lockedPedestals.size()) {
            state = RitualState.CRAFTING;
            stateTimer = 0;
            sync();
            return;
        }

        BlockPos pedestalPos = lockedPedestals.get(currentPedestalIndex);
        BlockEntity be = level.getBlockEntity(pedestalPos);

        if (be instanceof PedestalBlockEntity pedestal) {
            if (!pedestal.inventory.extractItem(0, 1, false).isEmpty()) {

                level.playSound(null, pedestalPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);

                level.sendBlockUpdated(pedestalPos, pedestal.getBlockState(), pedestal.getBlockState(), 3);

                currentPedestalIndex++;

                sync();

            } else {
                abortRitual("Missing item at index " + currentPedestalIndex);
            }
        } else {
            abortRitual("Invalid pedestal block entity");
        }
    }


    public void checkForRitual() {
        if (level.isClientSide() || state != RitualState.IDLE) return;

        List<BlockPos> nearbyPedestals = getItemsInNearbyPedestals(getLevel(), getBlockPos());
        RitualRecipeData recipe = RitualRecipeManager.findMatchingRecipe(this.graph, nearbyPedestals, getLevel(), getBlockPos());

        List<BlockPos> filtered = RitualRecipeManager.filterPedestals(nearbyPedestals, recipe, getLevel(), getBlockPos());

        if (recipe != null && filtered != null) {
            this.state = RitualState.STARTING;
            this.stateTimer = 0;
            this.currentRecipe = recipe;

            this.lockedPedestals.clear();
            this.lockedPedestals.addAll(filtered);

            System.out.println("Ritual Started. Pedestals: " + lockedPedestals.size());

            level.playSound(null, getBlockPos(), ModSounds.RITUAL_ACTIVATED.get(), SoundSource.BLOCKS);
            sync();
        }
    }

    private void putItemInPedestal(ItemStack itemStack, BlockPos blockPos) {
        BlockEntity blockEntity = getLevel().getBlockEntity(blockPos);
        if (blockEntity instanceof PedestalBlockEntity pedestal) {
            pedestal.clearInventory();
            pedestal.inventory.insertItem(0, itemStack, false);
            level.sendBlockUpdated(blockPos, pedestal.getBlockState(), pedestal.getBlockState(), 3);
        }
    }

    private void removeItemsFromPedestals(RitualRecipeData recipe, List<BlockPos> nearbyPedestals) {
        List<Item> neededItems = new ArrayList<>();
        for (RitualRecipeData.JsonIngredient ingredient : recipe.item_inputs) {
            if (ingredient.item != null) {
                for(int i=0; i < ingredient.count; i++) {
                    neededItems.add(BuiltInRegistries.ITEM.get(ResourceLocation.parse(ingredient.item)));
                }
            }
        }

        for (BlockPos pedestalPos : nearbyPedestals) {
            BlockEntity blockEntity = getLevel().getBlockEntity(pedestalPos);
            if (blockEntity instanceof PedestalBlockEntity pedestal) {
                ItemStack stackInSlot = pedestal.inventory.getStackInSlot(0);

                if (stackInSlot.isEmpty()) continue;

                Iterator<Item> iterator = neededItems.iterator();
                while (iterator.hasNext()) {
                    Item item = iterator.next();

                    if (stackInSlot.is(item)) {
                        iterator.remove();

                        pedestal.extractItem(0,1);

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

        Collections.sort(posList, ((o1, o2) -> {
            int dist_o1 = blockPos.distManhattan(o1);
            int dist_o2 = blockPos.distManhattan(o2);
            return dist_o1 - dist_o2;
        }));
        return posList;
    }

    private void finishRitual() {
        if (currentRecipe != null && currentRecipe.results != null && !currentRecipe.results.isEmpty()) {
            if (currentRecipe.results.getFirst().type == RitualRecipeData.RecipeType.CRAFT) {
                String item_str = currentRecipe.results.getFirst().data.getAsString();
                ResourceLocation item_loc = ResourceLocation.parse(item_str);
                Item item = BuiltInRegistries.ITEM.get(item_loc);

                putItemInPedestal(new ItemStack(item), getBlockPos().above());
                level.playSound(null, getBlockPos(), ModSounds.RITUAL_ACTIVATED.get(), SoundSource.BLOCKS, 1f, 1f);
            }
        }

        state = RitualState.FINISHED;
    }

    private void abortRitual(String reason) {
        System.out.println("Ritual Aborted: " + reason);
        level.playSound(null, getBlockPos(), SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1f, 1f);
        resetRitual();
    }

    private void resetRitual() {
        state = RitualState.IDLE;
        stateTimer = 0;
        currentPedestalIndex = 0;
        lockedPedestals.clear();
        consumeTimer = 0;
        currentRecipe = null;
        sync();
    }

    private void sync() {
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
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
        saveRitualData(nbt, registries);
    }

    private void saveRitualData(CompoundTag nbt, HolderLookup.Provider registries) {
        nbt.putString("state", state.name());
        nbt.putInt("state_timer", stateTimer);
        nbt.putInt("consume_timer", consumeTimer);
        nbt.putInt("pedestal_index", currentPedestalIndex);

        if (this.ritualID != null) nbt.putUUID("ritual_id", this.ritualID);
        nbt.put("graph_data", graph.serializeNBT(registries));

        long[] pedestalsArray = lockedPedestals.stream().mapToLong(BlockPos::asLong).toArray();
        nbt.putLongArray("locked_pedestals", pedestalsArray);

        if (this.currentRecipe != null) {
            nbt.putString("recipe_name", this.currentRecipe.id);
        }
    }

    private void loadRitualData(CompoundTag nbt, HolderLookup.Provider registries) {
        if (nbt.contains("state")) {
            try {
                this.state = RitualState.valueOf(nbt.getString("state"));
            } catch (IllegalArgumentException e) {
                this.state = RitualState.IDLE;
            }
        }

        if (nbt.contains("state_timer")) this.stateTimer = nbt.getInt("state_timer");
        if (nbt.contains("consume_timer")) this.consumeTimer = nbt.getInt("consume_timer");
        if (nbt.contains("pedestal_index")) this.currentPedestalIndex = nbt.getInt("pedestal_index");
        if (nbt.contains("recipe_name")) this.currentRecipe = RitualRecipeManager.getRecipe(ResourceLocation.parse(nbt.getString("recipe_name")));

        if (nbt.hasUUID("ritual_id")) this.ritualID = nbt.getUUID("ritual_id");
        if (nbt.contains("graph_data", Tag.TAG_COMPOUND)) graph.deserializeNBT(registries, nbt.getCompound("graph_data"));

        if (nbt.contains("locked_pedestals")) {
            this.lockedPedestals.clear();
            long[] pedestalsArray = nbt.getLongArray("locked_pedestals");
            for (long p : pedestalsArray) {
                this.lockedPedestals.add(BlockPos.of(p));
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag nbt = super.getUpdateTag(registries);
        saveRitualData(nbt, registries);
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt, HolderLookup.Provider registries) {
        super.handleUpdateTag(nbt, registries);
        loadRitualData(nbt, registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        loadRitualData(nbt, registries);
    }

    public RitualState getState() { return state; }
    public RitualFigures getGraph() { return graph; }
    public int getRadius() { return radius; }
}
