package com.ferralith.alkemia.client;

import com.ferralith.alkemia.block.ChalkboardPartBlock;
import com.ferralith.alkemia.entity.chalkboard.ChalkboardPartEntity;
import com.ferralith.alkemia.entity.chalkboard.MasterChalkboardEntity;
import com.ferralith.alkemia.registries.ModDataComponents;
import com.ferralith.alkemia.registries.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class DrawHandler {
    private static Integer oldMouseX = null;
    private static Integer oldMouseZ = null;

    public static void handleDrawing() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null) {
            return;
        }
        if (!mc.options.keyUse.isDown()) {
            oldMouseX = null;
            oldMouseZ = null;
            return;
        }

        HitResult hitResult = mc.hitResult;
        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK || hitResult.distanceTo(mc.player) > 6) {
            oldMouseX = null;
            oldMouseZ = null;
            return;
        }

        BlockHitResult blockHitResult = (BlockHitResult) hitResult;
        Level level = mc.level;
        BlockPos partPos = blockHitResult.getBlockPos();
        BlockState partState = level.getBlockState(partPos);
        if (!(partState.getBlock() instanceof ChalkboardPartBlock)) {
            oldMouseX = null;
            oldMouseZ = null;
            return;
        }

        if (partState.getBlock() instanceof ChalkboardPartBlock && blockHitResult.getDirection() == Direction.UP) {
            if (level.getBlockEntity(partPos) instanceof ChalkboardPartEntity partBE) {

                BlockEntity be = partBE.getMaster(level);
                //System.out.println("DRAW HANDLER MASTER POS:" + be);

                if (be instanceof MasterChalkboardEntity masterBE) {
                    //System.out.println("here");
                    handleDrawing(masterBE, mc, blockHitResult, partPos);
                    masterBE.getEntries();
                }
            } else if (level.getBlockEntity(partPos) instanceof MasterChalkboardEntity masterBE) {
                handleDrawing(masterBE, mc, blockHitResult, partPos);
            }
        }
    }
    private static void handleDrawing(MasterChalkboardEntity masterBE, Minecraft mc, BlockHitResult blockHitResult, BlockPos partPos) {
        var itemInHand = mc.player.getItemInHand(InteractionHand.MAIN_HAND);
        Vec3 hitVec = blockHitResult.getLocation();

        double localX = hitVec.x - partPos.getX();
        double localZ = hitVec.z - partPos.getZ();
        int pixelX_local = (int) (localX * 16);
        int pixelZ_local = (int) (localZ * 16);
        int globalX = partPos.getX() * 16 + pixelX_local;
        int globalZ = partPos.getZ() * 16 + pixelZ_local;

        if (itemInHand.getItem() == ModItems.CHALK_ITEM.get()) {
            Byte colorId = itemInHand.get(ModDataComponents.COLOR);
            if (colorId == null) colorId = (byte) 0;

            if (oldMouseX == null || oldMouseZ == null) {
                masterBE.setGlobalPixelClient(globalX, globalZ, (byte) (colorId + 1), 3);
            } else {
                drawLine(oldMouseX, oldMouseZ, globalX, globalZ, masterBE, (byte) (colorId + 1));
            }

            oldMouseX = globalX;
            oldMouseZ = globalZ;
            return;

        } else if (itemInHand.isEmpty() && mc.player.isShiftKeyDown()) {
            masterBE.setGlobalPixelClient(globalX, globalZ, (byte) 0, 5);

            oldMouseX = null;
            oldMouseZ = null;
            return;
        }
    }

    private static void drawLine(int globalX0, int globalZ0, int globalX1, int globalZ1,
                                 MasterChalkboardEntity masterBE, byte color) {
        int dx = Math.abs(globalX1 - globalX0);
        int dz = Math.abs(globalZ1 - globalZ0);
        int sx = globalX0 < globalX1 ? 1 : -1;
        int sz = globalZ0 < globalZ1 ? 1 : -1;
        int err = dx - dz;

        int x = globalX0;
        int z = globalZ0;

        while (true) {
            masterBE.setGlobalPixelClient(x, z, color, 3);

            if (x == globalX1 && z == globalZ1) break;

            int e2 = 2 * err;
            if (e2 > -dz) {
                err -= dz;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                z += sz;
            }
        }
    }
}
