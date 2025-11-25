package com.ferralith.alkemia.registries;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.block.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Alkemia.MODID);

    public static final DeferredBlock<Block> EXAMPLE_BLOCK =
            BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE));


    public static final DeferredBlock<Block> JAR_BLOCK =
            BLOCKS.register(
                    "jar_block",
                    resourceLocation -> new JarBlock(BlockBehaviour.Properties.of()
                            .noOcclusion()
                            .destroyTime(2)
                            .lightLevel(state -> state.getValue(JarBlock.LIGHT_LEVEL))));




    @SuppressWarnings("deprecation")
    public static final DeferredHolder<Block, AbstractCauldronBlock> COOL_LAVA_CAULDRON = BLOCKS.register(
            "mana_cauldron_block",
            () -> new ManaCauldronBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.CAULDRON).lightLevel(p_152690_ -> 15))
    );

    public static final DeferredHolder<Block, LiquidBlock> MANA_BLOCK = BLOCKS.register(
            "mana",
            () -> new LiquidBlock(
                    ModFluids.SOURCE_MANA.get(),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.FIRE)
                            .replaceable()
                            .noCollission()
                            .strength(100.0f)
                            .lightLevel(state -> 15)
                            .pushReaction(PushReaction.DESTROY)
                            .noLootTable()
                            .liquid()
                            .sound(SoundType.EMPTY)
            )
    );

    public static final DeferredBlock<Block> PEDESTAL = BLOCKS.register(
            "pedestal",
            () -> new PedestaBlock(BlockBehaviour.Properties.of().strength(4))
    );


    public static final DeferredBlock<Block> CHALKBOARD_BLOCK = BLOCKS.register(
            "chalkboard_block",
            () -> new ChalkboardPartBlock(BlockBehaviour.Properties.of().strength(4).noCollission())
    );

    public static final DeferredBlock<Block> RITUAL_BLOCK = BLOCKS.register(
            "ritual_block",
            () -> new RitualBaseBlock(BlockBehaviour.Properties.of().strength(4))
    );

    public static final DeferredBlock<Block> PORPHYRY_STONE = registerBlock(
            "porphyry_stone",
            () -> new Block(BlockBehaviour.Properties.of().strength(4))
    );
    public static final DeferredBlock<Block> SMOOTH_PORPHYRY = registerBlock(
            "smooth_porphyry",
            () -> new Block(BlockBehaviour.Properties.of().strength(4))
    );
    public static final DeferredBlock<Block> PORPHYRY_BRICKS = registerBlock(
            "porphyry_bricks",
            () -> new Block(BlockBehaviour.Properties.of().strength(4))
    );

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }
    private static <T extends Block> DeferredItem<BlockItem> registerBlockItem(String name, DeferredBlock<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
