package com.wuest.prefab.Util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

/**
 * Back-port of the 1.8+ immutable block position class.
 */
public class BlockPos {

    public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);

    private final int x;
    private final int y;
    private final int z;

    public BlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPos(double x, double y, double z) {
        this(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
    }

    public BlockPos(Entity entity) {
        this(entity.posX, entity.posY, entity.posZ);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public BlockPos add(int x, int y, int z) {
        return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.x + x, this.y + y, this.z + z);
    }

    public BlockPos add(BlockPos pos) {
        return this.add(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockPos subtract(BlockPos pos) {
        return this.add(-pos.getX(), -pos.getY(), -pos.getZ());
    }

    public BlockPos up() {
        return this.up(1);
    }

    public BlockPos up(int n) {
        return this.offset(EnumFacing.UP, n);
    }

    public BlockPos down() {
        return this.down(1);
    }

    public BlockPos down(int n) {
        return this.offset(EnumFacing.DOWN, n);
    }

    public BlockPos north() {
        return this.north(1);
    }

    public BlockPos north(int n) {
        return this.offset(EnumFacing.NORTH, n);
    }

    public BlockPos south() {
        return this.south(1);
    }

    public BlockPos south(int n) {
        return this.offset(EnumFacing.SOUTH, n);
    }

    public BlockPos west() {
        return this.west(1);
    }

    public BlockPos west(int n) {
        return this.offset(EnumFacing.WEST, n);
    }

    public BlockPos east() {
        return this.east(1);
    }

    public BlockPos east(int n) {
        return this.offset(EnumFacing.EAST, n);
    }

    public BlockPos offset(EnumFacing facing) {
        return this.offset(facing, 1);
    }

    public BlockPos offset(EnumFacing facing, int n) {
        return n == 0 ? this
            : new BlockPos(
                this.x + facing.getFrontOffsetX() * n,
                this.y + facing.getFrontOffsetY() * n,
                this.z + facing.getFrontOffsetZ() * n);
    }

    public double distanceSq(double toX, double toY, double toZ) {
        double d0 = this.x - toX;
        double d1 = this.y - toY;
        double d2 = this.z - toZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public double distanceSq(BlockPos to) {
        return this.distanceSq(to.getX(), to.getY(), to.getZ());
    }

    /**
     * Gets every position in the box defined by the two corners (inclusive).
     */
    public static List<BlockPos> getAllInBox(BlockPos from, BlockPos to) {
        int minX = Math.min(from.getX(), to.getX());
        int minY = Math.min(from.getY(), to.getY());
        int minZ = Math.min(from.getZ(), to.getZ());
        int maxX = Math.max(from.getX(), to.getX());
        int maxY = Math.max(from.getY(), to.getY());
        int maxZ = Math.max(from.getZ(), to.getZ());

        ArrayList<BlockPos> positions = new ArrayList<>();

        for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int x = minX; x <= maxX; x++) {
                    positions.add(new BlockPos(x, y, z));
                }
            }
        }

        return positions;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof BlockPos)) {
            return false;
        }

        BlockPos pos = (BlockPos) other;
        return this.x == pos.x && this.y == pos.y && this.z == pos.z;
    }

    @Override
    public int hashCode() {
        return (this.y + this.z * 31) * 31 + this.x;
    }

    @Override
    public String toString() {
        return "BlockPos{x=" + this.x + ", y=" + this.y + ", z=" + this.z + "}";
    }
}
