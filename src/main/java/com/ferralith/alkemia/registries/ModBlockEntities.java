package com.ferralith.alkemia.registries;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.entity.PedestalBlockEntity;
import com.ferralith.alkemia.entity.chalkboard.ChalkboardPartEntity;
import com.ferralith.alkemia.entity.JarBlockEntity;
import com.ferralith.alkemia.entity.chalkboard.MasterChalkboardEntity;
import com.ferralith.alkemia.entity.ritualblock.RitualMasterBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Alkemia.MODID);

    public static final Supplier<BlockEntityType<JarBlockEntity>> JAR_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "jar_block_entity",
            () -> BlockEntityType.Builder.of(
                    JarBlockEntity::new, ModBlocks.JAR_BLOCK.get()
            ).build(null));

    public static final Supplier<BlockEntityType<ChalkboardPartEntity>> CHALKBOARD_PART_ENTITY =
            BLOCK_ENTITIES.register("chalkboard_part_entity",
                    () -> BlockEntityType.Builder.of(
                            ChalkboardPartEntity::new, ModBlocks.CHALKBOARD_BLOCK.get()
            ).build(null));

    public static final Supplier<BlockEntityType<MasterChalkboardEntity>> MASTER_CHALKBOARD_ENTITY =
            BLOCK_ENTITIES.register("master_chalkboard_entity",
                    () -> BlockEntityType.Builder.of(MasterChalkboardEntity::new,
                            ModBlocks.CHALKBOARD_BLOCK.get()).build(null));

    public static final Supplier<BlockEntityType<RitualMasterBlockEntity>> MASTER_RITUAL_ENTITY =
            BLOCK_ENTITIES.register("master_ritual_entity",
                    () -> BlockEntityType.Builder.of(RitualMasterBlockEntity::new,
                            ModBlocks.RITUAL_BLOCK.get()).build(null));

    public static final Supplier<BlockEntityType<PedestalBlockEntity>> PEDESTAL_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("pedestal_block_entity",
                    () -> BlockEntityType.Builder.of(PedestalBlockEntity::new,
                            ModBlocks.PEDESTAL.get()).build(null));

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}
