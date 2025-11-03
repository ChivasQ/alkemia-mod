package com.ferralith.alkemia.fluid;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ManaFluidType extends FluidType {
    public ManaFluidType() {
        super(Properties.create()
                .fallDistanceModifier(0.0f)
                .pathType(PathType.LAVA)
                .adjacentPathType(null)
                .lightLevel(15)
                .density(3000)
                .viscosity(6000)

                .supportsBoating(true)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
                .canConvertToSource(false)
                .temperature(23)                                                                                                                //cauldron
                .addDripstoneDripping(PointedDripstoneBlock.LAVA_TRANSFER_PROBABILITY_PER_RANDOM_TICK, ParticleTypes.DRIPPING_DRIPSTONE_LAVA, null, SoundEvents.POINTED_DRIPSTONE_DRIP_LAVA_INTO_CAULDRON)
        );
    }


}
