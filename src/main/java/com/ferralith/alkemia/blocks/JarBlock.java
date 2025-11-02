package com.ferralith.alkemia.blocks;

import com.ferralith.alkemia.entity.JarBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class JarBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Stream.of(
            Block.box(3, 0, 3, 13, 13, 13),
            Block.box(4, 13, 4, 12, 14, 12),
            Block.box(5, 14, 5, 11, 16, 11)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final MapCodec<JarBlock> CODEC = simpleCodec(JarBlock::new);


    public JarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new JarBlockEntity(pos, state);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return super.getTicker(level, state, blockEntityType);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        // Клиентская сторона:
        if (level.isClientSide) {
            // Если у предмета есть Capability, говорим SUCCESS, чтобы рука махнула.
            // Иначе - PASS, чтобы палка (например) не вызывала взмах руки.
            return stack.getCapability(Capabilities.FluidHandler.ITEM) != null ?
                    ItemInteractionResult.SUCCESS :
                    ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Серверная сторона:
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof JarBlockEntity)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Получаем Capability и запоминаем, сколько жидкости было ДО.
        IFluidHandler blockFluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, state, blockEntity, null);
        if (blockFluidHandler == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        FluidStack fluidInBlockBefore = blockFluidHandler.getFluidInTank(0).copy();

        // ЭТА ОДНА ФУНКЦИЯ ДЕЛАЕТ ВСЁ:
        // 1. Проверяет Capability у предмета.
        // 2. Пытается перелить из item -> block.
        // 3. Если не вышло, пытается перелить из block -> item.
        // 4. Обрабатывает замену контейнеров (ведро <-> пустое ведро).
        // 5. Обрабатывает креативный режим.
        // 6. Возвращает true, если что-то произошло.
        boolean success = FluidUtil.interactWithFluidHandler(player, hand, level, pos, null);

        if (success) {
            // FluidUtil все сделал. Теперь мы просто проверяем результат, чтобы проиграть звук.
            FluidStack fluidInBlockAfter = blockFluidHandler.getFluidInTank(0);

            // ИСПРАВЛЕННЫЕ ЗВУКИ:
            if (fluidInBlockBefore.isEmpty() && !fluidInBlockAfter.isEmpty()) {
                // Бак был пуст, а теперь полон -> мы НАПОЛНИЛИ бак (опустошили ведро)
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            } else if (!fluidInBlockBefore.isEmpty() && fluidInBlockAfter.isEmpty()) {
                // Бак был полон (или частично), а теперь пуст -> мы ОПУСТОШИЛИ бак (наполнили ведро)
                level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            // (Тут можно добавить еще 'else if' для частичных переливаний, если у них другие звуки)

            return ItemInteractionResult.SUCCESS;
        }

        // FluidUtil не смог ничего сделать (например, кликнули не-жидкостным предметом)
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private static ItemInteractionResult tryToMoveFluid(IFluidHandler from, IFluidHandler to,
                                                        Player player, InteractionHand hand) {
        FluidStack drained = from.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
        int filled = to.fill(drained, IFluidHandler.FluidAction.SIMULATE);

        if (filled > 0) {
            drained = from.drain(filled, IFluidHandler.FluidAction.EXECUTE);
            to.fill(drained, IFluidHandler.FluidAction.EXECUTE);

            if (from instanceof IFluidHandlerItem fluidHandlerItem) {
                if (! player.isCreative()) {
                    player.setItemInHand(hand, fluidHandlerItem.getContainer());
                }
            }
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.FAIL;
    }
}
