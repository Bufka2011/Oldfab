package com.wuest.prefab;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.google.common.collect.Lists;
import com.wuest.prefab.Blocks.BlockBoundary;
import com.wuest.prefab.Blocks.BlockCompressedObsidian;
import com.wuest.prefab.Blocks.BlockCompressedStone;
import com.wuest.prefab.Blocks.BlockDoubleGlassSlab;
import com.wuest.prefab.Blocks.BlockGlassStairs;
import com.wuest.prefab.Blocks.BlockHalfGlassSlab;
import com.wuest.prefab.Blocks.BlockPaperLantern;
import com.wuest.prefab.Blocks.BlockPhasing;
import com.wuest.prefab.Config.Structures.BasicStructureConfiguration.EnumBasicStructureName;
import com.wuest.prefab.Gui.Structures.GuiAdvancedWareHouse;
import com.wuest.prefab.Gui.Structures.GuiBasicStructure;
import com.wuest.prefab.Gui.Structures.GuiChickenCoop;
import com.wuest.prefab.Gui.Structures.GuiFishPond;
import com.wuest.prefab.Gui.Structures.GuiHorseStable;
import com.wuest.prefab.Gui.Structures.GuiMonsterMasher;
import com.wuest.prefab.Gui.Structures.GuiNetherGate;
import com.wuest.prefab.Gui.Structures.GuiProduceFarm;
import com.wuest.prefab.Gui.Structures.GuiStartHouseChooser;
import com.wuest.prefab.Gui.Structures.GuiTreeFarm;
import com.wuest.prefab.Gui.Structures.GuiVillaerHouses;
import com.wuest.prefab.Gui.Structures.GuiWareHouse;
import com.wuest.prefab.Items.ItemBlockGlassSlab;
import com.wuest.prefab.Items.ItemBlockMeta;
import com.wuest.prefab.Items.ItemBundleOfTimber;
import com.wuest.prefab.Items.ItemCoilOfLanterns;
import com.wuest.prefab.Items.ItemCompressedChest;
import com.wuest.prefab.Items.ItemPalletOfBricks;
import com.wuest.prefab.Items.ItemPileOfBricks;
import com.wuest.prefab.Items.ItemStringOfLanterns;
import com.wuest.prefab.Items.ItemWarehouseUpgrade;
import com.wuest.prefab.Items.Structures.ItemAdvancedWareHouse;
import com.wuest.prefab.Items.Structures.ItemBasicStructure;
import com.wuest.prefab.Items.Structures.ItemChickenCoop;
import com.wuest.prefab.Items.Structures.ItemFishPond;
import com.wuest.prefab.Items.Structures.ItemHorseStable;
import com.wuest.prefab.Items.Structures.ItemInstantBridge;
import com.wuest.prefab.Items.Structures.ItemMonsterMasher;
import com.wuest.prefab.Items.Structures.ItemNetherGate;
import com.wuest.prefab.Items.Structures.ItemProduceFarm;
import com.wuest.prefab.Items.Structures.ItemStartHouse;
import com.wuest.prefab.Items.Structures.ItemTreeFarm;
import com.wuest.prefab.Items.Structures.ItemVillagerHouses;
import com.wuest.prefab.Items.Structures.ItemWareHouse;
import com.wuest.prefab.Proxy.Messages.ConfigSyncMessage;
import com.wuest.prefab.Proxy.Messages.Handlers.ConfigSyncHandler;
import com.wuest.prefab.Proxy.Messages.Handlers.PlayerEntityHandler;
import com.wuest.prefab.Proxy.Messages.Handlers.StructureHandler;
import com.wuest.prefab.Proxy.Messages.PlayerEntityTagMessage;
import com.wuest.prefab.Proxy.Messages.StructureTagMessage;
import com.wuest.prefab.Util.EtFuturumCompat;
import com.wuest.prefab.Util.Tuple2;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

/**
 * This is the mod registry so there is a way to get to all instances of the blocks/items created by this mod.
 * 
 * @author WuestMan
 *
 */
public class ModRegistry {

    /**
     * The ArrayList of mod registered items.
     */
    public static ArrayList<Item> ModItems = new ArrayList<Item>();

    /**
     * The ArrayList of mod registered blocks.
     */
    public static ArrayList<Block> ModBlocks = new ArrayList<Block>();

    /**
     * The hashmap of mod guis.
     */
    public static HashMap<Integer, Class> ModGuis = new HashMap<Integer, Class>();

    /**
     * The object which contains all recipes for this mod.
     */
    public static HashMap<String, Tuple2<Boolean, ArrayList<IRecipe>>> SavedRecipes = new HashMap<String, Tuple2<Boolean, ArrayList<IRecipe>>>();

    /**
     * The identifier for the WareHouse GUI.
     */
    public static final int GuiWareHouse = 1;

    /**
     * The identifier for the Chicken coop GUI.
     */
    public static final int GuiChickenCoop = 2;

    /**
     * The identifier for the Produce Farm GUI.
     */
    public static final int GuiProduceFarm = 3;

    /**
     * The identifier for the Tree Farm GUI.
     */
    public static final int GuiTreeFarm = 4;

    /**
     * The identifier for the FishPond GUI.
     */
    public static final int GuiFishPond = 5;

    /**
     * The identifier for the Starting House GUI.
     */
    public static final int GuiStartHouseChooser = 6;

    /**
     * The identifier for the Advanced WareHouse GUI.
     */
    public static final int GuiAdvancedWareHouse = 7;

    /**
     * The identifier for the Monster Masher GUI.
     */
    public static final int GuiMonsterMasher = 8;

    /**
     * The identifier for the Horse Stable GUI.
     */
    public static final int GuiHorseStable = 9;

    /**
     * The identifier for the Nether Gate GUI.
     */
    public static final int GuiNetherGate = 10;

    /**
     * The identifier for the Modular House GUI.
     */
    public static final int GuiModularHouse = 11;

    /**
     * The identifier for the Drafter GUI.
     */
    public static final int GuiDrafter = 12;

    /**
     * The identifier for the Basic structure GUI.
     */
    public static final int GuiBasicStructure = 13;

    /**
     * The identifier for the Villaer Houses GUI.
     */
    public static final int GuiVillagerHouses = 14;

    /**
     * The starting house registered item.
     * 
     * @return An instance of {@link ItemStartHouse}.
     */
    public static ItemStartHouse StartHouse() {
        return ModRegistry.GetItem(ItemStartHouse.class);
    }

    /**
     * The warehouse registered item.
     * 
     * @return An instance of {@link ItemWareHouse}.
     */
    public static ItemWareHouse WareHouse() {
        return ModRegistry.GetItem(ItemWareHouse.class);
    }

    /**
     * The advanced ware house registered item.
     * 
     * @return An instance of {@link ItemAdvancedWareHouse}.
     */
    public static ItemAdvancedWareHouse AdvancedWareHouse() {
        return ModRegistry.GetItem(ItemAdvancedWareHouse.class);
    }

    /**
     * The chicken coop registered item.
     * 
     * @return An instance of {@link ItemAdvancedWareHouse}.
     */
    public static ItemChickenCoop ChickenCoop() {
        return ModRegistry.GetItem(ItemChickenCoop.class);
    }

    /**
     * The CompressedStone registered item.
     * 
     * @return An instance of {@link ItemBlockMeta}.
     */
    public static ItemBlockMeta CompressedStoneItem() {
        return ModRegistry.GetItem(ItemBlockMeta.class);
    }

    /**
     * The CompressedStone registered Block.
     * 
     * @return An instance of {@link BlockCompressedStone}.
     */
    public static BlockCompressedStone CompressedStoneBlock() {
        return ModRegistry.GetBlock(BlockCompressedStone.class);
    }

    /**
     * The Compressed Chest registered item.
     * 
     * @return An instance of {@link ItemCompressedChest}.
     */
    public static ItemCompressedChest CompressedChestItem() {
        return ModRegistry.GetItem(ItemCompressedChest.class);
    }

    /**
     * The Produce Farm registered item.
     * 
     * @return An instance of {@link ItemProduceFarm}.
     */
    public static ItemProduceFarm ProduceFarm() {
        return ModRegistry.GetItem(ItemProduceFarm.class);
    }

    /**
     * The Tree Farm registered item.
     * 
     * @return An instance of {@link ItemTreeFarm}.
     */
    public static ItemTreeFarm TreeFarm() {
        return ModRegistry.GetItem(ItemTreeFarm.class);
    }

    /**
     * The Fish Pond registered item.
     * 
     * @return An instance of {@link ItemFishPond}.
     */
    public static ItemFishPond FishPond() {
        return ModRegistry.GetItem(ItemFishPond.class);
    }

    /**
     * The Pile of Bricks registered item.
     * 
     * @return An instance of {@link ItemPileOfBricks}.
     */
    public static ItemPileOfBricks PileOfBricks() {
        return ModRegistry.GetItem(ItemPileOfBricks.class);
    }

    /**
     * The Pallet of Bricks registered item.
     * 
     * @return An instance of {@link ItemPalletOfBricks}.
     */
    public static ItemPalletOfBricks PalletOfBricks() {
        return ModRegistry.GetItem(ItemPalletOfBricks.class);
    }

    /**
     * The Monster Masher registered item.
     * 
     * @return An instance of {@link ItemMonsterMasher}.
     */
    public static ItemMonsterMasher MonsterMasher() {
        return ModRegistry.GetItem(ItemMonsterMasher.class);
    }

    /**
     * The Warehouse Upgrade registered item.
     * 
     * @return An instance of {@link ItemWarehouseUpgrade}.
     */
    public static ItemWarehouseUpgrade WareHouseUpgrade() {
        return ModRegistry.GetItem(ItemWarehouseUpgrade.class);
    }

    /**
     * The Bundle of Timber registered item.
     * 
     * @return An instance of {@link ItemBundleOfTimber}.
     */
    public static ItemBundleOfTimber BundleOfTimber() {
        return ModRegistry.GetItem(ItemBundleOfTimber.class);
    }

    /**
     * The Horse Stable registered item.
     * 
     * @return An instance of {@link ItemHorseStable}.
     */
    public static ItemHorseStable HorseStable() {
        return ModRegistry.GetItem(ItemHorseStable.class);
    }

    /**
     * The Nether Gate registered item.
     * 
     * @return An instance of {@link ItemNetherGate}.
     */
    public static ItemNetherGate NetherGate() {
        return ModRegistry.GetItem(ItemNetherGate.class);
    }

    /**
     * The Basic Structure registered item.
     * 
     * @return An instance of {@link ItemBasicStructure}.
     */
    public static ItemBasicStructure BasicStructure() {
        return ModRegistry.GetItem(ItemBasicStructure.class);
    }

    /**
     * The Instant Bridge registered item.
     * 
     * @return An instance of {@link ItemInstantBridge}.
     */
    public static ItemInstantBridge InstantBridge() {
        return ModRegistry.GetItem(ItemInstantBridge.class);
    }

    /**
     * This method is used to get an ItemStack for compressed stone.
     * 
     * @param enumType The type of compressed stone.
     * @return An item stack with the appropriate meta data with 1 item in the stack
     */
    public static ItemStack GetCompressedStoneType(BlockCompressedStone.EnumType enumType) {
        return ModRegistry.GetCompressedStoneType(enumType, 1);
    }

    /**
     * This method is used to get an ItemStack for compressed stone.
     * 
     * @param enumType The type of compressed stone.
     * @param count    The number to have in the returned stack.
     * @return An item stack with the appropriate meta data with 1 item in the stack
     */
    public static ItemStack GetCompressedStoneType(BlockCompressedStone.EnumType enumType, int count) {
        return new ItemStack(Item.getItemFromBlock(ModRegistry.CompressedStoneBlock()), count, enumType.getMetadata());
    }

    /**
     * The Paper Lantern registered block.
     * 
     * @return An instance of {@link BlockPaperLantern}.
     */
    public static BlockPaperLantern PaperLantern() {
        return ModRegistry.GetBlock(BlockPaperLantern.class);
    }

    /**
     * The String of Lanterns registered item.
     * 
     * @return An instance of {@link ItemStringOfLanterns}.
     */
    public static ItemStringOfLanterns StringOfLanterns() {
        return ModRegistry.GetItem(ItemStringOfLanterns.class);
    }

    /**
     * The Coil of Lanterns registered item.
     * 
     * @return An instance of {@link ItemCoilOfLanterns}.
     */
    public static ItemCoilOfLanterns CoilOfLanterns() {
        return ModRegistry.GetItem(ItemCoilOfLanterns.class);
    }

    /**
     * The Compressed Obsidian registered block.
     * 
     * @return An instance of {@link CompressedObsidianBlock}.
     */
    public static BlockCompressedObsidian CompressedObsidianBlock() {
        return ModRegistry.GetBlock(BlockCompressedObsidian.class);
    }

    /**
     * The Villager Houses registered item.
     * 
     * @return An instance of {@link ItemVillagerHouses}.
     */
    public static ItemVillagerHouses VillagerHouses() {
        return ModRegistry.GetItem(ItemVillagerHouses.class);
    }

    /**
     * The Phasing Block registered Block.
     * 
     * @return An instance of {@link BlockPhasing}.
     */
    public static BlockPhasing PhasingBlock() {
        return ModRegistry.GetBlock(BlockPhasing.class);
    }

    /**
     * The Glass Stairs registered Block.
     * 
     * @return An instance of {@link BlockGlassStairs}.
     */
    public static BlockGlassStairs GlassStairs() {
        return ModRegistry.GetBlock(BlockGlassStairs.class);
    }

    /**
     * The Glass Slab registered Block.
     * 
     * @return An instance of {@link BlockHalfGlassSlab}.
     */
    public static BlockHalfGlassSlab GlassSlab() {
        return ModRegistry.GetBlock(BlockHalfGlassSlab.class);
    }

    /**
     * The Boundary Block registered Block.
     * 
     * @return An instance of {@link BlockBoundary}.
     */
    public static BlockBoundary BoundaryBlock() {
        return ModRegistry.GetBlock(BlockBoundary.class);
    }

    /**
     * Gets the item from the ModItems collections.
     * 
     * @param <T>          The type which extends item.
     * @param genericClass The class of item to get from the collection.
     * @return Null if the item could not be found otherwise the item found.
     */
    public static <T extends Item> T GetItem(Class<T> genericClass) {
        for (Item entry : ModRegistry.ModItems) {
            if (entry.getClass() == genericClass) {
                return (T) entry;
            }
        }

        return null;
    }

    /**
     * Gets the block from the ModBlockss collections.
     * 
     * @param <T>          The type which extends Block.
     * @param genericClass The class of block to get from the collection.
     * @return Null if the block could not be found otherwise the block found.
     */
    public static <T extends Block> T GetBlock(Class<T> genericClass) {
        for (Block entry : ModRegistry.ModBlocks) {
            if (entry.getClass() == genericClass) {
                return (T) entry;
            }
        }

        return null;
    }

    /**
     * Gets the gui screen for the ID and passes position data to it.
     * 
     * @param id The ID of the screen to get.
     * @param x  The X-Axis of where this screen was created from, this is used to create a BlockPos.
     * @param y  The Y-Axis of where this screen was created from, this is used to create a BlockPos.
     * @param z  The Z-Axis of where this screen was created from, this is used to create a BlockPos.
     * @return Null if the screen wasn't found, otherwise the screen found.
     */
    public static Object GetModGuiByID(int id, int x, int y, int z) {
        for (Entry<Integer, Class> entry : ModRegistry.ModGuis.entrySet()) {
            if (entry.getKey() == id) {
                try {
                    return entry.getValue()
                        .getConstructor(int.class, int.class, int.class)
                        .newInstance(x, y, z);
                } catch (InstantiationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * This is where all in-game mod components (Items, Blocks) will be registered.
     */
    public static void RegisterModComponents() {
        ModRegistry.registerItem(new ItemStartHouse("item_start_house"));
        ModRegistry.registerItem(new ItemWareHouse("item_warehouse"));
        ModRegistry.registerItem(new ItemChickenCoop("item_chicken_coop"));
        ModRegistry.registerItem(new ItemProduceFarm("item_produce_farm"));
        ModRegistry.registerItem(new ItemTreeFarm("item_tree_farm"));
        ModRegistry.registerItem(new ItemCompressedChest("item_compressed_chest"));
        ModRegistry.registerItem(new ItemPileOfBricks("item_pile_of_bricks"));
        ModRegistry.registerItem(new ItemPalletOfBricks("item_pallet_of_bricks"));
        ModRegistry.registerItem(new ItemFishPond("item_fish_pond"));
        ModRegistry.registerItem(new ItemAdvancedWareHouse("item_advanced_warehouse"));
        ModRegistry.registerItem(new ItemMonsterMasher("item_monster_masher"));
        ModRegistry.registerItem(new ItemWarehouseUpgrade("item_warehouse_upgrade"));
        ModRegistry.registerItem(new ItemBundleOfTimber("item_bundle_of_timber"));
        ModRegistry.registerItem(new ItemHorseStable("item_horse_stable"));
        ModRegistry.registerItem(new ItemNetherGate("item_nether_gate"));
        ModRegistry.registerItem(new ItemInstantBridge("item_instant_bridge"));
        // ModRegistry.registerItem(new ItemModularHouse("item_modular_house"));
        ModRegistry.registerItem(new ItemStringOfLanterns("item_string_of_lanterns"));
        ModRegistry.registerItem(new ItemCoilOfLanterns("item_coil_of_lanterns"));
        // ModRegistry.registerItem(new ItemBogus("item_bogus"));

        // Register all the basic structures here. The resource location is used for the item models and textures.
        // Only the first one in this list should have the last variable set to true.
        ModRegistry.registerItem(new ItemBasicStructure("item_basic_structure"));

        // Create/register the item block with this block as it's needed due to this being a meta data block.
        ModRegistry.registerBlock(new BlockCompressedStone(), "block_compressed_stone", ItemBlockMeta.class);
        ModRegistry.registerBlock(new BlockPaperLantern("block_paper_lantern"), "block_paper_lantern");
        ModRegistry.registerBlock(new BlockCompressedObsidian(), "block_compressed_obsidian", ItemBlockMeta.class);
        ModRegistry.registerItem(new ItemVillagerHouses("item_villager_houses"));
        ModRegistry.registerBlock(new BlockPhasing("block_phasing"), "block_phasing");
        ModRegistry.registerBlock(new BlockBoundary("block_boundary"), "block_boundary");
        ModRegistry.registerBlock(new BlockGlassStairs("block_glass_stairs"), "block_glass_stairs");

        // Glass Slab.
        BlockHalfGlassSlab registeredHalfGlassBlock = new BlockHalfGlassSlab();
        BlockDoubleGlassSlab registeredDoubleGlassSlab = new BlockDoubleGlassSlab();
        ModRegistry.registerBlock(
            registeredHalfGlassBlock,
            "block_half_glass_slab",
            ItemBlockGlassSlab.class,
            registeredHalfGlassBlock,
            registeredDoubleGlassSlab,
            false);
        ModRegistry.registerBlock(
            registeredDoubleGlassSlab,
            "block_double_glass_slab",
            ItemBlockGlassSlab.class,
            registeredHalfGlassBlock,
            registeredDoubleGlassSlab,
            true);
    }

    /**
     * This is where the mod recipes are registered.
     */
    public static void RegisterRecipes() {
        // Compressed Stone.
        ModRegistry.addShapedRecipe(
            "Compressed Stone",
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.COMPRESSED_STONE),
            "xxx",
            "xxx",
            "xxx",
            'x',
            new ItemStack(Blocks.stone, 1, OreDictionary.WILDCARD_VALUE));

        ModRegistry.addShapedRecipe(
            "Compressed Stone",
            new ItemStack(Item.getItemFromBlock(Blocks.stone), 9),
            "x",
            'x',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.COMPRESSED_STONE));

        // Double Compressed Stone.
        ModRegistry.addShapedRecipe(
            "Compressed Stone",
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE),
            "xxx",
            "xxx",
            "xxx",
            'x',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.COMPRESSED_STONE));

        ModRegistry.addShapedRecipe(
            "Compressed Stone",
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.COMPRESSED_STONE, 9),
            "x",
            'x',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE));

        // Triple Compressed Stone.
        ModRegistry.addShapedRecipe(
            "Compressed Stone",
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.TRIPLE_COMPRESSED_STONE),
            "xxx",
            "xxx",
            "xxx",
            'x',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE));

        ModRegistry.addShapedRecipe(
            "Compressed Stone",
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE, 9),
            "x",
            'x',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.TRIPLE_COMPRESSED_STONE));

        // Compressed Glowstone.
        ModRegistry.addShapedRecipe(
            "Compressed Glowstone",
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.COMPRESSED_GLOWSTONE),
            "xxx",
            "xxx",
            "xxx",
            'x',
            Item.getItemFromBlock(Blocks.glowstone));

        ModRegistry.addShapedRecipe(
            "Compressed Glowstone",
            new ItemStack(Item.getItemFromBlock(Blocks.glowstone), 9),
            "x",
            'x',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.COMPRESSED_GLOWSTONE));

        // Double Compressed Glowstone.
        ModRegistry.addShapedRecipe(
            "Compressed Glowstone",
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_GLOWSTONE),
            "xxx",
            "xxx",
            "xxx",
            'x',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.COMPRESSED_GLOWSTONE));

        ModRegistry.addShapedRecipe(
            "Compressed Glowstone",
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.COMPRESSED_GLOWSTONE, 9),
            "x",
            'x',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_GLOWSTONE));

        // Compressed Dirt
        ModRegistry.addShapedRecipe(
            "Compressed Dirt",
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.COMPRESSED_DIRT),
            "xxx",
            "xxx",
            "xxx",
            'x',
            Item.getItemFromBlock(Blocks.dirt));

        ModRegistry.addShapedRecipe(
            "Compressed Dirt",
            new ItemStack(Item.getItemFromBlock(Blocks.dirt), 9),
            "x",
            'x',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.COMPRESSED_DIRT));

        // Double Compressed Dirt
        ModRegistry.addShapedRecipe(
            "Compressed Dirt",
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_DIRT),
            "xxx",
            "xxx",
            "xxx",
            'x',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.COMPRESSED_DIRT));

        ModRegistry.addShapedRecipe(
            "Compressed Dirt",
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.COMPRESSED_DIRT, 9),
            "x",
            'x',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_DIRT));

        // Compressed Chest
        ModRegistry.addShapedRecipe(
            "Compressed Chest",
            new ItemStack(ModRegistry.CompressedChestItem()),
            "xxx",
            "xyx",
            "xxx",
            'x',
            Item.getItemFromBlock(Blocks.chest),
            'y',
            Item.getItemFromBlock(Blocks.ender_chest));

        // Pile of Bricks
        ModRegistry.addShapedRecipe(
            "Pile of Bricks",
            new ItemStack(ModRegistry.PileOfBricks()),
            "xxx",
            "xxx",
            "xxx",
            'x',
            Items.brick);

        ModRegistry
            .addShapedRecipe("Pile of Bricks", new ItemStack(Items.brick, 9), "x", 'x', ModRegistry.PileOfBricks());

        // Pallet of Bricks
        ModRegistry.addShapedRecipe(
            "Pile of Bricks",
            new ItemStack(ModRegistry.PalletOfBricks()),
            "xxx",
            "xxx",
            "xxx",
            'x',
            ModRegistry.PileOfBricks());

        ModRegistry.addShapedRecipe(
            "Pile of Bricks",
            new ItemStack(ModRegistry.PileOfBricks(), 9),
            "x",
            'x',
            ModRegistry.PalletOfBricks());

        // Warehouse
        ModRegistry.addShapedRecipe(
            "Warehouse",
            new ItemStack(ModRegistry.WareHouse()),
            "x x",
            "xyx",
            "zaz",
            'x',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE),
            'y',
            ModRegistry.CompressedChestItem(),
            'z',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_GLOWSTONE),
            'a',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.TRIPLE_COMPRESSED_STONE));

        ModRegistry.addShapedRecipe(
            "Warehouse",
            new ItemStack(ModRegistry.WareHouse()),
            "x x",
            "xyx",
            "zaz",
            'x',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE),
            'y',
            ModRegistry.CompressedChestItem(),
            'z',
            ModRegistry.CoilOfLanterns(),
            'a',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.TRIPLE_COMPRESSED_STONE));

        // Produce Farm.
        ModRegistry.addShapedRecipe(
            "Produce Farm",
            new ItemStack(ModRegistry.ProduceFarm()),
            "aba",
            "cdc",
            "aba",
            'a',
            ModRegistry.PalletOfBricks(),
            'b',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_DIRT),
            'c',
            Items.water_bucket,
            'd',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_GLOWSTONE));

        ModRegistry.addShapedRecipe(
            "Produce Farm",
            new ItemStack(ModRegistry.ProduceFarm()),
            "aba",
            "cdc",
            "aba",
            'a',
            ModRegistry.PalletOfBricks(),
            'b',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_DIRT),
            'c',
            Items.water_bucket,
            'd',
            ModRegistry.CoilOfLanterns());

        // Tree Farm/Park
        ModRegistry.addShapedRecipe(
            "Tree Farm",
            new ItemStack(ModRegistry.TreeFarm()),
            "aba",
            "cdc",
            "aba",
            'a',
            ModRegistry.PalletOfBricks(),
            'b',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_DIRT),
            'c',
            Items.flower_pot,
            'd',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_GLOWSTONE));

        ModRegistry.addShapedRecipe(
            "Tree Farm",
            new ItemStack(ModRegistry.TreeFarm()),
            "aba",
            "cdc",
            "aba",
            'a',
            ModRegistry.PalletOfBricks(),
            'b',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_DIRT),
            'c',
            Items.flower_pot,
            'd',
            ModRegistry.CoilOfLanterns());

        // Chicken Coop
        ModRegistry.addShapedRecipe(
            "Chicken Coop",
            new ItemStack(ModRegistry.ChickenCoop()),
            "eee",
            "aba",
            "cdc",
            'a',
            new ItemStack(Item.getItemFromBlock(Blocks.log), 1, 1),
            'b',
            Items.egg,
            'c',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.COMPRESSED_DIRT),
            'd',
            Item.getItemFromBlock(Blocks.hay_block),
            'e',
            Item.getItemFromBlock(Blocks.brick_block));

        // Fish farm.
        ModRegistry.addShapedRecipe(
            "Fish Farm",
            new ItemStack(ModRegistry.FishPond()),
            "abc",
            "ded",
            "fgf",
            'a',
            new ItemStack(Item.getItemFromBlock(Blocks.tallgrass), 1, 1),
            'b',
            new ItemStack(Items.reeds),
            'c',
            new ItemStack(Item.getItemFromBlock(Blocks.sapling), 1, 0),
            'd',
            new ItemStack(Items.water_bucket),
            'e',
            new ItemStack(Items.fishing_rod, 1, 0),
            'f',
            new ItemStack(Item.getItemFromBlock(Blocks.sand)),
            'g',
            new ItemStack(Items.fish, 1, OreDictionary.WILDCARD_VALUE));

        // Warehouse Upgrade
        ModRegistry.addShapedRecipe(
            "Warehouse Upgrade",
            new ItemStack(ModRegistry.WareHouseUpgrade()),
            "aba",
            "cdc",
            "aba",
            'a',
            Item.getItemFromBlock(Blocks.bookshelf),
            'b',
            Items.brewing_stand,
            'c',
            Item.getItemFromBlock(Blocks.enchanting_table),
            'd',
            Item.getItemFromBlock(Blocks.anvil));

        // Advanced Warehouse
        ModRegistry.addShapedRecipe(
            "Advanced Warehouse",
            new ItemStack(ModRegistry.AdvancedWareHouse()),
            "xbx",
            "xyx",
            "zaz",
            'x',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE),
            'y',
            ModRegistry.CompressedChestItem(),
            'z',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_GLOWSTONE),
            'a',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.TRIPLE_COMPRESSED_STONE),
            'b',
            ModRegistry.WareHouseUpgrade());

        ModRegistry.addShapedRecipe(
            "Advanced Warehouse",
            new ItemStack(ModRegistry.AdvancedWareHouse()),
            "xbx",
            "xyx",
            "zaz",
            'x',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE),
            'y',
            ModRegistry.CompressedChestItem(),
            'z',
            ModRegistry.CoilOfLanterns(),
            'a',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.TRIPLE_COMPRESSED_STONE),
            'b',
            ModRegistry.WareHouseUpgrade());

        // Monster Masher.
        ModRegistry.addShapedRecipe(
            "Monster Masher",
            new ItemStack(ModRegistry.MonsterMasher()),
            "abc",
            "ede",
            "fgh",
            'a',
            new ItemStack(Items.skull, 1, 2),
            'b',
            Item.getItemFromBlock(Blocks.redstone_block),
            'c',
            new ItemStack(Items.skull, 1, 0),
            'e',
            Item.getItemFromBlock(Blocks.iron_bars),
            'd',
            ModRegistry.CompressedChestItem(),
            'f',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE),
            'g',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.TRIPLE_COMPRESSED_STONE),
            'h',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_GLOWSTONE));

        // Planks to addShapedRecipe of timber.
        IRecipe currentRecipe = new ShapedOreRecipe(ModRegistry.BundleOfTimber(), "aaa", "aaa", "aaa", 'a', "logWood");

        GameRegistry.addRecipe(currentRecipe);

        ModRegistry.SaveModRecipe("Bundle of Timber", currentRecipe);

        // Bundle of timber to oak planks.
        ModRegistry.addShapedRecipe(
            "Bundle of Timber",
            new ItemStack(Item.getItemFromBlock(Blocks.log), 9),
            "a",
            'a',
            ModRegistry.BundleOfTimber());

        // Horse Stable.

        ModRegistry.addShapedRecipe(
            "Horse Stable",
            new ItemStack(ModRegistry.HorseStable()),
            "aaa",
            "bcb",
            "ded",
            'a',
            Item.getItemFromBlock(Blocks.brick_block),
            'b',
            ModRegistry.BundleOfTimber(),
            'c',
            Item.getItemFromBlock(Blocks.hay_block),
            'd',
            Item.getItemFromBlock(Blocks.fence),
            'e',
            Item.getItemFromBlock(Blocks.fence_gate));

        // Nether Gate
        ModRegistry.addShapedRecipe(
            "Nether Gate",
            new ItemStack(ModRegistry.NetherGate()),
            "aba",
            "bcb",
            "aba",
            'a',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE),
            'b',
            Item.getItemFromBlock(Blocks.obsidian),
            'c',
            Items.flint_and_steel);

        // Advanced Chicken Coop
        ItemStack result = ItemBasicStructure.getStack(EnumBasicStructureName.AdavancedCoop);

        ModRegistry.addShapelessRecipe(
            "Advanced Chicken Coop",
            result,
            ModRegistry.ChickenCoop(),
            ModRegistry.PalletOfBricks());

        // Advanced Horse Stable
        result = ItemBasicStructure.getStack(EnumBasicStructureName.AdvancedHorseStable);

        ModRegistry.addShapelessRecipe(
            "Advanced Horse Stable",
            result,
            ModRegistry.HorseStable(),
            ModRegistry.HorseStable(),
            ModRegistry.HorseStable(),
            ModRegistry.HorseStable(),
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_GLOWSTONE));

        ModRegistry.addShapelessRecipe(
            "Advanced Horse Stable",
            result,
            ModRegistry.HorseStable(),
            ModRegistry.HorseStable(),
            ModRegistry.HorseStable(),
            ModRegistry.HorseStable(),
            ModRegistry.CoilOfLanterns());

        // Barn
        result = ItemBasicStructure.getStack(EnumBasicStructureName.Barn);

        ModRegistry.addShapedRecipe(
            "Barn",
            result,
            "aba",
            "ccc",
            "cdc",
            'a',
            ModRegistry.PalletOfBricks(),
            'b',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.COMPRESSED_GLOWSTONE),
            'c',
            ModRegistry.BundleOfTimber(),
            'd',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_DIRT));

        // Machinery Tower
        result = ItemBasicStructure.getStack(EnumBasicStructureName.MachineryTower);

        ModRegistry.addShapedRecipe(
            "Machinery Tower",
            result,
            "aaa",
            " b ",
            " c ",
            'a',
            Item.getItemFromBlock(Blocks.golden_rail),
            'b',
            ModRegistry.StringOfLanterns(),
            'c',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.TRIPLE_COMPRESSED_STONE));

        // Defense Bunker
        result = ItemBasicStructure.getStack(EnumBasicStructureName.DefenseBunker);

        ModRegistry.addShapedRecipe(
            "Defense Bunker",
            result,
            "dad",
            "bcb",
            "ddd",
            'a',
            ModRegistry.CoilOfLanterns(),
            'b',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.TRIPLE_COMPRESSED_STONE),
            'c',
            new ItemStack(
                Item.getItemFromBlock(ModRegistry.CompressedObsidianBlock()),
                1,
                BlockCompressedObsidian.EnumType.DOUBLE_COMPRESSED_OBSIDIAN.getMetadata()),
            'd',
            Item.getItemFromBlock(Blocks.iron_block));

        // Mineshaft Entrance
        result = ItemBasicStructure.getStack(EnumBasicStructureName.MineshaftEntrance);

        ModRegistry.addShapedRecipe(
            "Mineshaft Entrance",
            result,
            "dad",
            "bcb",
            "aaa",
            'a',
            ModRegistry.BundleOfTimber(),
            'b',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.COMPRESSED_STONE),
            'c',
            Items.bed,
            'd',
            Items.iron_pickaxe);

        // Ender Gateway
        result = ItemBasicStructure.getStack(EnumBasicStructureName.EnderGateway);

        ModRegistry.addShapedRecipe(
            "Ender Gateway",
            result,
            "bdb",
            "dcd",
            "bab",
            'a',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.TRIPLE_COMPRESSED_STONE),
            'b',
            new ItemStack(Items.ender_eye),
            'c',
            new ItemStack(
                Item.getItemFromBlock(ModRegistry.CompressedObsidianBlock()),
                1,
                BlockCompressedObsidian.EnumType.DOUBLE_COMPRESSED_OBSIDIAN.getMetadata()),
            'd',
            Item.getItemFromBlock(Blocks.quartz_block));

        // Aqua base
        result = ItemBasicStructure.getStack(EnumBasicStructureName.AquaBase);

        ModRegistry.addShapedRecipe(
            "Aqua Base",
            result,
            "xxx",
            "yzy",
            "yay",
            'x',
            new ItemStack(Item.getItemFromBlock(Blocks.glass)),
            'y',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE),
            'z',
            new ItemStack(Item.getItemFromBlock(Blocks.glowstone)),
            'a',
            ModRegistry.CoilOfLanterns());

        // Grassy Plains
        result = ItemBasicStructure.getStack(EnumBasicStructureName.GrassyPlain);

        ModRegistry.addShapedRecipe(
            "Grassy Plains",
            result,
            "   ",
            "yyy",
            "xxx",
            'x',
            new ItemStack(Item.getItemFromBlock(Blocks.dirt)),
            'y',
            new ItemStack(Item.getItemFromBlock(Blocks.tallgrass), 1, 1));

        // Magic Temple
        result = ItemBasicStructure.getStack(EnumBasicStructureName.MagicTemple);

        ModRegistry.addShapedRecipe(
            "Magic Temple",
            result,
            "aaa",
            "cbd",
            "aaa",
            'a',
            new ItemStack(ModRegistry.BundleOfTimber()),
            'b',
            new ItemStack(Item.getItemFromBlock(Blocks.enchanting_table)),
            'c',
            new ItemStack(Items.brewing_stand),
            'd',
            new ItemStack(Items.blaze_rod));

        // Greenhouse
        result = ItemBasicStructure.getStack(EnumBasicStructureName.GreenHouse);

        ModRegistry.addShapedRecipe(
            "Green House",
            result,
            "bcb",
            "bab",
            "ded",
            'a',
            new ItemStack(Items.water_bucket),
            'b',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE),
            'c',
            new ItemStack(Item.getItemFromBlock(Blocks.glass_pane)),
            'd',
            new ItemStack(ModRegistry.StringOfLanterns()),
            'e',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_DIRT));

        // Instant Bridge.
        ModRegistry.addShapedRecipe(
            "Instant Bridge",
            new ItemStack(ModRegistry.InstantBridge()),
            "bab",
            "bab",
            "bab",
            'a',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE),
            'b',
            Item.getItemFromBlock(Blocks.torch));

        // Paper Lantern.
        ModRegistry.addShapelessRecipe(
            "Paper Lantern",
            new ItemStack(Item.getItemFromBlock(ModRegistry.PaperLantern())),
            Items.stick,
            Items.paper,
            Item.getItemFromBlock(Blocks.torch));

        // String of lanterns (compressed paper lanterns).
        ModRegistry.addShapedRecipe(
            "Paper Lantern",
            new ItemStack(ModRegistry.StringOfLanterns()),
            "aaa",
            "aaa",
            "aaa",
            'a',
            Item.getItemFromBlock(ModRegistry.PaperLantern()));

        ModRegistry.addShapelessRecipe(
            "Paper Lantern",
            new ItemStack(Item.getItemFromBlock(ModRegistry.PaperLantern()), 9),
            ModRegistry.StringOfLanterns());

        // Coil of lanterns (double compressed paper lanterns).
        ModRegistry.addShapedRecipe(
            "Paper Lantern",
            new ItemStack(ModRegistry.CoilOfLanterns()),
            "aaa",
            "aaa",
            "aaa",
            'a',
            ModRegistry.StringOfLanterns());

        ModRegistry.addShapelessRecipe(
            "Paper Lantern",
            new ItemStack(ModRegistry.StringOfLanterns(), 9),
            ModRegistry.CoilOfLanterns());

        // Compressed Obsidian.
        ModRegistry.addShapedRecipe(
            "Compressed Obsidian",
            new ItemStack(
                Item.getItemFromBlock(ModRegistry.CompressedObsidianBlock()),
                1,
                BlockCompressedObsidian.EnumType.COMPRESSED_OBSIDIAN.getMetadata()),
            "aaa",
            "aaa",
            "aaa",
            'a',
            Item.getItemFromBlock(Blocks.obsidian));

        ModRegistry.addShapelessRecipe(
            "Compressed Obsidian",
            new ItemStack(Item.getItemFromBlock(Blocks.obsidian), 9),
            new ItemStack(
                Item.getItemFromBlock(ModRegistry.CompressedObsidianBlock()),
                1,
                BlockCompressedObsidian.EnumType.COMPRESSED_OBSIDIAN.getMetadata()));

        // Double compressed obsidian.
        ModRegistry.addShapedRecipe(
            "Compressed Obsidian",
            new ItemStack(
                Item.getItemFromBlock(ModRegistry.CompressedObsidianBlock()),
                1,
                BlockCompressedObsidian.EnumType.DOUBLE_COMPRESSED_OBSIDIAN.getMetadata()),
            "aaa",
            "aaa",
            "aaa",
            'a',
            new ItemStack(
                Item.getItemFromBlock(ModRegistry.CompressedObsidianBlock()),
                1,
                BlockCompressedObsidian.EnumType.COMPRESSED_OBSIDIAN.getMetadata()));

        ModRegistry.addShapelessRecipe(
            "Compressed Obsidian",
            new ItemStack(
                Item.getItemFromBlock(ModRegistry.CompressedObsidianBlock()),
                9,
                BlockCompressedObsidian.EnumType.COMPRESSED_OBSIDIAN.getMetadata()),
            new ItemStack(
                Item.getItemFromBlock(ModRegistry.CompressedObsidianBlock()),
                1,
                BlockCompressedObsidian.EnumType.DOUBLE_COMPRESSED_OBSIDIAN.getMetadata()));

        // Villager Houses.
        ModRegistry.addShapedRecipe(
            "Villager Houses",
            new ItemStack(ModRegistry.VillagerHouses()),
            "aaa",
            "aaa",
            "bbb",
            'a',
            ModRegistry.BundleOfTimber(),
            'b',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.COMPRESSED_STONE));

        // Phasic Block
        ModRegistry.addShapedRecipe(
            "Phasic Block",
            new ItemStack(ModRegistry.PhasingBlock()),
            "aaa",
            "aba",
            "aaa",
            'a',
            Item.getItemFromBlock(Blocks.stone),
            'b',
            Items.ender_pearl);

        // Smart Glass
        ModRegistry.addShapedRecipe(
            "Smart Glass",
            new ItemStack(ModRegistry.BoundaryBlock()),
            "aaa",
            "aba",
            "aaa",
            'a',
            Item.getItemFromBlock(Blocks.glass),
            'b',
            Items.ender_pearl);

        // Starting House.
        ModRegistry.addShapedRecipe(
            "Starting House",
            new ItemStack(ModRegistry.StartHouse()),
            "abc",
            "ded",
            "fff",
            'a',
            Item.getItemFromBlock(Blocks.crafting_table),
            'b',
            Items.clock,
            'c',
            Item.getItemFromBlock(Blocks.furnace),
            'd',
            ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.COMPRESSED_STONE),
            'e',
            Items.bed,
            'f',
            ModRegistry.BundleOfTimber());

        // Glass Stairs
        ModRegistry.addShapedRecipe(
            "Glass Stairs",
            new ItemStack(ModRegistry.GlassStairs(), 4),
            "a  ",
            "aa ",
            "aaa",
            'a',
            Item.getItemFromBlock(Blocks.glass));

        // Glass Slabs
        ModRegistry.addShapedRecipe(
            "Glass Slabs",
            new ItemStack(ModRegistry.GlassSlab(), 2),
            "a",
            "a",
            'a',
            Item.getItemFromBlock(Blocks.glass));

        ModRegistry.RegisterEtFuturumRecipes();
    }

    /**
     * Adds the original 1.10 recipes which use blocks from Et Futurum Requiem, when that mod is installed.
     */
    public static void RegisterEtFuturumRecipes() {
        if (!EtFuturumCompat.isEnabled()) {
            return;
        }

        String[] woodTypes = new String[] { "spruce", "birch", "jungle", "acacia", "dark_oak" };

        for (String wood : woodTypes) {
            Block fence = EtFuturumCompat.getBlock("fence_" + wood);
            Block fenceGate = EtFuturumCompat.getBlock("fence_gate_" + wood);

            if (fence != null && fenceGate != null) {
                ModRegistry.addShapedRecipe(
                    "Horse Stable",
                    new ItemStack(ModRegistry.HorseStable()),
                    "aaa",
                    "bcb",
                    "ded",
                    'a',
                    Item.getItemFromBlock(Blocks.brick_block),
                    'b',
                    ModRegistry.BundleOfTimber(),
                    'c',
                    Item.getItemFromBlock(Blocks.hay_block),
                    'd',
                    new ItemStack(fence),
                    'e',
                    new ItemStack(fenceGate));
            }
        }

        Block prismarine = EtFuturumCompat.getBlock("prismarine_block");

        if (prismarine != null) {
            ModRegistry.addShapedRecipe(
                "Ender Gateway",
                ItemBasicStructure.getStack(EnumBasicStructureName.EnderGateway),
                "bdb",
                "dcd",
                "bab",
                'a',
                ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.TRIPLE_COMPRESSED_STONE),
                'b',
                new ItemStack(prismarine, 1, 2),
                'c',
                new ItemStack(
                    Item.getItemFromBlock(ModRegistry.CompressedObsidianBlock()),
                    1,
                    BlockCompressedObsidian.EnumType.DOUBLE_COMPRESSED_OBSIDIAN.getMetadata()),
                'd',
                Item.getItemFromBlock(Blocks.quartz_block));
        }

        Block seaLantern = EtFuturumCompat.getBlock("sea_lantern");

        if (seaLantern != null) {
            ModRegistry.addShapedRecipe(
                "Aqua Base",
                ItemBasicStructure.getStack(EnumBasicStructureName.AquaBase),
                "xxx",
                "yzy",
                "yay",
                'x',
                new ItemStack(Item.getItemFromBlock(Blocks.glass)),
                'y',
                ModRegistry.GetCompressedStoneType(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE),
                'z',
                new ItemStack(seaLantern),
                'a',
                ModRegistry.CoilOfLanterns());
        }
    }

    /**
     * Saves the mod recipe to the field.
     * 
     * @param displayName The name of the recipe.
     * @param recipe      The recipe to save.
     */
    public static void SaveModRecipe(String displayName, IRecipe recipe) {
        // Make a hashmap with a string key and an arraylist of tuples for the value. The tuple will be of IRecipe and
        // boolean
        if (ModRegistry.SavedRecipes.containsKey(displayName)) {
            Tuple2<Boolean, ArrayList<IRecipe>> recipes = ModRegistry.SavedRecipes.get(displayName);
            recipes._2.add(recipe);
        } else {
            Tuple2<Boolean, ArrayList<IRecipe>> recipes = new Tuple2<Boolean, ArrayList<IRecipe>>(
                true,
                new ArrayList<IRecipe>());
            recipes._2.add(recipe);
            ModRegistry.SavedRecipes.put(displayName, recipes);
        }
    }

    /**
     * Adds a shaped recipe to the game registry/mod registry.
     * 
     * @param displayName      The name of the recipe.
     * @param stack            The output of the recipel
     * @param recipeComponents The components of the recipe.
     * @return An IRecipe to be used.
     */
    public static IRecipe addShapedRecipe(String displayName, ItemStack stack, Object... recipeComponents) {
        IRecipe recipe = GameRegistry.addShapedRecipe(stack, recipeComponents);

        ModRegistry.SaveModRecipe(displayName, recipe);

        return recipe;
    }

    /**
     * Adds a shapeless crafting recipe to the the game.
     * 
     * @param displayName      the display name for this recipe.
     * @param stack            The output of this recipe.
     * @param recipeComponents The various items/blocks for this recipe.
     * @return An IRecipe which was registered into the game registry.
     */
    public static IRecipe addShapelessRecipe(String displayName, ItemStack stack, Object... recipeComponents) {
        List<ItemStack> list = Lists.<ItemStack>newArrayList();

        for (Object object : recipeComponents) {
            if (object instanceof ItemStack) {
                list.add(((ItemStack) object).copy());
            } else if (object instanceof Item) {
                list.add(new ItemStack((Item) object));
            } else {
                if (!(object instanceof Block)) {
                    throw new IllegalArgumentException(
                        "Invalid shapeless recipe: unknown type " + object.getClass()
                            .getName() + "!");
                }

                list.add(new ItemStack((Block) object));
            }
        }

        ShapelessRecipes returnValue = new ShapelessRecipes(stack, list);
        GameRegistry.addRecipe(returnValue);

        ModRegistry.SaveModRecipe(displayName, returnValue);

        return returnValue;
    }

    /**
     * This is where the mod messages are registered.
     */
    public static void RegisterMessages() {
        Prefab.network.registerMessage(ConfigSyncHandler.class, ConfigSyncMessage.class, 1, Side.CLIENT);

        Prefab.network.registerMessage(StructureHandler.class, StructureTagMessage.class, 2, Side.SERVER);

        Prefab.network.registerMessage(PlayerEntityHandler.class, PlayerEntityTagMessage.class, 3, Side.CLIENT);
    }

    /**
     * Holds the information needed to register a block with the game registry.
     */
    private static class BlockRegistration {

        public final Block block;
        public final String name;
        public final Class<? extends ItemBlock> itemBlockClass;
        public final Object[] itemBlockArgs;

        public BlockRegistration(Block block, String name, Class<? extends ItemBlock> itemBlockClass,
            Object[] itemBlockArgs) {
            this.block = block;
            this.name = name;
            this.itemBlockClass = itemBlockClass;
            this.itemBlockArgs = itemBlockArgs;
        }
    }

    private static final ArrayList<BlockRegistration> blockRegistrations = new ArrayList<BlockRegistration>();

    private static final HashMap<Item, String> itemNames = new HashMap<Item, String>();

    private static boolean componentsRegistered = false;

    /**
     * Registers all of the blocks and items with the game registry. This must be called during pre-init.
     */
    public static void RegisterWithGameRegistry() {
        if (ModRegistry.componentsRegistered) {
            return;
        }

        ModRegistry.componentsRegistered = true;

        for (Item item : new ArrayList<Item>(ModRegistry.ModItems)) {
            GameRegistry.registerItem(item, ModRegistry.itemNames.get(item));
        }

        for (BlockRegistration registration : ModRegistry.blockRegistrations) {
            GameRegistry.registerBlock(
                registration.block,
                registration.itemBlockClass,
                registration.name,
                registration.itemBlockArgs);
            Item itemBlock = Item.getItemFromBlock(registration.block);

            if (itemBlock != null && !ModRegistry.ModItems.contains(itemBlock)) {
                ModRegistry.ModItems.add(itemBlock);
            }
        }
    }

    /**
     * Register an Item
     *
     * @param item The Item instance
     * @param <T>  The Item type
     * @return The Item instance
     */
    public static <T extends Item> T registerItem(T item) {
        ModRegistry.ModItems.add(item);
        return item;
    }

    /**
     * Registers a block in the game registry with a default item block.
     */
    public static <T extends Block> T registerBlock(T block, String name) {
        return ModRegistry.registerBlock(block, name, ItemBlock.class);
    }

    /**
     * Registers a block in the game registry.
     * 
     * @param block          The block to register.
     * @param name           The registry name.
     * @param itemBlockClass The item block class to create for this block.
     * @param itemBlockArgs  Additional constructor arguments for the item block.
     * @return The block which was registered.
     */
    public static <T extends Block> T registerBlock(T block, String name, Class<? extends ItemBlock> itemBlockClass,
        Object... itemBlockArgs) {
        ModRegistry.setBlockName(block, name);
        ModRegistry.ModBlocks.add(block);
        ModRegistry.blockRegistrations.add(new BlockRegistration(block, name, itemBlockClass, itemBlockArgs));
        return block;
    }

    /**
     * Set the registry name of {@code item} to {@code itemName} and the un-localised name to the full registry name.
     *
     * @param item     The item
     * @param itemName The item's name
     */
    public static void setItemName(Item item, String itemName) {
        if (itemName != null) {
            ModRegistry.itemNames.put(item, itemName);
            item.setUnlocalizedName(Prefab.MODID + ":" + itemName);
            item.setTextureName(Prefab.MODID + ":" + itemName);
        }
    }

    /**
     * Set the un-localised name of {@code block} to the full registry name.
     *
     * @param block     The block
     * @param blockName The block's name
     */
    public static void setBlockName(Block block, String blockName) {
        block.setBlockName(Prefab.MODID + ":" + blockName);
    }

    /**
     * Adds all of the Mod Guis to the HasMap.
     */
    public static void AddGuis() {
        ModRegistry.ModGuis.put(ModRegistry.GuiWareHouse, GuiWareHouse.class);
        ModRegistry.ModGuis.put(ModRegistry.GuiChickenCoop, GuiChickenCoop.class);
        ModRegistry.ModGuis.put(ModRegistry.GuiProduceFarm, GuiProduceFarm.class);
        ModRegistry.ModGuis.put(ModRegistry.GuiTreeFarm, GuiTreeFarm.class);
        ModRegistry.ModGuis.put(ModRegistry.GuiFishPond, GuiFishPond.class);
        ModRegistry.ModGuis.put(ModRegistry.GuiStartHouseChooser, GuiStartHouseChooser.class);
        ModRegistry.ModGuis.put(ModRegistry.GuiAdvancedWareHouse, GuiAdvancedWareHouse.class);
        ModRegistry.ModGuis.put(ModRegistry.GuiMonsterMasher, GuiMonsterMasher.class);
        ModRegistry.ModGuis.put(ModRegistry.GuiHorseStable, GuiHorseStable.class);
        ModRegistry.ModGuis.put(ModRegistry.GuiNetherGate, GuiNetherGate.class);
        ModRegistry.ModGuis.put(ModRegistry.GuiBasicStructure, GuiBasicStructure.class);
        ModRegistry.ModGuis.put(ModRegistry.GuiVillagerHouses, GuiVillaerHouses.class);
    }

    /**
     * Static constructor for the mod registry. This must be the last static member so the other static fields are
     * initialized first.
     */
    static {
        ModRegistry.RegisterModComponents();
    }
}
