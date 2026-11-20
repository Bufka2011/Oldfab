package com.wuest.prefab.Config.Structures;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.wuest.prefab.BuildingMethods;
import com.wuest.prefab.Config.EntityPlayerConfiguration;
import com.wuest.prefab.Config.ModConfiguration.CeilingFloorBlockType;
import com.wuest.prefab.Config.ModConfiguration.WallBlockType;
import com.wuest.prefab.Gui.GuiLangKeys;
import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.Proxy.Messages.PlayerEntityTagMessage;
import com.wuest.prefab.StructureGen.CustomStructures.StructureAlternateStart;
import com.wuest.prefab.Util.BlockPos;
import com.wuest.prefab.Util.BlockState;
import com.wuest.prefab.Util.EnumDyeColor;
import com.wuest.prefab.Util.EnumFacing;

/**
 * This class is used to determine the configuration for a particular house.
 * 
 * @author WuestMan
 *
 */
public class HouseConfiguration extends StructureConfiguration {

    protected static BlockPos NorthEastCorner;
    protected static BlockPos SouthEastCorner;
    protected static BlockPos SouthWestCorner;
    protected static BlockPos NorthWestCorner;

    private static String addTorchesTag = "addTorches";
    private static String addBedTag = "addBed";
    private static String addCraftingTableTag = "addCraftingTable";
    private static String addChestTag = "addChest";
    private static String addChestContentsTag = "addChestContents";
    private static String addFarmTag = "addFarm";
    private static String floorBlockTag = "floorBlock";
    private static String ceilingBlockTag = "ceilingBlock";
    private static String wallWoodTypeTag = "wallWoodType";
    private static String isCeilingFlatTag = "isCeilingFlat";
    private static String addMineShaftTag = "addMineShaft";
    private static String hitXTag = "hitX";
    private static String hitYTag = "hitY";
    private static String hitZTag = "hitZ";
    private static String houseWidthTag = "houseWidth";
    private static String houseDepthTag = "houseDepth";
    private static String houseFacingTag = "houseFacing";
    private static String houseStyleTag = "houseStyle";
    private static String glassColorTag = "glassColor";

    public boolean addTorches;
    public boolean addBed;
    public boolean addCraftingTable;
    public boolean addChest;
    public boolean addChestContents;
    public boolean addFarm;
    public CeilingFloorBlockType floorBlock;
    public CeilingFloorBlockType ceilingBlock;
    public WallBlockType wallWoodType;
    public boolean isCeilingFlat;
    public boolean addMineShaft;
    public HouseStyle houseStyle;
    public EnumDyeColor glassColor;

    /**
     * When the house is facing north, this would be the east/west direction.
     */
    public int houseWidth;

    /**
     * When the house if facing north, this would be the north/south direction.
     */
    public int houseDepth;

    /**
     * Initializes a new instance of the {@link HouseConfiguration} class.
     */
    public HouseConfiguration() {
        super();
    }

    @Override
    public void Initialize() {
        super.Initialize();
        this.houseStyle = HouseStyle.BASIC;
        this.glassColor = EnumDyeColor.CYAN;
        this.floorBlock = CeilingFloorBlockType.StoneBrick;
        this.ceilingBlock = CeilingFloorBlockType.StoneBrick;
        this.wallWoodType = WallBlockType.Oak;
        this.houseDepth = 9;
        this.houseWidth = 9;
        this.addTorches = true;
        this.addBed = true;
        this.addCraftingTable = true;
        this.addChest = true;
        this.addChestContents = true;
        this.addFarm = true;
        this.isCeilingFlat = false;
        this.addMineShaft = true;
    }

    @Override
    public NBTTagCompound WriteToNBTTagCompound() {
        NBTTagCompound tag = new NBTTagCompound();

        // This tag should only be written for options which will NOT be overwritten by server options.
        // Server configuration settings will be used for all other options.
        // This is so the server admin can force a player to not use something.
        tag.setBoolean(HouseConfiguration.addTorchesTag, this.addTorches);
        tag.setBoolean(HouseConfiguration.addBedTag, this.addBed);
        tag.setBoolean(HouseConfiguration.addCraftingTableTag, this.addCraftingTable);
        tag.setBoolean(HouseConfiguration.addChestTag, this.addChest);
        tag.setBoolean(HouseConfiguration.addChestContentsTag, this.addChestContents);
        tag.setBoolean(HouseConfiguration.addFarmTag, this.addFarm);
        tag.setInteger(HouseConfiguration.floorBlockTag, this.floorBlock.getValue());
        tag.setInteger(HouseConfiguration.ceilingBlockTag, this.ceilingBlock.getValue());
        tag.setInteger(HouseConfiguration.wallWoodTypeTag, this.wallWoodType.getValue());
        tag.setBoolean(HouseConfiguration.isCeilingFlatTag, this.isCeilingFlat);
        tag.setBoolean(HouseConfiguration.addMineShaftTag, this.addMineShaft);
        tag.setInteger(HouseConfiguration.hitXTag, this.pos.getX());
        tag.setInteger(HouseConfiguration.hitYTag, this.pos.getY());
        tag.setInteger(HouseConfiguration.hitZTag, this.pos.getZ());
        tag.setInteger(HouseConfiguration.houseDepthTag, this.houseDepth);
        tag.setInteger(HouseConfiguration.houseWidthTag, this.houseWidth);
        tag.setString(HouseConfiguration.houseFacingTag, this.houseFacing.getName());
        tag.setInteger(HouseConfiguration.houseStyleTag, this.houseStyle.value);
        tag.setString(
            HouseConfiguration.glassColorTag,
            this.glassColor.getName()
                .toUpperCase());

        return tag;
    }

    /**
     * Gets the name used in a text slider.
     * 
     * @param name  The name of the option to get.
     * @param value The integer value to associate with the name.
     * @return A string representing the value to show in the text slider.
     */
    public static String GetIntegerOptionStringValue(String name, int value) {
        if (name.equals(GuiLangKeys.STARTER_HOUSE_CEILING_TYPE) || name.equals(GuiLangKeys.STARTER_HOUSE_FLOOR_STONE)) {
            return " - " + CeilingFloorBlockType.ValueOf(value)
                .getName();
        } else if (name.equals(GuiLangKeys.STARTER_HOUSE_WALL_TYPE)) {
            return " - " + WallBlockType.ValueOf(value)
                .getName();
        }

        return "";
    }

    /**
     * Custom method to read the NBTTagCompound message.
     * 
     * @param tag The message to create the configuration from.
     * @return An new configuration object with the values derived from the NBTTagCompound.
     */
    @Override
    public HouseConfiguration ReadFromNBTTagCompound(NBTTagCompound tag) {
        HouseConfiguration config = null;

        if (tag != null) {
            config = new HouseConfiguration();

            if (tag.hasKey(HouseConfiguration.addTorchesTag)) {
                config.addTorches = tag.getBoolean(HouseConfiguration.addTorchesTag);
            }

            if (tag.hasKey(HouseConfiguration.addBedTag)) {
                config.addBed = tag.getBoolean(HouseConfiguration.addBedTag);
            }

            if (tag.hasKey(HouseConfiguration.addCraftingTableTag)) {
                config.addCraftingTable = tag.getBoolean(HouseConfiguration.addCraftingTableTag);
            }

            if (tag.hasKey(HouseConfiguration.addChestTag)) {
                config.addChest = tag.getBoolean(HouseConfiguration.addChestTag);
            }

            if (tag.hasKey(HouseConfiguration.addChestContentsTag)) {
                config.addChestContents = tag.getBoolean(HouseConfiguration.addChestContentsTag);
            }

            if (tag.hasKey(HouseConfiguration.addFarmTag)) {
                config.addFarm = tag.getBoolean(HouseConfiguration.addFarmTag);
            }

            if (tag.hasKey(HouseConfiguration.floorBlockTag)) {
                config.floorBlock = CeilingFloorBlockType.ValueOf(tag.getInteger(HouseConfiguration.floorBlockTag));
            }

            if (tag.hasKey(HouseConfiguration.ceilingBlockTag)) {
                config.ceilingBlock = CeilingFloorBlockType.ValueOf(tag.getInteger(HouseConfiguration.ceilingBlockTag));
            }

            if (tag.hasKey(HouseConfiguration.wallWoodTypeTag)) {
                config.wallWoodType = WallBlockType.ValueOf(tag.getInteger(HouseConfiguration.wallWoodTypeTag));
            }

            if (tag.hasKey(HouseConfiguration.isCeilingFlatTag)) {
                config.isCeilingFlat = tag.getBoolean(HouseConfiguration.isCeilingFlatTag);
            }

            if (tag.hasKey(HouseConfiguration.addMineShaftTag)) {
                config.addMineShaft = tag.getBoolean(HouseConfiguration.addMineShaftTag);
            }

            if (tag.hasKey(HouseConfiguration.hitXTag)) {
                config.pos = new BlockPos(
                    tag.getInteger(HouseConfiguration.hitXTag),
                    tag.getInteger(HouseConfiguration.hitYTag),
                    tag.getInteger(HouseConfiguration.hitZTag));
            }

            if (tag.hasKey(HouseConfiguration.houseDepthTag)) {
                config.houseDepth = tag.getInteger(HouseConfiguration.houseDepthTag);
            }

            if (tag.hasKey(HouseConfiguration.houseWidthTag)) {
                config.houseWidth = tag.getInteger(HouseConfiguration.houseWidthTag);
            }

            if (tag.hasKey(HouseConfiguration.houseFacingTag)) {
                config.houseFacing = EnumFacing.byName(tag.getString(HouseConfiguration.houseFacingTag));
            }

            if (tag.hasKey(HouseConfiguration.houseStyleTag)) {
                config.houseStyle = HouseStyle.ValueOf(tag.getInteger(HouseConfiguration.houseStyleTag));
            }

            if (tag.hasKey(HouseConfiguration.glassColorTag)) {
                config.glassColor = EnumDyeColor.valueOf(tag.getString(HouseConfiguration.glassColorTag));
            }
        }

        return config;
    }

    /**
     * This is used to actually build the structure as it creates the structure instance and calls build structure.
     * 
     * @param player      The player which requested the build.
     * @param world       The world instance where the build will occur.
     * @param hitBlockPos This hit block position.
     */
    @Override
    protected void ConfigurationSpecificBuildStructure(EntityPlayer player, World world, BlockPos hitBlockPos) {
        boolean houseBuilt = true;

        if (this.houseStyle == HouseConfiguration.HouseStyle.BASIC) {
            // We hit a block, let's start building!!!!!
            BlockPos startingPosition = hitBlockPos.up();

            // Get the new "North" facing. This is the orientation of
            // the house and all building will be based on this.
            EnumFacing northFace = this.houseFacing;

            // Get the "South" facing of the house to make rotating
            // easier.
            EnumFacing southFace = northFace.getOpposite();

            // Set the "North East" corner.
            /*
             * ItemStartHouse.NorthEastCorner = startingPosition.offset(northFace, (int)
             * Math.floor(configuration.houseDepth / 2) + 1)
             * .offset(northFace.rotateY(), (int) Math.floor(configuration.houseWidth / 2) + 1);
             */
            HouseConfiguration.NorthEastCorner = startingPosition.offset(southFace)
                .offset(northFace.rotateY());

            // Set the "South East" corner.
            HouseConfiguration.SouthEastCorner = HouseConfiguration.NorthEastCorner
                .offset(southFace, this.houseDepth + 1);

            // Set the "South West" corner.
            HouseConfiguration.SouthWestCorner = HouseConfiguration.SouthEastCorner
                .offset(northFace.rotateYCCW(), this.houseWidth + 1);

            // Set the "North West" corner.
            HouseConfiguration.NorthWestCorner = HouseConfiguration.NorthEastCorner
                .offset(northFace.rotateYCCW(), this.houseWidth + 1);

            // Put the starting position in the middle of the house as that's what the rest of the methods expect.
            startingPosition = HouseConfiguration.NorthEastCorner
                .offset(southFace, (int) Math.floor(this.houseDepth / 2) + 1)
                .offset(northFace.rotateYCCW(), (int) Math.floor(this.houseWidth / 2) + 1);

            BlockPos endBlockPos = startingPosition.offset(northFace.rotateYCCW(), this.houseWidth + 11)
                .offset(southFace, this.houseDepth + 11)
                .offset(EnumFacing.UP, 15);

            // Make sure this structure can be placed here.
            if (!BuildingMethods
                .CheckBuildSpaceForAllowedBlockReplacement(this, world, startingPosition, endBlockPos, player)) {
                // Send a message to the player saying that the structure could not
                // be built.
                player.addChatComponentMessage(
                    new ChatComponentTranslation(GuiLangKeys.GUI_STRUCTURE_NOBUILD)
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
                return;
            }

            // Clear the space before the user is teleported. This
            // is in-case they right-click on a space that is only 1
            // block tall.
            BuildingMethods
                .ClearSpace(world, HouseConfiguration.NorthEastCorner, this.houseWidth, 15, this.houseDepth, northFace);

            // Build the basic structure.
            HouseConfiguration.BuildStructure(world, startingPosition, this, northFace);

            // Build the interior.
            HouseConfiguration.BuildInterior(world, startingPosition, player, this, northFace);

            // Set up the exterior.
            HouseConfiguration.BuildExterior(world, startingPosition, player, this, northFace);

            if (this.addMineShaft && startingPosition.getY() > 15) {
                // Set up the mineshaft.
                HouseConfiguration.PlaceMineShaft(world, startingPosition, this.houseDepth, northFace);
            }

            houseBuilt = true;
        } else {
            // Build the alternate starter house instead.
            StructureAlternateStart structure = StructureAlternateStart
                .CreateInstance(this.houseStyle.getStructureLocation(), StructureAlternateStart.class);
            houseBuilt = structure.BuildStructure(this, world, hitBlockPos, EnumFacing.NORTH, player);
        }

        // The house was successfully built, remove the item from the inventory.
        if (houseBuilt) {
            EntityPlayerConfiguration playerConfig = EntityPlayerConfiguration
                .loadFromEntityData((EntityPlayerMP) player);
            playerConfig.builtStarterHouse = true;
            playerConfig.saveToPlayer(player);
            player.inventory.consumeInventoryItem(ModRegistry.StartHouse());
            player.inventoryContainer.detectAndSendChanges();

            // Make sure to send a message to the client to sync up the server player information and the client player
            // information.
            if (player instanceof EntityPlayerMP && ((EntityPlayerMP) player).playerNetServerHandler != null) {
                Prefab.network.sendTo(
                    new PlayerEntityTagMessage(playerConfig.getModIsPlayerNewTag(player)),
                    (EntityPlayerMP) player);
            }
        }
    }

    private static void BuildStructure(World world, BlockPos startingPosition, HouseConfiguration configuration,
        EnumFacing facing) {
        // Make sure that the area beneath the house is all there. Don't want
        // the house to be hanging in the air.
        BlockPos pos = HouseConfiguration.NorthEastCorner;

        BuildingMethods.SetFloor(
            world,
            pos.down(2),
            Blocks.dirt,
            configuration.houseWidth + 2,
            configuration.houseDepth + 2,
            new ArrayList<ItemStack>(),
            facing.getOpposite(),
            null);

        Block floor = null;

        switch (configuration.floorBlock) {
            case Brick: {
                floor = Blocks.brick_block;
                break;
            }

            case SandStone: {
                floor = Blocks.sandstone;
                break;
            }

            default: {
                floor = Blocks.stonebrick;
                break;
            }
        }

        // Create the floor.
        BuildingMethods.SetFloor(
            world,
            pos.down(),
            floor,
            configuration.houseWidth + 2,
            configuration.houseDepth + 2,
            new ArrayList<ItemStack>(),
            facing.getOpposite(),
            null);

        // Create the walls.
        HouseConfiguration.SetWalls(
            world,
            new BlockState(Blocks.planks, configuration.wallWoodType.getValue()),
            configuration,
            facing);

        Block ceiling = null;
        Block stairs = null;

        switch (configuration.ceilingBlock) {
            case Brick: {
                ceiling = Blocks.brick_block;
                stairs = Blocks.brick_stairs;
                break;
            }

            case SandStone: {
                ceiling = Blocks.sandstone;
                stairs = Blocks.sandstone_stairs;
                break;
            }

            default: {
                ceiling = Blocks.stonebrick;
                stairs = Blocks.stone_brick_stairs;
                break;
            }
        }

        // Set the ceiling.
        pos = pos.up(4);
        BuildingMethods.SetCeiling(
            world,
            pos,
            new BlockState(ceiling, 0),
            configuration.houseWidth + 2,
            configuration.houseDepth + 2,
            stairs,
            configuration,
            facing,
            null);
    }

    private static void BuildInterior(World world, BlockPos startingPosition, EntityPlayer player,
        HouseConfiguration configuration, EnumFacing facing) {
        // Keep the corner positions since they are important.
        BlockPos northEastCornerPosition = HouseConfiguration.NorthEastCorner.up();
        BlockPos southEastCornerPosition = HouseConfiguration.SouthEastCorner.up();
        BlockPos northWestCornerPosition = HouseConfiguration.NorthWestCorner.up();
        BlockPos southWestCornerPosition = HouseConfiguration.SouthWestCorner.up();

        // Use a separate position for each item.
        BlockPos itemPosition = northEastCornerPosition;

        if (configuration.addTorches) {
            // Set the torch locations so it's not dark in the house.
            HouseConfiguration.PlaceInsideTorches(world, configuration, facing);
        }

        // Create an oak door in the north east corner
        HouseConfiguration.DecorateDoor(world, northEastCornerPosition, player, configuration, facing);

        if (configuration.addBed) {
            // Place a bed in the north west corner.
            HouseConfiguration.PlaceBed(world, northWestCornerPosition, facing);
        }

        if (configuration.addCraftingTable && !Prefab.proxy.proxyConfiguration.enableHouseGenerationRestrictions) {
            // Place a crafting table in the south west corner.
            HouseConfiguration.PlaceAndFillCraftingMachines(player, world, southWestCornerPosition, facing);
        }

        if (configuration.addChest && !Prefab.proxy.proxyConfiguration.enableHouseGenerationRestrictions) {
            // Place a double chest in the south east corner.
            HouseConfiguration.PlaceAndFillChest(player, world, southEastCornerPosition, configuration, facing);
        }
    }

    private static void BuildExterior(World world, BlockPos startingPosition, EntityPlayer player,
        HouseConfiguration configuration, EnumFacing facing) {
        // Keep the corner positions since they are important.
        // These positions are level with 1 block above the floor.
        BlockPos northEastCornerPosition = HouseConfiguration.NorthEastCorner.up();
        BlockPos southEastCornerPosition = HouseConfiguration.SouthEastCorner.up();
        BlockPos northWestCornerPosition = HouseConfiguration.NorthWestCorner.up();
        BlockPos southWestCornerPosition = HouseConfiguration.SouthWestCorner.up();

        if (configuration.addTorches) {
            HouseConfiguration.PlaceOutsideTorches(world, northEastCornerPosition, configuration, facing);
        }

        if (configuration.addFarm) {
            HouseConfiguration.PlaceSmallFarm(world, northEastCornerPosition.down(), facing);
        }
    }

    private static void SetWalls(World world, BlockState block, HouseConfiguration configuration, EnumFacing facing) {
        // Get the north east corner.
        BlockPos pos = HouseConfiguration.NorthEastCorner;

        facing = facing.getOpposite();

        // East Wall.
        BuildingMethods.CreateWall(world, 4, configuration.houseDepth + 1, facing, pos, block);

        facing = facing.rotateY();

        // South Wall.
        pos = HouseConfiguration.SouthEastCorner;
        BuildingMethods.CreateWall(world, 4, configuration.houseWidth + 2, facing, pos, block);

        facing = facing.rotateY();

        // West Wall.
        pos = HouseConfiguration.SouthWestCorner;
        BuildingMethods.CreateWall(world, 4, configuration.houseDepth + 2, facing, pos, block);

        facing = facing.rotateY();

        // North Wall.
        pos = HouseConfiguration.NorthWestCorner;
        BuildingMethods.CreateWall(world, 4, configuration.houseWidth + 2, facing, pos, block);
    }

    private static void PlaceInsideTorches(World world, HouseConfiguration configuration, EnumFacing facing) {
        // Use a separate position for each item.
        BlockPos itemPosition = null;
        int torchWidthOffset = configuration.houseWidth < 9 ? 2 : 4;
        int torchDepthOffset = configuration.houseDepth < 9 ? 2 : 4;

        /*
         * Torch Facings 1 = East 2 = West 3 = South 4 = North 5 = Up
         */

        // North Wall torches.
        itemPosition = HouseConfiguration.NorthEastCorner.offset(facing.rotateYCCW(), torchWidthOffset)
            .offset(facing.getOpposite())
            .up();
        BlockState blockState = new BlockState(Blocks.torch, BuildingMethods.GetTorchFacing(facing.getOpposite()));
        BuildingMethods.ReplaceBlock(world, itemPosition, blockState);

        int blocksMoved = torchWidthOffset;

        while (blocksMoved < configuration.houseWidth) {
            itemPosition = itemPosition.offset(facing.rotateYCCW(), torchWidthOffset);

            if (BuildingMethods.isAir(world, itemPosition)
                && !BuildingMethods.isAir(world, itemPosition.offset(facing))) {
                BuildingMethods.ReplaceBlock(world, itemPosition, blockState);
            }

            blocksMoved += torchWidthOffset;
        }

        // East wall torches.
        itemPosition = HouseConfiguration.NorthEastCorner.offset(facing.getOpposite(), torchDepthOffset)
            .offset(facing.rotateYCCW())
            .up();
        blockState = new BlockState(Blocks.torch, BuildingMethods.GetTorchFacing(facing.rotateYCCW()));
        BuildingMethods.ReplaceBlock(world, itemPosition, blockState);

        blocksMoved = torchDepthOffset;

        while (blocksMoved < configuration.houseDepth) {
            itemPosition = itemPosition.offset(facing.getOpposite(), torchDepthOffset);

            if (BuildingMethods.isAir(world, itemPosition)
                && !BuildingMethods.isAir(world, itemPosition.offset(facing.rotateY()))) {
                BuildingMethods.ReplaceBlock(world, itemPosition, blockState);
            }

            blocksMoved += torchDepthOffset;
        }

        // West wall torches.
        itemPosition = HouseConfiguration.NorthWestCorner.offset(facing.getOpposite(), torchDepthOffset)
            .offset(facing.rotateY())
            .up();
        blockState = new BlockState(Blocks.torch, BuildingMethods.GetTorchFacing(facing.rotateY()));
        BuildingMethods.ReplaceBlock(world, itemPosition, blockState);

        blocksMoved = torchDepthOffset;

        while (blocksMoved < configuration.houseDepth) {
            itemPosition = itemPosition.offset(facing.getOpposite(), torchDepthOffset);

            if (BuildingMethods.isAir(world, itemPosition)
                && !BuildingMethods.isAir(world, itemPosition.offset(facing.rotateYCCW()))) {
                BuildingMethods.ReplaceBlock(world, itemPosition, blockState);
            }

            blocksMoved += torchDepthOffset;
        }

        // South wall torches.
        itemPosition = HouseConfiguration.SouthEastCorner.offset(facing.rotateYCCW(), torchWidthOffset)
            .offset(facing)
            .up();
        blockState = new BlockState(Blocks.torch, BuildingMethods.GetTorchFacing(facing));
        BuildingMethods.ReplaceBlock(world, itemPosition, blockState);

        blocksMoved = torchDepthOffset;

        while (blocksMoved < configuration.houseWidth) {
            itemPosition = itemPosition.offset(facing.rotateYCCW(), torchDepthOffset);

            if (BuildingMethods.isAir(world, itemPosition)
                && !BuildingMethods.isAir(world, itemPosition.offset(facing.getOpposite()))) {
                BuildingMethods.ReplaceBlock(world, itemPosition, blockState);
            }

            blocksMoved += torchDepthOffset;
        }
    }

    private static void DecorateDoor(World world, BlockPos cornerPosition, EntityPlayer player,
        HouseConfiguration configuration, EnumFacing facing) {
        BlockPos itemPosition = cornerPosition.offset(facing.rotateYCCW());

        BuildingMethods.setBlockToAir(world, itemPosition);

        Block door = null;
        Block stairs = null;

        switch (configuration.wallWoodType) {
            case Spruce: {
                door = Blocks.wooden_door;
                stairs = Blocks.spruce_stairs;
                break;
            }

            case Birch: {
                door = Blocks.wooden_door;
                stairs = Blocks.birch_stairs;
                break;
            }

            case Jungle: {
                door = Blocks.wooden_door;
                stairs = Blocks.jungle_stairs;
                break;
            }

            case Acacia: {
                door = Blocks.wooden_door;
                stairs = Blocks.acacia_stairs;
                break;
            }

            case DarkOak: {
                door = Blocks.wooden_door;
                stairs = Blocks.dark_oak_stairs;
                break;
            }

            default: {
                door = Blocks.wooden_door;
                stairs = Blocks.oak_stairs;
                break;
            }
        }

        ItemDoor.placeDoorBlock(
            world,
            itemPosition.down()
                .getX(),
            itemPosition.down()
                .getY(),
            itemPosition.down()
                .getZ(),
            facing.rotateY()
                .getHorizontalIndex(),
            door);

        // Put a glass pane above the door.
        BuildingMethods.ReplaceBlock(world, itemPosition.up(), Blocks.glass_pane);

        // Create a pressure plate for the door, no need to re-set the item
        // position here since it needs to be in relation to the door.
        itemPosition = itemPosition.offset(facing.getOpposite())
            .down();
        BuildingMethods.ReplaceBlock(world, itemPosition, Blocks.wooden_pressure_plate);

        // Place a sign.
        itemPosition = itemPosition.offset(facing, 2)
            .offset(facing.rotateYCCW());
        BlockSign sign = (BlockSign) Blocks.standing_sign;
        int signMeta = 8;

        switch (facing) {
            case EAST: {
                signMeta = 12;
                break;
            }

            case SOUTH: {
                signMeta = 0;
                break;
            }

            case WEST: {
                signMeta = 4;
                break;
            }
            default: {
                break;
            }
        }

        // Make sure that there is dirt under the sign.
        BuildingMethods.ReplaceBlock(world, itemPosition.down(), Blocks.dirt);
        BuildingMethods.ReplaceBlock(world, itemPosition, new BlockState(sign, signMeta));

        TileEntity tileEntity = world.getTileEntity(itemPosition.getX(), itemPosition.getY(), itemPosition.getZ());

        if (tileEntity instanceof TileEntitySign) {
            TileEntitySign signTile = (TileEntitySign) tileEntity;
            signTile.signText[0] = "This is";

            if (player.getCommandSenderName()
                .length() >= 15) {
                signTile.signText[1] = player.getCommandSenderName();
            } else {
                signTile.signText[1] = player.getCommandSenderName() + "'s";
            }

            signTile.signText[2] = "house!";
            signTile.markDirty();
            world.markBlockForUpdate(itemPosition.getX(), itemPosition.getY(), itemPosition.getZ());
        }
    }

    private static void PlaceBed(World world, BlockPos cornerPosition, EnumFacing facing) {
        // This is the "north west" corner.
        BlockPos itemPosition = cornerPosition.offset(facing.rotateY(), 1)
            .offset(facing.getOpposite(), 2)
            .down();

        if (world.setBlock(
            itemPosition.getX(),
            itemPosition.getY(),
            itemPosition.getZ(),
            Blocks.bed,
            facing.getHorizontalIndex(),
            3)) {
            BlockPos headPos = itemPosition.offset(facing);
            world.setBlock(
                headPos.getX(),
                headPos.getY(),
                headPos.getZ(),
                Blocks.bed,
                facing.getHorizontalIndex() | 8,
                3);
        }

    }

    private static void PlaceAndFillChest(EntityPlayer player, World world, BlockPos cornerPosition,
        HouseConfiguration configuration, EnumFacing facing) {
        // Create a double wide chest.
        BlockPos itemPosition = cornerPosition.offset(facing)
            .offset(facing.rotateYCCW())
            .down();
        BlockState chestState = new BlockState(Blocks.chest, (facing).getIndex());
        BuildingMethods.ReplaceBlock(world, itemPosition, chestState);

        itemPosition = itemPosition.offset(facing.rotateYCCW());
        BuildingMethods.ReplaceBlock(world, itemPosition, chestState);

        if (configuration.addChestContents) {
            StructureAlternateStart.FillChest(world, itemPosition, configuration, player);
        }
    }

    private static void PlaceAndFillCraftingMachines(EntityPlayer player, World world, BlockPos cornerPosition,
        EnumFacing facing) {
        BlockPos itemPosition = cornerPosition.offset(facing.rotateY())
            .offset(facing)
            .down();
        BuildingMethods.ReplaceBlock(world, itemPosition, Blocks.crafting_table);

        // Trigger the workbench achievement.
        player.triggerAchievement(AchievementList.buildWorkBench);

        // Place a furnace next to the crafting table and fill it with 20 coal.
        itemPosition = itemPosition.offset(facing.rotateY());
        BuildingMethods.ReplaceBlock(world, itemPosition, new BlockState(Blocks.furnace, (facing).getIndex()));

        TileEntity tileEntity = world.getTileEntity(itemPosition.getX(), itemPosition.getY(), itemPosition.getZ());

        if (tileEntity instanceof TileEntityFurnace) {
            TileEntityFurnace furnaceTile = (TileEntityFurnace) tileEntity;
            furnaceTile.setInventorySlotContents(1, new ItemStack(Items.coal, 20));
        }
    }

    private static void PlaceOutsideTorches(World world, BlockPos cornerPosition, HouseConfiguration configuration,
        EnumFacing facing) {
        BlockPos itemPosition = HouseConfiguration.NorthEastCorner.offset(facing);
        int houseDepth = configuration.houseDepth + 2;
        int houseWidth = configuration.houseWidth + 2;
        int torchWidthOffset = configuration.houseWidth < 9 ? 3 : 4;
        int torchDepthOffset = configuration.houseDepth < 9 ? 3 : 4;

        // North Wall torches going west.
        BlockState blockState = new BlockState(Blocks.torch, BuildingMethods.GetTorchFacing(facing));
        BuildingMethods.ReplaceBlock(world, itemPosition, blockState);

        int blocksMoved = torchWidthOffset;

        while (blocksMoved <= houseWidth) {
            itemPosition = itemPosition.offset(facing.rotateYCCW(), torchWidthOffset);

            if (BuildingMethods.isAir(world, itemPosition)
                && BuildingMethods.isSideSolid(world, itemPosition.offset(facing.getOpposite()), facing)) {
                BuildingMethods.ReplaceBlock(world, itemPosition, blockState);
            }

            blocksMoved += torchWidthOffset;
        }

        // East wall torches going south.
        itemPosition = HouseConfiguration.NorthEastCorner.offset(facing.rotateY());
        blockState = new BlockState(Blocks.torch, BuildingMethods.GetTorchFacing(facing.rotateY()));
        BuildingMethods.ReplaceBlock(world, itemPosition, blockState);

        blocksMoved = torchDepthOffset;

        while (blocksMoved <= houseDepth) {
            itemPosition = itemPosition.offset(facing.getOpposite(), torchDepthOffset);

            if (BuildingMethods.isAir(world, itemPosition)
                && BuildingMethods.isSideSolid(world, itemPosition.offset(facing.rotateYCCW()), facing.rotateY())) {
                BuildingMethods.ReplaceBlock(world, itemPosition, blockState);
            }

            blocksMoved += torchDepthOffset;
        }

        // West wall torches going south.
        itemPosition = HouseConfiguration.NorthWestCorner.offset(facing.rotateYCCW());
        blockState = new BlockState(Blocks.torch, BuildingMethods.GetTorchFacing(facing.rotateYCCW()));
        BuildingMethods.ReplaceBlock(world, itemPosition, blockState);

        blocksMoved = torchDepthOffset;

        while (blocksMoved <= houseDepth) {
            itemPosition = itemPosition.offset(facing.getOpposite(), torchDepthOffset);

            if (BuildingMethods.isAir(world, itemPosition)
                && BuildingMethods.isSideSolid(world, itemPosition.offset(facing.rotateY()), facing.rotateYCCW())) {
                BuildingMethods.ReplaceBlock(world, itemPosition, blockState);
            }

            blocksMoved += torchDepthOffset;
        }

        // South wall torches going west.
        itemPosition = HouseConfiguration.SouthEastCorner.offset(facing.getOpposite());
        blockState = new BlockState(Blocks.torch, BuildingMethods.GetTorchFacing(facing.getOpposite()));
        BuildingMethods.ReplaceBlock(world, itemPosition, blockState);

        blocksMoved = torchDepthOffset;

        while (blocksMoved <= houseWidth) {
            itemPosition = itemPosition.offset(facing.rotateYCCW(), torchDepthOffset);

            if (BuildingMethods.isAir(world, itemPosition)
                && BuildingMethods.isSideSolid(world, itemPosition.offset(facing), facing.getOpposite())) {
                BuildingMethods.ReplaceBlock(world, itemPosition, blockState);
            }

            blocksMoved += torchDepthOffset;
        }

        if (configuration.isCeilingFlat) {
            // Roof Torches
            // North east corner.
            cornerPosition = cornerPosition.up(4);
            blockState = new BlockState(Blocks.torch, BuildingMethods.GetTorchFacing(EnumFacing.UP));

            for (int i = 0; i <= houseDepth; i += torchDepthOffset) {
                itemPosition = cornerPosition.offset(facing.getOpposite(), i);

                for (int j = 0; j <= houseWidth; j += torchWidthOffset) {
                    // When placing the torch, make sure that the torch will be on a solid block otherwise it will just
                    // fall.
                    if (BuildingMethods.isAir(world, itemPosition)
                        && !BuildingMethods.isAir(world, itemPosition.down())) {
                        BuildingMethods.ReplaceBlock(world, itemPosition, blockState);
                    }

                    itemPosition = itemPosition.offset(facing.rotateYCCW(), torchWidthOffset);
                }
            }
        }
    }

    private static void PlaceSmallFarm(World world, BlockPos cornerPosition, EnumFacing facing) {
        BlockPos farmStart = cornerPosition.offset(facing, 4)
            .offset(facing.rotateYCCW(), 5);
        BlockState state = BlockState.get(world, farmStart);

        farmStart = farmStart.down();

        boolean setWaterToCobbleStone = world.provider.isHellWorld || world.provider.dimensionId == 1;
        Block waterBlock = setWaterToCobbleStone ? Blocks.cobblestone : Blocks.water;

        // We are now at the surface and this is where the first water source
        // will be.
        BuildingMethods.ReplaceBlock(world, farmStart, waterBlock);
        BuildingMethods.ReplaceBlock(world, farmStart.down(), Blocks.dirt);
        BuildingMethods.ReplaceBlock(world, farmStart.up(), Blocks.glass);
        BuildingMethods.ReplaceBlock(world, farmStart.up(2), Blocks.torch);
        BuildingMethods.ReplaceBlock(world, farmStart.offset(facing), Blocks.farmland);
        BuildingMethods.ReplaceBlock(
            world,
            farmStart.offset(facing)
                .offset(facing.rotateYCCW()),
            Blocks.farmland);
        BuildingMethods.ReplaceBlock(world, farmStart.offset(facing.rotateYCCW()), Blocks.farmland);
        BuildingMethods.ReplaceBlock(world, farmStart.offset(facing.getOpposite()), Blocks.farmland);
        BuildingMethods.ReplaceBlock(
            world,
            farmStart.offset(facing.getOpposite())
                .offset(facing.rotateYCCW()),
            Blocks.farmland);

        farmStart = farmStart.offset(facing.rotateY());

        BuildingMethods.ReplaceBlock(world, farmStart, waterBlock);
        BuildingMethods.ReplaceBlock(world, farmStart.down(), Blocks.dirt);
        BuildingMethods.ReplaceBlock(world, farmStart.up(), Blocks.glass);
        BuildingMethods.ReplaceBlock(world, farmStart.up(2), Blocks.torch);
        BuildingMethods.ReplaceBlock(world, farmStart.offset(facing), Blocks.farmland);
        BuildingMethods.ReplaceBlock(world, farmStart.offset(facing.getOpposite()), Blocks.farmland);

        farmStart = farmStart.offset(facing.rotateY());

        BuildingMethods.ReplaceBlock(world, farmStart, waterBlock);
        BuildingMethods.ReplaceBlock(world, farmStart.down(), Blocks.dirt);
        BuildingMethods.ReplaceBlock(world, farmStart.up(), Blocks.glass);
        BuildingMethods.ReplaceBlock(world, farmStart.up(2), Blocks.torch);
        BuildingMethods.ReplaceBlock(world, farmStart.offset(facing), Blocks.farmland);
        BuildingMethods.ReplaceBlock(
            world,
            farmStart.offset(facing)
                .offset(facing.rotateY()),
            Blocks.farmland);
        BuildingMethods.ReplaceBlock(world, farmStart.offset(facing.rotateY()), Blocks.farmland);
        BuildingMethods.ReplaceBlock(world, farmStart.offset(facing.getOpposite()), Blocks.farmland);
        BuildingMethods.ReplaceBlock(
            world,
            farmStart.offset(facing.getOpposite())
                .offset(facing.rotateY()),
            Blocks.farmland);
    }

    private static void PlaceMineShaft(World world, BlockPos pos, int houseDepth, EnumFacing facing) {
        // The initial position is where the character was teleported too, go
        // back X blocks and start building the mine shaft.
        if ((houseDepth & 1) != 0) {
            houseDepth = (int) Math.floor(houseDepth / 2);
        } else {
            houseDepth = (int) Math.floor(houseDepth / 2) - 1;
        }

        pos = pos.offset(facing.getOpposite(), houseDepth)
            .down();

        StructureAlternateStart.PlaceMineShaft(world, pos, facing, false);
    }

    /**
     * This enum is used to contain the different type of starting houses available to the player.
     * 
     * @author WuestMan
     *
     */
    public enum HouseStyle {

        BASIC(0, GuiLangKeys.STARTER_HOUSE_BASIC_DISPLAY,
            new ResourceLocation("prefab", "textures/gui/basic_house.png"), GuiLangKeys.STARTER_HOUSE_BASIC_NOTES, 153,
            148, ""),
        RANCH(1, GuiLangKeys.STARTER_HOUSE_RANCH_DISPLAY,
            new ResourceLocation("prefab", "textures/gui/ranch_house.png"), GuiLangKeys.STARTER_HOUSE_RANCH_NOTES, 152,
            89, "assets/prefab/structures/ranch_house.zip"),
        LOFT(2, GuiLangKeys.STARTER_HOUSE_LOFT_DISPLAY, new ResourceLocation("prefab", "textures/gui/loft_house.png"),
            GuiLangKeys.STARTER_HOUSE_LOFT_NOTES, 152, 87, "assets/prefab/structures/loft_house.zip"),
        HOBBIT(3, GuiLangKeys.STARTER_HOUSE_HOBBIT_DISPLAY,
            new ResourceLocation("prefab", "textures/gui/hobbit_house.png"), GuiLangKeys.STARTER_HOUSE_HOBBIT_NOTES,
            151, 133, "assets/prefab/structures/hobbit_house.zip"),
        DESERT(4, GuiLangKeys.STARTER_HOUSE_DESERT_DISPLAY,
            new ResourceLocation("prefab", "textures/gui/desert_house.png"), GuiLangKeys.STARTER_HOUSE_DESERT_NOTES,
            152, 131, "assets/prefab/structures/desert_house.zip"),
        SNOWY(5, GuiLangKeys.STARTER_HOUSE_SNOWY_DISPLAY,
            new ResourceLocation("prefab", "textures/gui/snowy_house.png"), GuiLangKeys.STARTER_HOUSE_SNOWY_NOTES, 150,
            125, "assets/prefab/structures/snowy_house.zip");

        private final int value;
        private final String displayName;
        private final ResourceLocation housePicture;
        private final String houseNotes;
        private final int imageWidth;
        private final int imageHeight;
        private final String structureLocation;

        HouseStyle(int newValue, String displayName, ResourceLocation housePicture, String houseNotes, int imageWidth,
            int imageHeight, String structureLocation) {
            this.value = newValue;
            this.displayName = displayName;
            this.housePicture = housePicture;
            this.houseNotes = houseNotes;
            this.imageWidth = imageWidth;
            this.imageHeight = imageHeight;
            this.structureLocation = structureLocation;
        }

        /**
         * Gets a unique identifier for this style.
         * 
         * @return An integer representing the ID of this style.
         */
        public int getValue() {
            return value;
        }

        /**
         * Gets the display name for this style.
         * 
         * @return A string representing the name of this style.
         */
        public String getDisplayName() {
            return GuiLangKeys.translateString(this.displayName);
        }

        /**
         * Gets the notes for this house style.
         * 
         * @return A string representing the translated notes for this style.
         */
        public String getHouseNotes() {
            return GuiLangKeys.translateString(this.houseNotes);
        }

        /**
         * Gets the picture used in the GUI for this style.
         * 
         * @return A resource location representing the image to use for this style.
         */
        public ResourceLocation getHousePicture() {
            return this.housePicture;
        }

        /**
         * Gets the width of the image to use with this style.
         * 
         * @return An integer representing the image width.
         */
        public int getImageWidth() {
            return this.imageWidth;
        }

        /**
         * Gets the height of the image to use with this style.
         * 
         * @return An integer representing the image height.
         */
        public int getImageHeight() {
            return this.imageHeight;
        }

        /**
         * Gets a string for the resource location of this style.
         * 
         * @return A string representing the location of the structure asset in the mod.
         */
        public String getStructureLocation() {
            return this.structureLocation;
        }

        /**
         * Returns a house style based off of an integer value.
         * 
         * @param value The integer value representing the house style.
         * @return The house style found or HouseStyle.Basic if none found.
         */
        public static HouseStyle ValueOf(int value) {
            switch (value) {
                case 0: {
                    return HouseStyle.BASIC;
                }

                case 1: {
                    return HouseStyle.RANCH;
                }

                case 2: {
                    return HouseStyle.LOFT;
                }

                case 3: {
                    return HouseStyle.HOBBIT;
                }

                case 4: {
                    return HouseStyle.DESERT;
                }

                case 5: {
                    return HouseStyle.SNOWY;
                }

                default: {
                    return HouseStyle.BASIC;
                }
            }
        }
    }
}
