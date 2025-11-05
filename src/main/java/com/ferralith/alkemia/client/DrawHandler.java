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
            Byte colorId = itemInHand.get(ModDataComponents.COLOR);
            if (colorId == null) colorId = (byte) 0;

            if (oldMouseX == null || oldMouseZ == null) {
                masterBE.setPixelClient(partPos, pixelZ_local, pixelX_local, (byte) (colorId+1), 3); //TODO: MAKE COLOR SYSTEM
            } else {
                masterBE.setPixelClient(partPos, pixelZ_local, pixelX_local, (byte) (colorId+1), 3); //FIXME: FIX LINE ALG
                //line2d(pixelZ_local, pixelX_local, oldMouseX, oldMouseZ, masterBE, partPos);
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

    private static void newline2d(int g_x0, int g_z0, int g_x1, int g_z1, MasterChalkboardEntity masterBE, int yLevel, byte color) {
        int dx = Math.abs(g_x1 - g_x0);
        int dy = Math.abs(g_z1 - g_z0);
        int sx = g_x0 < g_x1 ? 1 : -1;
        int sy = g_z0 < g_z1 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            int blockX = Math.floorDiv(g_x0, 16);
            int blockZ = Math.floorDiv(g_z0, 16);
            BlockPos currentPos = new BlockPos(blockX, yLevel, blockZ);

            int localX = Math.floorMod(g_x0, 16);
            int localZ = Math.floorMod(g_z0, 16);

            masterBE.setPixelClient(currentPos, localX, localZ, color, 3);

            if ((g_x0 == g_x1) && (g_z0 == g_z1)) break;
            int e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                g_x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                g_z0 += sy;
            }
        }
    }

    private static void newhandleDrawing(MasterChalkboardEntity masterBE, Minecraft mc, BlockHitResult blockHitResult, BlockPos partPos) {
        var itemInHand = mc.player.getItemInHand(InteractionHand.MAIN_HAND);

        Vec3 hitVec = blockHitResult.getLocation();

        int pixelX_local = (int) Math.floor((hitVec.x - partPos.getX()) * 16);
        int pixelZ_local = (int) Math.floor((hitVec.z - partPos.getZ()) * 16);

        int globalPixelX = Math.floorDiv((int)Math.floor(hitVec.x * 16), 1);
        int globalPixelZ = Math.floorDiv((int)Math.floor(hitVec.z * 16), 1);


        byte color = (byte) 0; //erasing color
        boolean drawing = false;

        if (itemInHand.getItem() == ModItems.CHALK_ITEM.get()) {
            color = (byte) 1;
            drawing = true;
        } else if (itemInHand.isEmpty() && mc.player.isShiftKeyDown()) {
            color = (byte) 0; //erasing color
            drawing = true;
        }

        if (drawing) {
            if (oldMouseX == null || oldMouseZ == null) {
                masterBE.setPixelClient(partPos, pixelZ_local, pixelX_local, color, 3);
            } else {
                newline2d(
                        globalPixelZ, globalPixelX,
                        oldMouseX, oldMouseZ,
                        masterBE,
                        partPos.getY(),
                        color
                );
            }
            oldMouseX = globalPixelZ;
            oldMouseZ = globalPixelX;
        }
    }
}
