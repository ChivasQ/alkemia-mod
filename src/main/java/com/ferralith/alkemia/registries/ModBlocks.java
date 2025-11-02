package com.ferralith.alkemia.registries;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.blocks.JarBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Alkemia.MODID);

    public static final DeferredBlock<Block> EXAMPLE_BLOCK =
            BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE));
    public static final DeferredBlock<Block> JAR_BLOCK =
            BLOCKS.register(
                    "jar_block",
                    resourceLocation -> new JarBlock(BlockBehaviour.Properties.of()
                            .noOcclusion().destroyTime(2))
            );



    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
