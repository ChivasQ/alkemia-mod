package com.ferralith.alkemia.client;

import com.ferralith.alkemia.block.ChalkboardPartBlock;
import com.ferralith.alkemia.entity.chalkboard.ChalkboardPartEntity;
import com.ferralith.alkemia.entity.chalkboard.MasterChalkboardEntity;
import com.ferralith.alkemia.registries.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
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

                if (be instanceof MasterChalkboardEntity masterBE) {

                    handleDrawing(masterBE, mc, blockHitResult, partPos);
                }
            } else if (level.getBlockEntity(partPos) instanceof MasterChalkboardEntity masterBE) {
                handleDrawing(masterBE, mc, blockHitResult, partPos);
            }
        }
    }

    private static void handleDrawing(MasterChalkboardEntity masterBE, Minecraft mc, BlockHitResult blockHitResult, BlockPos partPos) {
        var itemInHand = mc.player.getItemInHand(InteractionHand.MAIN_HAND);

        if (itemInHand.getItem() == ModItems.CHALK_ITEM.get()) {

            Vec3 hitVec = blockHitResult.getLocation();
            double localX = hitVec.x - partPos.getX();
            double localZ = hitVec.z - partPos.getZ();
            int pixelX_local = (int) (localX * 16);
            int pixelZ_local = (int) (localZ * 16);


            if (oldMouseX == null || oldMouseZ == null) {
                masterBE.setPixelClient(partPos, pixelZ_local, pixelX_local, (byte) 1, 3); //TODO: MAKE COLOR SYSTEM
            } else {
                line2d(pixelZ_local, pixelX_local, oldMouseX, oldMouseZ, masterBE, partPos);
            }
            oldMouseX = pixelZ_local;
            oldMouseZ = pixelX_local;
            return;

        } else if (itemInHand.isEmpty() && mc.player.isShiftKeyDown()) {
            Vec3 hitVec = blockHitResult.getLocation();
            double localX = hitVec.x - partPos.getX();
            double localZ = hitVec.z - partPos.getZ();
            int pixelX_local = (int) (localX * 16);
            int pixelZ_local = (int) (localZ * 16);

            masterBE.setPixelClient(partPos, pixelZ_local, pixelX_local, (byte) 0,  9);
            return;
        }
    }


    private static void line2d(int x0, int z0, int x1, int y1, MasterChalkboardEntity masterBE, BlockPos pos) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - z0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = z0 < y1 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            masterBE.setPixelClient(pos, x0, z0, (byte) 1, 3);
            //setpixel
            if ((x0 == x1) && (z0 == y1)) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                z0 += sy;
            }
        }

    }
}
