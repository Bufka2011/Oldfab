package com.wuest.prefab.Util;

import net.minecraft.util.MathHelper;

/**
 * Back-port of the 1.8+ EnumFacing enumeration. Minecraft 1.7.10 only has a very limited facing enum so this class
 * provides the rotation helpers used throughout the structure generation code.
 */
public enum EnumFacing {

    DOWN(0, 1, -1, "down", 0, -1, 0),
    UP(1, 0, -1, "up", 0, 1, 0),
    NORTH(2, 3, 2, "north", 0, 0, -1),
    SOUTH(3, 2, 0, "south", 0, 0, 1),
    WEST(4, 5, 1, "west", -1, 0, 0),
    EAST(5, 4, 3, "east", 1, 0, 0);

    public static final EnumFacing[] HORIZONTALS = new EnumFacing[] { SOUTH, WEST, NORTH, EAST };

    private final int index;
    private final int opposite;
    private final int horizontalIndex;
    private final String name;
    private final int offsetX;
    private final int offsetY;
    private final int offsetZ;

    EnumFacing(int index, int opposite, int horizontalIndex, String name, int offsetX, int offsetY, int offsetZ) {
        this.index = index;
        this.opposite = opposite;
        this.horizontalIndex = horizontalIndex;
        this.name = name;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    /**
     * @return The index of this facing; this matches the vanilla 1.7.10 "side" value.
     */
    public int getIndex() {
        return this.index;
    }

    public int getHorizontalIndex() {
        return this.horizontalIndex;
    }

    public String getName() {
        return this.name;
    }

    public String getName2() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int getFrontOffsetX() {
        return this.offsetX;
    }

    public int getFrontOffsetY() {
        return this.offsetY;
    }

    public int getFrontOffsetZ() {
        return this.offsetZ;
    }

    public boolean isHorizontal() {
        return this.horizontalIndex >= 0;
    }

    public EnumFacing getOpposite() {
        return values()[this.opposite];
    }

    /**
     * Rotate clockwise around the Y axis (when looking down).
     */
    public EnumFacing rotateY() {
        switch (this) {
            case NORTH:
                return EAST;
            case EAST:
                return SOUTH;
            case SOUTH:
                return WEST;
            case WEST:
                return NORTH;
            default:
                return this;
        }
    }

    /**
     * Rotate counter-clockwise around the Y axis (when looking down).
     */
    public EnumFacing rotateYCCW() {
        switch (this) {
            case NORTH:
                return WEST;
            case EAST:
                return NORTH;
            case SOUTH:
                return EAST;
            case WEST:
                return SOUTH;
            default:
                return this;
        }
    }

    public static EnumFacing byName(String name) {
        if (name == null) {
            return null;
        }

        for (EnumFacing facing : values()) {
            if (facing.name.equalsIgnoreCase(name)) {
                return facing;
            }
        }

        return null;
    }

    public static EnumFacing getFront(int index) {
        return values()[MathHelper.abs_int(index % 6)];
    }

    public static EnumFacing getHorizontal(int horizontalIndex) {
        return HORIZONTALS[MathHelper.abs_int(horizontalIndex % 4)];
    }

    /**
     * Gets the horizontal facing for an entity rotation yaw.
     */
    public static EnumFacing fromAngle(double angle) {
        return getHorizontal(MathHelper.floor_double(angle / 90.0D + 0.5D) & 3);
    }
}
