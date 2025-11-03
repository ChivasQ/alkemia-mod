package com.ferralith.alkemia.fluid;

import com.ferralith.alkemia.registries.ModBlocks;
import com.ferralith.alkemia.registries.ModFluids;
import com.ferralith.alkemia.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidType;

import javax.annotation.Nullable;

public abstract class ManaFluid extends FlowingFluid {
    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_MANA.get();
    }

    @Override
    public Fluid getSource() {
        return ModFluids.SOURCE_MANA.get();
    }

    @Override
    public FluidType getFluidType() {
        return ModFluids.MANA_TYPE.get();
    }

    @Override
    protected boolean canConvertToSource(Level level) {
        return false;
    }


    @Override
    public boolean isSource(FluidState fluidState) {
        return false;
    }

    @Override
    public int getAmount(FluidState fluidState) {
        return 0;
    }

    public static class Flowing extends ManaFluid {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends ManaFluid {
        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }

    protected ManaFluid() {
        super();
    }


    @Override
    public Item getBucket() {
        return ModItems.MANA_BUCKET.get();
    }

    @Override
    protected int getSlopeFindDistance(LevelReader worldIn) {
        return 2;
    }

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == ModFluids.SOURCE_MANA.get() || fluid == ModFluids.FLOWING_MANA.get();
    }

    @Override
    protected int getDropOff(LevelReader worldIn) {
        return 1;
    }

    @Override
    public int getTickDelay(LevelReader worldIn) {
        return 2;
    }

    @Override
    protected @Nullable ParticleOptions getDripParticle() {
        return super.getDripParticle();
    }

    /**
     * Returns whether the boat can be used on the fluid.
     *
     * @param state the state of the fluid
     * @param boat  the boat trying to be used on the fluid
     * @return {@code true} if the boat can be used, {@code false} otherwise
     */
    @Override
    public boolean supportsBoating(FluidState state, Boat boat) {
        return true;
    }

    /**
     * Returns whether the block can be extinguished by this fluid.
     *
     * @param state  the state of the fluid
     * @param getter the getter which can get the fluid
     * @param pos    the position of the fluid
     * @return {@code true} if the block can be extinguished, {@code false} otherwise
     */
    @Override
    public boolean canExtinguish(FluidState state, BlockGetter getter, BlockPos pos) {
        return false;
    }

    /**
     * Basic animation is handled with an extended texture file and a corresponding mcmeta file. This is for
     * special effects (intermittent effects, dynamic effects, audio).
     * */
    @Override
    protected void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
        BlockPos blockpos = pos.above();
        if (level.getBlockState(blockpos).isAir() && !level.getBlockState(blockpos).isSolidRender(level, blockpos)) {
            if (random.nextInt(10) == 0) {
                double d0 = (double)pos.getX() + random.nextDouble();
                double d1 = (double)pos.getY() + 1.0;
                double d2 = (double)pos.getZ() + random.nextDouble();
                level.addParticle(ParticleTypes.LAVA, d0, d1, d2, 0.0, 0.0, 0.0);
                level.playLocalSound(
                        d0, d1, d2, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false
                );
            }

            if (random.nextInt(200) == 0) {
                level.playLocalSound(
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        SoundEvents.LAVA_AMBIENT,
                        SoundSource.BLOCKS,
                        0.2F + random.nextFloat() * 0.2F,
                        0.9F + random.nextFloat() * 0.15F,
                        false
                );
            }
        }
    }

    @Override
    public int getSpreadDelay(Level level, BlockPos pos, FluidState currentState, FluidState newState) {
        int i = this.getTickDelay(level);
        if (!currentState.isEmpty()
                && !newState.isEmpty()
                && !currentState.getValue(FALLING)
                && !newState.getValue(FALLING)
                && newState.getHeight(level, pos) > currentState.getHeight(level, pos)
                && level.getRandom().nextInt(4) != 0) {
            i *= 4;
        }

        return i;
    }


    @Override
    protected void spreadTo(LevelAccessor level, BlockPos pos, BlockState blockState, Direction direction, FluidState fluidState) {
        if (direction == Direction.DOWN) {
            FluidState fluidstate = level.getFluidState(pos);
            if (fluidstate.is(FluidTags.WATER)) {
                if (blockState.getBlock() instanceof LiquidBlock) {
                    level.setBlock(pos, net.neoforged.neoforge.event.EventHooks.fireFluidPlaceBlockEvent(level, pos, pos, Blocks.STONE.defaultBlockState()), 3);
                }

                level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
                return;
            }
        }

        super.spreadTo(level, pos, blockState, direction, fluidState);
    }

    @Override
    public boolean canConvertToSource(FluidState state, Level level, BlockPos pos) {
        return canConvertToSource(level);
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
        //noinspection deprecation - used in vanilla/NeoForge
        return state.getHeight(level, pos) >= 0.44444445F && fluid.is(FluidTags.WATER);
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0f;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState state) {
        return ModBlocks.MANA_BLOCK.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }
}
