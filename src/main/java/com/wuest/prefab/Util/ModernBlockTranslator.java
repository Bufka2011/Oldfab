package com.wuest.prefab.Util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import com.wuest.prefab.Prefab;

import cpw.mods.fml.common.registry.GameData;

/**
 * The structure files shipped with Prefab were created with Minecraft 1.10.2. They describe blocks by their 1.10
 * registry name and a set of block state properties. This class converts those descriptions into a 1.7.10 block and
 * metadata value.
 * <p>
 * Blocks which do not exist in 1.7.10 are substituted with the closest vanilla 1.7.10 block (for example sea lanterns
 * become glowstone and spruce doors become oak doors).
 */
public class ModernBlockTranslator {

    private static final String[] COLORS = new String[] { "white", "orange", "magenta", "light_blue", "yellow", "lime",
        "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black" };

    private static final String[] WOOD_TYPES = new String[] { "oak", "spruce", "birch", "jungle", "acacia",
        "dark_oak" };

    private static final String[] SLAB_TYPES = new String[] { "stone", "sand", "wood_old", "cobblestone", "brick",
        "stone_brick", "nether_brick", "quartz" };

    private static final String[] DOUBLE_PLANT_TYPES = new String[] { "sunflower", "syringa", "double_grass",
        "double_fern", "double_rose", "paeonia" };

    private static final String[] RED_FLOWER_TYPES = new String[] { "poppy", "blue_orchid", "allium", "houstonia",
        "red_tulip", "orange_tulip", "white_tulip", "pink_tulip", "oxeye_daisy" };

    private static final String[] RAIL_SHAPES = new String[] { "north_south", "east_west", "ascending_east",
        "ascending_west", "ascending_north", "ascending_south", "south_east", "south_west", "north_west",
        "north_east" };

    private static final Set<String> STAIRS = new HashSet<>();

    private static final Map<String, String> DIRECT_RENAMES = new HashMap<>();

    static {
        String[] stairs = new String[] { "oak_stairs", "stone_stairs", "brick_stairs", "stone_brick_stairs",
            "nether_brick_stairs", "sandstone_stairs", "spruce_stairs", "birch_stairs", "jungle_stairs",
            "quartz_stairs", "acacia_stairs", "dark_oak_stairs", "red_sandstone_stairs", "purpur_stairs" };

        for (String stair : stairs) {
            STAIRS.add(stair);
        }

        // Blocks which only changed their name.
        DIRECT_RENAMES.put("daylight_detector_inverted", "daylight_detector");
        DIRECT_RENAMES.put("magma", "netherrack");
        DIRECT_RENAMES.put("sea_lantern", "glowstone");
        DIRECT_RENAMES.put("red_sandstone", "sandstone");
        DIRECT_RENAMES.put("red_sandstone_stairs", "sandstone_stairs");
        DIRECT_RENAMES.put("purpur_stairs", "quartz_stairs");
        DIRECT_RENAMES.put("purpur_block", "quartz_block");
        DIRECT_RENAMES.put("spruce_door", "wooden_door");
        DIRECT_RENAMES.put("birch_door", "wooden_door");
        DIRECT_RENAMES.put("jungle_door", "wooden_door");
        DIRECT_RENAMES.put("acacia_door", "wooden_door");
        DIRECT_RENAMES.put("dark_oak_door", "wooden_door");
        DIRECT_RENAMES.put("spruce_fence", "fence");
        DIRECT_RENAMES.put("birch_fence", "fence");
        DIRECT_RENAMES.put("jungle_fence", "fence");
        DIRECT_RENAMES.put("acacia_fence", "fence");
        DIRECT_RENAMES.put("dark_oak_fence", "fence");
        DIRECT_RENAMES.put("spruce_fence_gate", "fence_gate");
        DIRECT_RENAMES.put("birch_fence_gate", "fence_gate");
        DIRECT_RENAMES.put("jungle_fence_gate", "fence_gate");
        DIRECT_RENAMES.put("acacia_fence_gate", "fence_gate");
        DIRECT_RENAMES.put("dark_oak_fence_gate", "fence_gate");
        DIRECT_RENAMES.put("iron_trapdoor", "trapdoor");
        DIRECT_RENAMES.put("grass_path", "grass");
        DIRECT_RENAMES.put("end_bricks", "end_stone");
        DIRECT_RENAMES.put("red_nether_brick", "nether_brick");
        DIRECT_RENAMES.put("nether_wart_block", "netherrack");
    }

    /**
     * Rotates the properties of a block. The rotation is expressed in quarter turns clockwise.
     *
     * @param properties The properties to rotate, they are modified in place.
     * @param turns      The number of clockwise quarter turns.
     */
    public static void rotate(Map<String, String> properties, int turns) {
        turns = ((turns % 4) + 4) % 4;

        if (turns == 0 || properties.isEmpty()) {
            return;
        }

        // Facing.
        String facing = properties.get("facing");

        if (facing != null) {
            if (facing.startsWith("down_") || facing.startsWith("up_")) {
                // Lever on a floor or ceiling.
                if (turns % 2 == 1) {
                    facing = facing.endsWith("_x") ? facing.substring(0, facing.length() - 1) + "z"
                        : facing.substring(0, facing.length() - 1) + "x";
                }
            } else {
                EnumFacing enumFacing = EnumFacing.byName(facing);

                if (enumFacing != null && enumFacing.isHorizontal()) {
                    for (int i = 0; i < turns; i++) {
                        enumFacing = enumFacing.rotateY();
                    }

                    facing = enumFacing.getName();
                }
            }

            properties.put("facing", facing);
        }

        // Standing signs/banners.
        String rotation = properties.get("rotation");

        if (rotation != null) {
            try {
                int value = Integer.parseInt(rotation);
                properties.put("rotation", String.valueOf((value + 4 * turns) % 16));
            } catch (NumberFormatException e) {
                // Leave the value alone.
            }
        }

        // Axis based blocks (logs, hay, bone, portals).
        String axis = properties.get("axis");

        if (axis != null && turns % 2 == 1) {
            if (axis.equals("x")) {
                properties.put("axis", "z");
            } else if (axis.equals("z")) {
                properties.put("axis", "x");
            }
        }

        // Quartz pillars.
        String variant = properties.get("variant");

        if (variant != null && turns % 2 == 1) {
            if (variant.equals("lines_x")) {
                properties.put("variant", "lines_z");
            } else if (variant.equals("lines_z")) {
                properties.put("variant", "lines_x");
            }
        }

        // Connection properties (vines, fences, walls, panes, redstone).
        if (properties.containsKey("north") && properties.containsKey("east")
            && properties.containsKey("south")
            && properties.containsKey("west")) {
            HashMap<String, String> rotated = new HashMap<>();

            for (EnumFacing horizontal : EnumFacing.HORIZONTALS) {
                EnumFacing target = horizontal;

                for (int i = 0; i < turns; i++) {
                    target = target.rotateY();
                }

                rotated.put(target.getName(), properties.get(horizontal.getName()));
            }

            properties.putAll(rotated);
        }

        // Rails.
        String shape = properties.get("shape");

        if (shape != null) {
            properties.put("shape", rotateRailShape(shape, turns));
        }
    }

    private static String rotateRailShape(String shape, int turns) {
        if (shape.equals("north_south") || shape.equals("east_west")) {
            return turns % 2 == 0 ? shape : shape.equals("north_south") ? "east_west" : "north_south";
        }

        if (shape.startsWith("ascending_")) {
            EnumFacing facing = EnumFacing.byName(shape.substring("ascending_".length()));

            if (facing != null) {
                for (int i = 0; i < turns; i++) {
                    facing = facing.rotateY();
                }

                return "ascending_" + facing.getName();
            }

            return shape;
        }

        String[] parts = shape.split("_");

        if (parts.length == 2) {
            EnumFacing first = EnumFacing.byName(parts[0]);
            EnumFacing second = EnumFacing.byName(parts[1]);

            if (first != null && second != null) {
                for (int i = 0; i < turns; i++) {
                    first = first.rotateY();
                    second = second.rotateY();
                }

                // Rail names always start with north/south.
                if (first == EnumFacing.EAST || first == EnumFacing.WEST) {
                    EnumFacing temp = first;
                    first = second;
                    second = temp;
                }

                return first.getName() + "_" + second.getName();
            }
        }

        // Stair shapes and other values are left as is.
        return shape;
    }

    /**
     * Converts a 1.10 block definition to a 1.7.10 block state.
     *
     * @param domain     The registry domain (minecraft or prefab).
     * @param name       The registry path of the block.
     * @param properties The block state properties.
     * @return The block state to place or null if this block cannot be found.
     */
    public static BlockState translate(String domain, String name, Map<String, String> properties) {
        if (domain == null || domain.isEmpty()) {
            domain = "minecraft";
        }

        if (domain.equals(Prefab.MODID)) {
            return ModernBlockTranslator.translatePrefabBlock(name, properties);
        }

        if (!domain.equals("minecraft")) {
            Block block = ModernBlockTranslator.findBlock(domain + ":" + name);
            return block == null ? null : new BlockState(block, 0);
        }

        PropertyReader p = new PropertyReader(properties);
        String originalName = name;

        if (DIRECT_RENAMES.containsKey(name)) {
            name = DIRECT_RENAMES.get(name);
        }

        if (STAIRS.contains(originalName)) {
            return new BlockState(findBlock(name), getStairsMeta(p));
        }

        switch (originalName) {
            case "air":
                return BlockState.AIR;

            case "stone": {
                String variant = p.get("variant", "stone");

                if (variant.contains("granite")) {
                    return new BlockState(Blocks.hardened_clay, 0);
                } else if (variant.contains("diorite")) {
                    return new BlockState(Blocks.quartz_block, 0);
                }

                // Andesite becomes plain stone.
                return new BlockState(Blocks.stone, 0);
            }

            case "dirt": {
                String variant = p.get("variant", "dirt");
                return new BlockState(Blocks.dirt, variant.equals("podzol") ? 2 : 0);
            }

            case "planks":
            case "double_wooden_slab":
                return new BlockState(findBlock(name), indexOf(WOOD_TYPES, p.get("variant", "oak")));

            case "wooden_slab":
                return new BlockState(
                    Blocks.wooden_slab,
                    indexOf(WOOD_TYPES, p.get("variant", "oak")) | (p.is("half", "top") ? 8 : 0));

            case "stone_slab":
                return new BlockState(
                    Blocks.stone_slab,
                    getSlabType(p.get("variant", "stone")) | (p.is("half", "top") ? 8 : 0));

            case "double_stone_slab":
                return new BlockState(
                    Blocks.double_stone_slab,
                    getSlabType(p.get("variant", "stone")) | (p.is("seamless", "true") ? 8 : 0));

            case "stone_slab2":
                return new BlockState(Blocks.stone_slab, 1 | (p.is("half", "top") ? 8 : 0));

            case "double_stone_slab2":
                return new BlockState(Blocks.double_stone_slab, 1);

            case "sand":
                return new BlockState(Blocks.sand, p.is("variant", "red_sand") ? 1 : 0);

            case "sandstone":
            case "red_sandstone": {
                String type = p.get("type", "sandstone");
                return new BlockState(
                    Blocks.sandstone,
                    type.startsWith("chiseled") ? 1 : type.startsWith("smooth") ? 2 : 0);
            }

            case "stonebrick": {
                String variant = p.get("variant", "stonebrick");
                int meta = variant.startsWith("mossy") ? 1
                    : variant.startsWith("cracked") ? 2 : variant.startsWith("chiseled") ? 3 : 0;
                return new BlockState(Blocks.stonebrick, meta);
            }

            case "log":
                return new BlockState(Blocks.log, indexOf(WOOD_TYPES, p.get("variant", "oak")) | getAxisMeta(p));

            case "log2":
                return new BlockState(
                    Blocks.log2,
                    (indexOf(WOOD_TYPES, p.get("variant", "acacia")) - 4 & 3) | getAxisMeta(p));

            case "leaves":
            case "leaves2": {
                int variant = indexOf(WOOD_TYPES, p.get("variant", originalName.equals("leaves") ? "oak" : "acacia"));

                if (originalName.equals("leaves2")) {
                    variant = (variant - 4) & 3;
                }

                int meta = variant & 3;

                if (p.is("decayable", "false")) {
                    meta |= 4;
                }

                if (p.is("check_decay", "true")) {
                    meta |= 8;
                }

                return new BlockState(findBlock(originalName), meta);
            }

            case "hay_block":
                return new BlockState(Blocks.hay_block, getAxisMeta(p));

            case "bone_block": {
                // Bone blocks don't exist in 1.7.10; use quartz pillars which look similar.
                String axis = p.get("axis", "y");
                return new BlockState(Blocks.quartz_block, axis.equals("x") ? 3 : axis.equals("z") ? 4 : 2);
            }

            case "quartz_block": {
                String variant = p.get("variant", "default");
                int meta = variant.equals("chiseled") ? 1
                    : variant.equals("lines_y") ? 2 : variant.equals("lines_x") ? 3 : variant.equals("lines_z") ? 4 : 0;
                return new BlockState(Blocks.quartz_block, meta);
            }

            case "prismarine":
                // Prismarine does not exist; use cyan hardened clay.
                return new BlockState(Blocks.stained_hardened_clay, 9);

            case "slime":
                return new BlockState(Blocks.wool, 5);

            case "wool":
            case "carpet":
            case "stained_glass":
            case "stained_glass_pane":
            case "stained_hardened_clay":
                return new BlockState(findBlock(name), indexOf(COLORS, p.get("color", "white")));

            case "concrete":
            case "concrete_powder":
                return new BlockState(Blocks.wool, indexOf(COLORS, p.get("color", "white")));

            case "tallgrass": {
                String type = p.get("type", "tall_grass");
                return new BlockState(Blocks.tallgrass, type.equals("dead_bush") ? 0 : type.equals("fern") ? 2 : 1);
            }

            case "double_plant":
                if (p.is("half", "upper")) {
                    return new BlockState(Blocks.double_plant, 8);
                }

                return new BlockState(
                    Blocks.double_plant,
                    indexOf(DOUBLE_PLANT_TYPES, p.get("variant", "double_grass")));

            case "red_flower":
                return new BlockState(Blocks.red_flower, indexOf(RED_FLOWER_TYPES, p.get("type", "poppy")));

            case "cobblestone_wall":
                return new BlockState(Blocks.cobblestone_wall, p.is("variant", "mossy_cobblestone") ? 1 : 0);

            case "torch":
            case "redstone_torch":
            case "unlit_redstone_torch":
            case "end_rod":
                return new BlockState(
                    originalName.equals("end_rod") ? Blocks.torch : findBlock(name),
                    getTorchMeta(p.get("facing", "up")));

            case "lever":
                return new BlockState(
                    Blocks.lever,
                    getLeverMeta(p.get("facing", "north")) | (p.is("powered", "true") ? 8 : 0));

            case "stone_button":
            case "wooden_button": {
                String facing = p.get("facing", "north");

                if (facing.equals("up")) {
                    // Buttons on the floor don't exist in 1.7.10. Use a pressure plate instead.
                    return new BlockState(
                        originalName.equals("stone_button") ? Blocks.stone_pressure_plate
                            : Blocks.wooden_pressure_plate,
                        0);
                } else if (facing.equals("down")) {
                    // Buttons on ceilings don't exist in 1.7.10.
                    return BlockState.AIR;
                }

                return new BlockState(findBlock(name), getTorchMeta(facing));
            }

            case "wooden_pressure_plate":
            case "stone_pressure_plate":
                return new BlockState(findBlock(name), p.is("powered", "true") ? 1 : 0);

            case "light_weighted_pressure_plate":
            case "heavy_weighted_pressure_plate":
            case "daylight_detector":
            case "daylight_detector_inverted":
            case "redstone_wire":
                return new BlockState(findBlock(name), p.getInt("power", 0));

            case "wooden_door":
            case "iron_door":
            case "spruce_door":
            case "birch_door":
            case "jungle_door":
            case "acacia_door":
            case "dark_oak_door": {
                Block door = findBlock(name);

                if (p.is("half", "upper")) {
                    return new BlockState(
                        door,
                        8 | (p.is("hinge", "right") ? 1 : 0) | (p.is("powered", "true") ? 2 : 0));
                }

                EnumFacing facing = getFacing(p, EnumFacing.NORTH);
                return new BlockState(
                    door,
                    facing.rotateY()
                        .getHorizontalIndex() | (p.is("open", "true") ? 4 : 0));
            }

            case "trapdoor":
            case "iron_trapdoor": {
                EnumFacing facing = getFacing(p, EnumFacing.NORTH);
                int meta = facing == EnumFacing.SOUTH ? 1
                    : facing == EnumFacing.WEST ? 2 : facing == EnumFacing.EAST ? 3 : 0;

                if (p.is("open", "true")) {
                    meta |= 4;
                }

                if (p.is("half", "top")) {
                    meta |= 8;
                }

                return new BlockState(Blocks.trapdoor, meta);
            }

            case "fence_gate":
            case "spruce_fence_gate":
            case "birch_fence_gate":
            case "jungle_fence_gate":
            case "acacia_fence_gate":
            case "dark_oak_fence_gate":
                return new BlockState(
                    Blocks.fence_gate,
                    getFacing(p, EnumFacing.NORTH).getHorizontalIndex() | (p.is("open", "true") ? 4 : 0));

            case "bed": {
                int meta = getFacing(p, EnumFacing.NORTH).getHorizontalIndex();

                if (p.is("part", "head")) {
                    meta |= 8;
                }

                return new BlockState(Blocks.bed, meta);
            }

            case "anvil":
                return new BlockState(
                    Blocks.anvil,
                    getFacing(p, EnumFacing.NORTH).getHorizontalIndex() | (p.getInt("damage", 0) << 2));

            case "end_portal_frame":
                return new BlockState(
                    Blocks.end_portal_frame,
                    getFacing(p, EnumFacing.NORTH).getHorizontalIndex() | (p.is("eye", "true") ? 4 : 0));

            case "pumpkin":
            case "lit_pumpkin":
                return new BlockState(findBlock(name), getFacing(p, EnumFacing.SOUTH).getHorizontalIndex());

            case "chest":
            case "trapped_chest":
            case "ender_chest":
            case "furnace":
            case "lit_furnace":
            case "ladder":
            case "wall_sign":
            case "wall_banner": {
                Block block = originalName.equals("wall_banner") ? Blocks.wall_sign : findBlock(name);
                return new BlockState(block, getFacing(p, EnumFacing.NORTH).getIndex());
            }

            case "dispenser":
            case "dropper":
                return new BlockState(
                    findBlock(name),
                    getFacing(p, EnumFacing.NORTH).getIndex() | (p.is("triggered", "true") ? 8 : 0));

            case "hopper":
                return new BlockState(
                    Blocks.hopper,
                    getFacing(p, EnumFacing.DOWN).getIndex() | (p.is("enabled", "false") ? 8 : 0));

            case "piston":
            case "sticky_piston":
                return new BlockState(
                    findBlock(name),
                    getFacing(p, EnumFacing.UP).getIndex() | (p.is("extended", "true") ? 8 : 0));

            case "piston_head":
                return new BlockState(
                    Blocks.piston_head,
                    getFacing(p, EnumFacing.UP).getIndex() | (p.is("type", "sticky") ? 8 : 0));

            case "standing_sign":
            case "standing_banner":
                return new BlockState(Blocks.standing_sign, p.getInt("rotation", 0));

            case "skull":
                return new BlockState(Blocks.skull, getFacing(p, EnumFacing.UP).getIndex());

            case "brewing_stand": {
                int meta = 0;

                for (int i = 0; i < 3; i++) {
                    if (p.is("has_bottle_" + i, "true")) {
                        meta |= 1 << i;
                    }
                }

                return new BlockState(Blocks.brewing_stand, meta);
            }

            case "cauldron":
                return new BlockState(Blocks.cauldron, p.getInt("level", 0));

            case "cactus":
            case "reeds":
            case "fire":
                return new BlockState(findBlock(name), p.getInt("age", 0));

            case "nether_wart":
            case "wheat":
            case "carrots":
            case "potatoes":
            case "melon_stem":
            case "pumpkin_stem":
                return new BlockState(findBlock(name), p.getInt("age", 0));

            case "farmland":
                return new BlockState(Blocks.farmland, p.getInt("moisture", 0));

            case "snow_layer":
                return new BlockState(Blocks.snow_layer, Math.max(0, p.getInt("layers", 1) - 1));

            case "water":
            case "flowing_water":
            case "lava":
            case "flowing_lava":
                return new BlockState(findBlock(name), p.getInt("level", 0));

            case "portal":
                return new BlockState(Blocks.portal, p.is("axis", "z") ? 2 : 1);

            case "vine": {
                int meta = 0;

                if (p.is("south", "true")) {
                    meta |= 1;
                }

                if (p.is("west", "true")) {
                    meta |= 2;
                }

                if (p.is("north", "true")) {
                    meta |= 4;
                }

                if (p.is("east", "true")) {
                    meta |= 8;
                }

                return new BlockState(Blocks.vine, meta);
            }

            case "rail":
                return new BlockState(Blocks.rail, Math.max(0, indexOf(RAIL_SHAPES, p.get("shape", "north_south"))));

            case "golden_rail":
            case "detector_rail":
            case "activator_rail":
                return new BlockState(
                    findBlock(name),
                    Math.min(5, Math.max(0, indexOf(RAIL_SHAPES, p.get("shape", "north_south"))))
                        | (p.is("powered", "true") ? 8 : 0));

            case "flower_pot": {
                NBTTagCompound tag = getFlowerPotData(p.get("contents", "empty"));
                return new BlockState(Blocks.flower_pot, 0, tag);
            }

            default: {
                Block block = findBlock(name);

                if (block == null) {
                    Prefab.LOGGER.warn("Unable to find a 1.7.10 replacement for block [minecraft:{}]", originalName);
                    return null;
                }

                return new BlockState(block, 0);
            }
        }
    }

    private static BlockState translatePrefabBlock(String name, Map<String, String> properties) {
        PropertyReader p = new PropertyReader(properties);

        switch (name) {
            case "block_glass_stairs":
                return new BlockState(findBlock(Prefab.MODID + ":block_glass_stairs"), getStairsMeta(p));

            case "block_andesite_stairs":
            case "block_diorite_stairs":
            case "block_granite_stairs":
                // Andesite, diorite and granite don't exist in 1.7.10.
                return new BlockState(
                    name.contains("diorite") ? Blocks.quartz_stairs
                        : name.contains("granite") ? Blocks.brick_stairs : Blocks.stone_brick_stairs,
                    getStairsMeta(p));

            case "block_half_glass_slab":
                return new BlockState(findBlock(Prefab.MODID + ":block_half_glass_slab"), p.is("half", "top") ? 8 : 0);

            case "block_double_glass_slab":
                return new BlockState(findBlock(Prefab.MODID + ":block_double_glass_slab"), 0);

            case "block_half_andesite_slab":
            case "block_half_diorite_slab":
            case "block_half_granite_slab":
                return new BlockState(
                    Blocks.stone_slab,
                    (name.contains("diorite") ? 7 : name.contains("granite") ? 4 : 0) | (p.is("half", "top") ? 8 : 0));

            case "block_double_andesite_slab":
            case "block_double_diorite_slab":
            case "block_double_granite_slab":
                return new BlockState(
                    Blocks.double_stone_slab,
                    name.contains("diorite") ? 7 : name.contains("granite") ? 4 : 0);

            case "block_phasing": {
                // The metadata matches the 1.10 version of this block.
                String progress = p.get("phasing_progress", "base");
                int meta = 0;

                if (progress.equals("twenty_percent")) {
                    meta = 2;
                } else if (progress.equals("forty_percent")) {
                    meta = 4;
                } else if (progress.equals("sixty_percent")) {
                    meta = 6;
                } else if (progress.equals("eighty_percent")) {
                    meta = 8;
                } else if (progress.equals("transparent")) {
                    meta = 10;
                }

                if (p.is("phasing_out", "true")) {
                    meta += 1;
                }

                return new BlockState(findBlock(Prefab.MODID + ":block_phasing"), meta);
            }

            case "block_boundary":
                return new BlockState(findBlock(Prefab.MODID + ":block_boundary"), p.is("powered", "true") ? 1 : 0);

            default: {
                Block block = findBlock(Prefab.MODID + ":" + name);
                return block == null ? null : new BlockState(block, 0);
            }
        }
    }

    private static NBTTagCompound getFlowerPotData(String contents) {
        Item item = null;
        int data = 0;

        switch (contents) {
            case "cactus":
                item = Item.getItemFromBlock(Blocks.cactus);
                break;
            case "dead_bush":
                item = Item.getItemFromBlock(Blocks.deadbush);
                break;
            case "fern":
                item = Item.getItemFromBlock(Blocks.tallgrass);
                data = 2;
                break;
            case "dandelion":
                item = Item.getItemFromBlock(Blocks.yellow_flower);
                break;
            case "mushroom_red":
                item = Item.getItemFromBlock(Blocks.red_mushroom);
                break;
            case "mushroom_brown":
                item = Item.getItemFromBlock(Blocks.brown_mushroom);
                break;
            default: {
                int flowerIndex = indexOf(
                    RED_FLOWER_TYPES,
                    contents.replace("rose", "poppy")
                        .replace("blue_orchid", "blue_orchid"));

                if (contents.endsWith("_sapling")) {
                    item = Item.getItemFromBlock(Blocks.sapling);
                    data = indexOf(WOOD_TYPES, contents.substring(0, contents.length() - "_sapling".length()));
                } else if (flowerIndex > 0 || contents.equals("rose") || contents.equals("poppy")) {
                    item = Item.getItemFromBlock(Blocks.red_flower);
                    data = Math.max(0, flowerIndex);
                }
            }
        }

        if (item == null) {
            return null;
        }

        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Item", Item.getIdFromItem(item));
        tag.setInteger("Data", data);
        return tag;
    }

    private static int getSlabType(String variant) {
        switch (variant) {
            case "sandstone":
            case "red_sandstone":
                return 1;
            case "wood_old":
                return 2;
            case "cobblestone":
                return 3;
            case "brick":
                return 4;
            case "stone_brick":
                return 5;
            case "nether_brick":
                return 6;
            case "quartz":
                return 7;
            default:
                return 0;
        }
    }

    private static int getStairsMeta(PropertyReader p) {
        EnumFacing facing = getFacing(p, EnumFacing.EAST);
        int meta = facing.isHorizontal() ? 5 - facing.getIndex() : 0;
        return meta | (p.is("half", "top") ? 4 : 0);
    }

    private static int getAxisMeta(PropertyReader p) {
        String axis = p.get("axis", "y");
        return axis.equals("x") ? 4 : axis.equals("z") ? 8 : axis.equals("none") ? 12 : 0;
    }

    private static int getTorchMeta(String facing) {
        switch (facing) {
            case "east":
                return 1;
            case "west":
                return 2;
            case "south":
                return 3;
            case "north":
                return 4;
            default:
                return 5;
        }
    }

    private static int getLeverMeta(String facing) {
        switch (facing) {
            case "down_x":
                return 0;
            case "east":
                return 1;
            case "west":
                return 2;
            case "south":
                return 3;
            case "north":
                return 4;
            case "up_z":
                return 5;
            case "up_x":
                return 6;
            default:
                return 7;
        }
    }

    private static EnumFacing getFacing(PropertyReader p, EnumFacing defaultFacing) {
        EnumFacing facing = EnumFacing.byName(p.get("facing", defaultFacing.getName()));
        return facing == null ? defaultFacing : facing;
    }

    private static int indexOf(String[] values, String value) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(value)) {
                return i;
            }
        }

        return 0;
    }

    /**
     * Finds a block by registry name.
     *
     * @param name The registry name, with or without the "minecraft:" domain.
     * @return The block or null if no block is registered with this name.
     */
    public static Block findBlock(String name) {
        if (name.indexOf(':') < 0) {
            name = "minecraft:" + name;
        }

        if (!GameData.getBlockRegistry()
            .containsKey(name)) {
            return null;
        }

        return GameData.getBlockRegistry()
            .getObject(name);
    }

    /**
     * Gets the registry name of a block.
     */
    public static String getRegistryName(Block block) {
        return GameData.getBlockRegistry()
            .getNameForObject(block);
    }

    /**
     * Gets an item by its registry name.
     */
    public static Item findItem(String name) {
        if (name.indexOf(':') < 0) {
            name = "minecraft:" + name;
        }

        Object item = Item.itemRegistry.getObject(name);
        return item instanceof Item ? (Item) item : null;
    }

    private static class PropertyReader {

        private final Map<String, String> properties;

        PropertyReader(Map<String, String> properties) {
            this.properties = properties == null ? new HashMap<String, String>() : properties;
        }

        String get(String name, String defaultValue) {
            String value = this.properties.get(name);
            return value == null ? defaultValue : value;
        }

        boolean is(String name, String value) {
            return value.equals(this.properties.get(name));
        }

        int getInt(String name, int defaultValue) {
            try {
                return Integer.parseInt(this.get(name, String.valueOf(defaultValue)));
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
    }
}
