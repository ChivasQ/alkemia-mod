package com.ferralith.alkemia.block;

import com.ferralith.alkemia.entity.ritualblock.RitualMasterBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class RitualBaseBlock extends BaseEntityBlock {
    private static final MapCodec<ChalkboardPartBlock> CODEC = simpleCodec(ChalkboardPartBlock::new);
    public static final String RITUAL_MASTER_TAG = "ritual_master_tag";
    public static final String RITUAL_NODE_INDEX_TAG = "ritual_node_index";
    public static final String RITUAL_MATER_POS = "ritual_node_pos";

    public RitualBaseBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RitualMasterBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : (level1, blockPos, blockState, blockEntity) -> ((RitualMasterBlockEntity)blockEntity).tick();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) return super.useWithoutItem(state, level, pos, player, hitResult);
        System.out.println("use");
        if (level.getBlockEntity(pos) instanceof RitualMasterBlockEntity blockEntity) {
            blockEntity.checkForRitual();
        }



        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        System.out.println("setPlacedBy");
        if (level.isClientSide()) {
            return;
        }

        if (level.getBlockEntity(pos) instanceof RitualMasterBlockEntity master) {
            UUID ritualID = UUID.randomUUID();
            master.setRitualID(ritualID);

            List<Vec3> nodes = master.getGraph().getNodes();
            Vec3 centerPos = master.getGraph().blockPos;

            for (int i = 0; i < nodes.size(); i++) {
                Vec3 nodePos = nodes.get(i);

                Interaction interaction = new Interaction(EntityType.INTERACTION, level);
                interaction.setCustomNameVisible(true);

                interaction.setPos(centerPos.x + nodePos.x, centerPos.y + nodePos.y, centerPos.z + nodePos.z);
                System.out.println(interaction.getBoundingBox());
                System.out.println(interaction.getPosition(1));
                interaction.setBoundingBox(interaction.getBoundingBox().inflate(0.5));
                interaction.setCustomName(Component.literal("node" + i));

                interaction.getPersistentData().putUUID(RITUAL_MASTER_TAG, ritualID);
                interaction.getPersistentData().putInt(RITUAL_NODE_INDEX_TAG, i);
                interaction.getPersistentData().putLong(RITUAL_MATER_POS, pos.asLong());

                level.addFreshEntity(interaction);
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide() && state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof RitualMasterBlockEntity master) {

                UUID ritualID = master.getRitualID();
                if (ritualID == null) return;

                int radius = master.getRadius() + 2;
                AABB searchArea = new AABB(pos).inflate(radius);

                List<Interaction> children = level.getEntitiesOfClass(Interaction.class, searchArea);

                for (Interaction child : children) {
                    if (ritualID.equals(child.getPersistentData().getUUID(RITUAL_MASTER_TAG))) {
                        child.discard();
                    }
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
