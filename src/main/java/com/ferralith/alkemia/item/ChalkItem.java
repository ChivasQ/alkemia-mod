package com.ferralith.alkemia.item;

import com.ferralith.alkemia.client.PlayerSelection;
import com.ferralith.alkemia.entity.ritualblock.RitualMasterBlockEntity;
import com.ferralith.alkemia.registries.ModAttachments;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.phys.Vec3;

public class ChalkItem extends Item {
    public ChalkItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
//        if (!level.isClientSide()) return;
//        PlayerSelection cap = entity.getData(ModAttachments.PLAYER_DATA_SELECTION);
//        BlockPos blockPos = cap.getActiveRitualBlockPos();
//        System.out.println(cap.getFirstSelectedNodeIndex());
//        if (cap.getFirstSelectedNodeIndex() != null && blockPos != null) {
//            System.out.println("hi");
//            if (level.getBlockEntity(blockPos) instanceof RitualMasterBlockEntity master) {
//                Vec3 vec3 = master.getGraph().getNodes().get(cap.getFirstSelectedNodeIndex());
//                Vec3 added = vec3.add(blockPos.getX(), blockPos.getY(), blockPos.getZ());
//                level.addParticle(ParticleTypes.ASH, added.x, added.y, added.z, 0d, 1d, 0d);
//            }
//        }

        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }
}
