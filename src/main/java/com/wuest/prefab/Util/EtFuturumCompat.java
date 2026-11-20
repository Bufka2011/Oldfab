package com.wuest.prefab.Util;

import net.minecraft.block.Block;

import com.wuest.prefab.Config.ModConfiguration;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.Util.ModernBlockTranslator.PropertyReader;

import cpw.mods.fml.common.Loader;

/**
 * Optional integration with Et Futurum Requiem. That mod back-ports many blocks from newer versions of Minecraft. When
 * it's installed, the structures use its blocks instead of the vanilla 1.7.10 substitutes.
 * <p>
 * Blocks are found by their registry name so there is no compile time dependency on Et Futurum Requiem. If a block was
 * disabled in the Et Futurum Requiem configuration it's not registered and the vanilla substitute is used instead.
 */
public class EtFuturumCompat {

    public static final String MODID = "etfuturum";

    private static final String[] WOOD_TYPES = new String[] { "spruce", "birch", "jungle", "acacia", "dark_oak" };

    private static final String[] COLORS = new String[] { "white", "orange", "magenta", "light_blue", "yellow", "lime",
        "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black" };

    private static Boolean loaded;

    /**
     * Determines if Et Futurum Requiem blocks should be used.
     */
    public static boolean isEnabled() {
        if (loaded == null) {
            loaded = Loader.isModLoaded(MODID);
        }

        if (!loaded) {
            return false;
        }

        // On the client this uses the server's configuration when connected to a server.
        ModConfiguration configuration = Prefab.proxy == null ? null : Prefab.proxy.getServerConfiguration();
        return configuration == null || configuration.useEtFuturumBlocks;
    }

    /**
     * Gets an Et Futurum Requiem block.
     *
     * @param name The registry path of the block.
     * @return Null if Et Futurum Requiem is not enabled or the block is not registered.
     */
    public static Block getBlock(String name) {
        if (!EtFuturumCompat.isEnabled()) {
            return null;
        }

        return ModernBlockTranslator.findBlock(MODID + ":" + name);
    }

    private static BlockState state(String name, int meta) {
        Block block = EtFuturumCompat.getBlock(name);
        return block == null ? null : new BlockState(block, meta);
    }

    /**
     * Translates a 1.10 minecraft block to an Et Futurum Requiem block.
     *
     * @return Null when this block should use the default translation.
     */
    static BlockState translateMinecraftBlock(String name, PropertyReader p) {
        if (!EtFuturumCompat.isEnabled()) {
            return null;
        }

        switch (name) {
            case "stone": {
                String variant = p.get("variant", "stone");
                int meta;

                switch (variant) {
                    case "granite":
                        meta = 1;
                        break;
                    case "smooth_granite":
                        meta = 2;
                        break;
                    case "diorite":
                        meta = 3;
                        break;
                    case "smooth_diorite":
                        meta = 4;
                        break;
                    case "andesite":
                        meta = 5;
                        break;
                    case "smooth_andesite":
                        meta = 6;
                        break;
                    default:
                        return null;
                }

                return state("stone", meta);
            }

            case "dirt":
                return p.is("variant", "coarse_dirt") ? state("coarse_dirt", 0) : null;

            case "bone_block":
                return state("bone", ModernBlockTranslator.getAxisMeta(p));

            case "sea_lantern":
                return state("sea_lantern", 0);

            case "prismarine": {
                String variant = p.get("variant", "prismarine");
                return state(
                    "prismarine_block",
                    variant.equals("dark_prismarine") ? 2 : variant.equals("prismarine_bricks") ? 1 : 0);
            }

            case "purpur_block":
                return state("purpur_block", 0);

            case "purpur_pillar":
                return state("purpur_pillar", ModernBlockTranslator.getAxisMeta(p));

            case "purpur_stairs":
                return state("purpur_stairs", ModernBlockTranslator.getStairsMeta(p));

            case "red_sandstone_stairs":
                return state("red_sandstone_stairs", ModernBlockTranslator.getStairsMeta(p));

            case "red_sandstone": {
                String type = p.get("type", "red_sandstone");
                return state("red_sandstone", type.startsWith("chiseled") ? 1 : type.startsWith("smooth") ? 2 : 0);
            }

            case "end_bricks":
                return state("end_bricks", 0);

            case "red_nether_brick":
                return state("red_netherbrick", 0);

            case "grass_path":
                return state("grass_path", 0);

            case "magma":
                return state("magma", 0);

            case "slime":
                return state("slime", 0);

            case "end_rod":
                return state(
                    "end_rod",
                    ModernBlockTranslator.getFacing(p, EnumFacing.UP)
                        .getIndex());

            case "daylight_detector_inverted":
                return state("daylight_detector_inverted", p.getInt("power", 0));

            case "iron_trapdoor": {
                EnumFacing facing = ModernBlockTranslator.getFacing(p, EnumFacing.NORTH);
                int meta = facing == EnumFacing.SOUTH ? 1
                    : facing == EnumFacing.WEST ? 2 : facing == EnumFacing.EAST ? 3 : 0;
                meta |= p.is("open", "true") ? 4 : 0;
                meta |= p.is("half", "top") ? 8 : 0;
                return state("iron_trapdoor", meta);
            }

            case "concrete":
                return state("concrete", ModernBlockTranslator.indexOf(COLORS, p.get("color", "white")));

            case "concrete_powder":
                return state("concrete_powder", ModernBlockTranslator.indexOf(COLORS, p.get("color", "white")));

            default:
                break;
        }

        for (String wood : WOOD_TYPES) {
            if (name.equals(wood + "_door")) {
                if (p.is("half", "upper")) {
                    return state(
                        "door_" + wood,
                        8 | (p.is("hinge", "right") ? 1 : 0) | (p.is("powered", "true") ? 2 : 0));
                }

                EnumFacing facing = ModernBlockTranslator.getFacing(p, EnumFacing.NORTH);
                return state(
                    "door_" + wood,
                    facing.rotateY()
                        .getHorizontalIndex() | (p.is("open", "true") ? 4 : 0));
            } else if (name.equals(wood + "_fence")) {
                return state("fence_" + wood, 0);
            } else if (name.equals(wood + "_fence_gate")) {
                return state(
                    "fence_gate_" + wood,
                    ModernBlockTranslator.getFacing(p, EnumFacing.NORTH)
                        .getHorizontalIndex() | (p.is("open", "true") ? 4 : 0));
            }
        }

        return null;
    }

    /**
     * Translates a Prefab block from 1.10 to an Et Futurum Requiem block. This is used for the granite, diorite and
     * andesite stairs and slabs which don't exist in the 1.7.10 version of Prefab.
     *
     * @return Null when this block should use the default translation.
     */
    static BlockState translatePrefabBlock(String name, PropertyReader p) {
        if (!EtFuturumCompat.isEnabled()) {
            return null;
        }

        String stone = name.contains("granite") ? "granite"
            : name.contains("diorite") ? "diorite" : name.contains("andesite") ? "andesite" : null;

        if (stone == null) {
            return null;
        }

        // The slabs use the order: granite, polished granite, diorite, polished diorite, andesite, polished andesite.
        int slabMeta = stone.equals("granite") ? 0 : stone.equals("diorite") ? 2 : 4;

        if (name.endsWith("_stairs")) {
            return state(stone + "_stairs", ModernBlockTranslator.getStairsMeta(p));
        } else if (name.startsWith("block_half_")) {
            return state("stone_slab_2", slabMeta | (p.is("half", "top") ? 8 : 0));
        } else if (name.startsWith("block_double_")) {
            return state("double_stone_slab_2", slabMeta);
        }

        return null;
    }
}
