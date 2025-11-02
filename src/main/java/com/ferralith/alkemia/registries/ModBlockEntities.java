package com.ferralith.alkemia.registries;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.entity.JarBlockEntity;
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


    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}
