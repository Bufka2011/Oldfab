package com.wuest.prefab.Util;

/**
 * Back-port of the 1.8+ dye color enumeration. The metadata value matches the wool/stained glass metadata.
 */
public enum EnumDyeColor {

    WHITE(0, "white", "white"),
    ORANGE(1, "orange", "orange"),
    MAGENTA(2, "magenta", "magenta"),
    LIGHT_BLUE(3, "light_blue", "lightBlue"),
    YELLOW(4, "yellow", "yellow"),
    LIME(5, "lime", "lime"),
    PINK(6, "pink", "pink"),
    GRAY(7, "gray", "gray"),
    SILVER(8, "silver", "silver"),
    CYAN(9, "cyan", "cyan"),
    PURPLE(10, "purple", "purple"),
    BLUE(11, "blue", "blue"),
    BROWN(12, "brown", "brown"),
    GREEN(13, "green", "green"),
    RED(14, "red", "red"),
    BLACK(15, "black", "black");

    private final int meta;
    private final String name;
    private final String unlocalizedName;

    EnumDyeColor(int meta, String name, String unlocalizedName) {
        this.meta = meta;
        this.name = name;
        this.unlocalizedName = unlocalizedName;
    }

    /**
     * @return The wool/stained glass/stained clay metadata for this color.
     */
    public int getMetadata() {
        return this.meta;
    }

    /**
     * @return The dye item metadata for this color.
     */
    public int getDyeDamage() {
        return 15 - this.meta;
    }

    public String getName() {
        return this.name;
    }

    public String getUnlocalizedName() {
        return this.unlocalizedName;
    }

    @Override
    public String toString() {
        return this.unlocalizedName;
    }

    public static EnumDyeColor byMetadata(int meta) {
        return values()[(meta & 15)];
    }

    public static EnumDyeColor byName(String name) {
        for (EnumDyeColor color : values()) {
            if (color.name.equalsIgnoreCase(name) || color.name()
                .equalsIgnoreCase(name)) {
                return color;
            }
        }

        return null;
    }
}
