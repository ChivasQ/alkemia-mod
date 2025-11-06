package com.ferralith.alkemia.block;

import com.ferralith.alkemia.entity.chalkboard.ChalkboardPartEntity;
import com.ferralith.alkemia.entity.chalkboard.MasterChalkboardEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ChalkboardPartBlock extends BaseEntityBlock {
    private static final MapCodec<ChalkboardPartBlock> CODEC = simpleCodec(ChalkboardPartBlock::new);

    public static final BooleanProperty MASTER = BooleanProperty.create("master");

    public ChalkboardPartBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(MASTER, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(MASTER);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        if (blockState.getValue(MASTER)) {
            return new MasterChalkboardEntity(blockPos, blockState);
        }
        return new ChalkboardPartEntity(blockPos, blockState);
    }


//    @Override
//    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
//        //super.onPlace(state, level, pos, oldState, movedByPiston);
//
//        BlockPos masterPos = null;
//        BlockPos[] offs = {
//                pos.north(), pos.south(), pos.east(), pos.west()
//        };
//
//        for (BlockPos checkPos : offs) {
//            BlockEntity be = level.getBlockEntity(checkPos);
//
//            if (be instanceof MasterChalkboardEntity master) {
//                masterPos = master.getBlockPos();
//                break;
//            }
//            if (be instanceof ChalkboardPartEntity part) {
//                BlockEntity potentialMaster = part.getMaster(level);
//                if (potentialMaster != null) {
//                    masterPos = potentialMaster.getBlockPos();
//                    break;
//                }
//            }
//        }
//
//        if (masterPos == null) {
//            if (!state.getValue(MASTER)) {
//                level.setBlock(pos, state.setValue(MASTER, true), 3);
//            }
//            return;
//        }
//
//        BlockEntity thisBE = level.getBlockEntity(pos);
//
//        if (state.getValue(MASTER)) {
//            BlockState newState = state.setValue(MASTER, false);
//
//            level.setBlock(pos, newState, Block.UPDATE_ALL);
//
//            BlockEntity be = level.getBlockEntity(pos);
//            if (!(be instanceof ChalkboardPartEntity)) {
//                be = new ChalkboardPartEntity(pos, newState);
//                ((ChalkboardPartEntity) be).setMaster(masterPos);
//                level.setBlockEntity((ChalkboardPartEntity) be);
//                System.out.println(level.isClientSide());
//                level.sendBlockUpdated(pos, state.setValue(MASTER, false), newState, Block.UPDATE_ALL_IMMEDIATE);
//
//            }
//
//        }
//    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
//        if (level.isClientSide()) return;


        BlockPos masterPos = null;
        BlockPos[] offs = {
                pos.north(), pos.south(), pos.east(), pos.west()
        };

        for (BlockPos checkPos : offs) {
            BlockEntity be = level.getBlockEntity(checkPos);

            if (be instanceof MasterChalkboardEntity master) {
                masterPos = master.getBlockPos();
                break;
            }
            if (be instanceof ChalkboardPartEntity part) {
                BlockEntity potentialMaster = part.getMaster(level);
                if (potentialMaster != null) {
                    masterPos = potentialMaster.getBlockPos();
                    break;
                }
            }
        }

        if (masterPos == null) {
            if (!state.getValue(MASTER)) {
                level.setBlock(pos, state.setValue(MASTER, true), 3);
            }
            return;
        }

        if (state.getValue(MASTER)) {
            BlockState newState = state.setValue(MASTER, false);

            level.setBlock(pos, newState, Block.UPDATE_ALL);

            BlockEntity be = level.getBlockEntity(pos);
            if (! (be instanceof ChalkboardPartEntity)) {
                be = new ChalkboardPartEntity(pos, newState);
                ((ChalkboardPartEntity) be).setMaster(masterPos);
                level.setBlockEntity(be);
                System.out.println(level.isClientSide());
                level.sendBlockUpdated(pos, state.setValue(MASTER, false), newState, Block.UPDATE_ALL_IMMEDIATE);

            }
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);

        BlockPos masterPos = null;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ChalkboardPartEntity part) {
            masterPos = part.getMaster(level).getBlockPos();
            if (masterPos != null) {
                if (level.getBlockEntity(masterPos) instanceof MasterChalkboardEntity part1) {
                    part1.removeBlock(pos);
                }
            }
        } else if (be instanceof MasterChalkboardEntity part) {
            part.getRidOfMap();
        }

    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return super.getTicker(level, state, blockEntityType);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    public static final VoxelShape SHAPE = java.util.Optional.of(Block.box(0, 0, 0, 16, 0.5f, 16)).get();

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
}
