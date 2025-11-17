package com.ferralith.alkemia.commands;

import com.ferralith.alkemia.Alkemia;
import com.ferralith.alkemia.entity.ritualblock.RitualMasterBlockEntity;
import com.ferralith.alkemia.ritual.RitualFigures;
import com.ferralith.alkemia.ritual.data.RitualSavingManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.nio.file.Path;

@EventBusSubscriber(modid = Alkemia.MODID)
public class ModCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("alkemia")
                        .then(Commands.literal("saveritual")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                                .executes(ModCommands::executeSaveRitual)
                                        )
                                )
                        )
        );
    }

    private static int executeSaveRitual(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        try {
            final String ritualName = StringArgumentType.getString(context, "name");
            final BlockPos startPos = BlockPosArgument.getBlockPos(context, "pos");
            final Level world = source.getLevel();

            RitualFigures graph;

            BlockEntity entity = world.getBlockEntity(startPos);
            if (entity instanceof RitualMasterBlockEntity blockEntity) {
                graph = blockEntity.getGraph();

                if (graph.getJoints().isEmpty()) {
                    return 0;
                }

                Path savePath = source.getServer().getWorldPath(LevelResource.ROOT)
                        .resolve("generated_rituals")
                        .resolve(ritualName + ".json");

                RitualSavingManager.saveRitualToFile(graph, savePath);

                source.sendSuccess(() ->
                                Component.literal("Ritual '" + ritualName + "' saved!"),
                        true
                );

                return 1;
            }
            return 0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}