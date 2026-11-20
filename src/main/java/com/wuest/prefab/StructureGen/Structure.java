package com.wuest.prefab.StructureGen;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.wuest.prefab.BuildingMethods;
import com.wuest.prefab.Config.Structures.StructureConfiguration;
import com.wuest.prefab.Events.ModEventHandler;
import com.wuest.prefab.Gui.GuiLangKeys;
import com.wuest.prefab.Util.BlockPos;
import com.wuest.prefab.Util.BlockState;
import com.wuest.prefab.Util.EnumFacing;
import com.wuest.prefab.ZipUtil;

/**
 * Each structure represents a building which is pre-defined in a JSON file.
 *
 * @author WuestMan
 */
public class Structure {

    @Expose
    private String name;

    @Expose
    private BuildClear clearSpace;

    @Expose
    private ArrayList<BuildBlock> blocks;

    public ArrayList<BlockPos> clearedBlockPos = new ArrayList<>();
    public ArrayList<BuildBlock> priorityOneBlocks = new ArrayList<>();
    public ArrayList<BuildBlock> priorityTwoBlocks = new ArrayList<>();
    public ArrayList<BuildBlock> priorityThreeBlocks = new ArrayList<>();
    public StructureConfiguration configuration;
    public World world;
    public BlockPos originalPos;
    public EnumFacing assumedNorth;

    public Structure() {
        this.Initialize();
    }

    /**
     * Creates an instance of the structure after reading from a resource location and converting it from JSON.
     *
     * @param <T>              The type which extends Structure.
     * @param resourceLocation The location of the JSON file to load. Example: "assets/prefab/structures/warehouse.zip"
     * @param child            The child class which extends Structure.
     * @return Null if the resource wasn't found or the JSON could not be parsed, otherwise the de-serialized object.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Structure> T CreateInstance(String resourceLocation, Class<? extends Structure> child) {
        Gson file = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
            .create();
        return (T) file.fromJson(ZipUtil.decompressResource(resourceLocation), child);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public BuildClear getClearSpace() {
        return this.clearSpace;
    }

    public void setClearSpace(BuildClear value) {
        this.clearSpace = value;
    }

    public ArrayList<BuildBlock> getBlocks() {
        return this.blocks;
    }

    public void setBlocks(ArrayList<BuildBlock> value) {
        this.blocks = value;
    }

    public void Initialize() {
        this.name = "";
        this.clearSpace = new BuildClear();
        this.blocks = new ArrayList<>();
    }

    /**
     * This is the main building method for this structure.
     *
     * @param configuration The configuration the user updated.
     * @param world         The current world.
     * @param originalPos   The block the user clicked on.
     * @param assumedNorth  This should always be "NORTH" when the file is based on a scan.
     * @param player        The player requesting the structure.
     * @return True if the build can occur, otherwise false.
     */
    public boolean BuildStructure(StructureConfiguration configuration, World world, BlockPos originalPos,
        EnumFacing assumedNorth, EntityPlayer player) {
        BlockPos startBlockPos = this.clearSpace.getStartingPosition()
            .getRelativePosition(originalPos, configuration.houseFacing);
        BlockPos endBlockPos = startBlockPos.offset(
            configuration.houseFacing.rotateYCCW(),
            this.clearSpace.getShape()
                .getWidth() - 1)
            .offset(
                configuration.houseFacing.getOpposite(),
                this.clearSpace.getShape()
                    .getWidth() - 1)
            .offset(
                EnumFacing.UP,
                this.clearSpace.getShape()
                    .getHeight());

        // Make sure this structure can be placed here.
        if (!BuildingMethods
            .CheckBuildSpaceForAllowedBlockReplacement(configuration, world, startBlockPos, endBlockPos, player)) {
            // Send a message to the player saying that the structure could not be built.
            player.addChatComponentMessage(
                new ChatComponentTranslation(GuiLangKeys.GUI_STRUCTURE_NOBUILD)
                    .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
            return false;
        }

        if (!this.BeforeBuilding(configuration, world, originalPos, assumedNorth, player)) {
            // First, clear the area where the structure will be built.
            this.ClearSpace(configuration, world, originalPos, assumedNorth);

            // Now place all of the blocks.
            for (BuildBlock block : this.getBlocks()) {
                BlockState blockState = block.getTranslatedState(configuration, assumedNorth);

                if (blockState == null) {
                    continue;
                }

                Block foundBlock = blockState.getBlock();
                BuildBlock subBlock = null;

                // Check if water should be replaced with cobble.
                if (!this.WaterReplacedWithCobbleStone(
                    configuration,
                    block,
                    world,
                    originalPos,
                    assumedNorth,
                    foundBlock,
                    blockState,
                    player)
                    && !this.CustomBlockProcessingHandled(
                        configuration,
                        block,
                        world,
                        originalPos,
                        assumedNorth,
                        foundBlock,
                        blockState,
                        player)) {
                    block.setBlockState(blockState);

                    if (block.getSubBlock() != null) {
                        BlockState subState = block.getSubBlock()
                            .getTranslatedState(configuration, assumedNorth);

                        if (subState != null) {
                            subBlock = block.getSubBlock();
                            subBlock.setBlockState(subState);
                            foundBlock = subState.getBlock();
                        } else {
                            block.setSubBlock(null);
                        }
                    }

                    if (!block.getHasFacing()) {
                        this.priorityOneBlocks.add(block);
                    } else {
                        // These blocks may be attached to other facing blocks and must be done later.
                        if (foundBlock instanceof BlockTorch || foundBlock instanceof BlockSign
                            || foundBlock instanceof BlockLever
                            || foundBlock instanceof BlockButton) {
                            this.priorityThreeBlocks.add(block);
                        } else {
                            this.priorityTwoBlocks.add(block);
                        }
                    }
                }
            }

            this.configuration = configuration;
            this.world = world;
            this.assumedNorth = assumedNorth;
            this.originalPos = originalPos;

            if (ModEventHandler.structuresToBuild.containsKey(player)) {
                ModEventHandler.structuresToBuild.get(player)
                    .add(this);
            } else {
                ArrayList<Structure> structures = new ArrayList<>();
                structures.add(this);
                ModEventHandler.structuresToBuild.put(player, structures);
            }
        }

        return true;
    }

    /**
     * This method is used before any building occurs to check for things or possibly pre-build locations. Note: This
     * is even done before blocks are cleared.
     *
     * @return False if processing should continue, otherwise true to cancel processing.
     */
    protected boolean BeforeBuilding(StructureConfiguration configuration, World world, BlockPos originalPos,
        EnumFacing assumedNorth, EntityPlayer player) {
        return false;
    }

    /**
     * This method is used after the main building is build for any additional structures or modifications.
     */
    public void AfterBuilding(StructureConfiguration configuration, World world, BlockPos originalPos,
        EnumFacing assumedNorth, EntityPlayer player) {}

    protected void ClearSpace(StructureConfiguration configuration, World world, BlockPos originalPos,
        EnumFacing assumedNorth) {
        if (this.clearSpace.getShape()
            .getWidth() > 0
            && this.clearSpace.getShape()
                .getLength() > 0) {
            BlockPos startBlockPos = this.clearSpace.getStartingPosition()
                .getRelativePosition(originalPos, configuration.houseFacing);
            BlockPos endBlockPos = startBlockPos
                .offset(
                    configuration.houseFacing.rotateYCCW(),
                    this.clearSpace.getShape()
                        .getWidth() - 1)
                .offset(
                    configuration.houseFacing.getOpposite(),
                    this.clearSpace.getShape()
                        .getLength() - 1)
                .offset(
                    EnumFacing.UP,
                    this.clearSpace.getShape()
                        .getHeight());

            this.clearedBlockPos = new ArrayList<>(BlockPos.getAllInBox(startBlockPos, endBlockPos));
        } else {
            this.clearedBlockPos = new ArrayList<>();
        }
    }

    protected Boolean CustomBlockProcessingHandled(StructureConfiguration configuration, BuildBlock block, World world,
        BlockPos originalPos, EnumFacing assumedNorth, Block foundBlock, BlockState blockState, EntityPlayer player) {
        return false;
    }

    /**
     * Determines if a water block was replaced with cobblestone because this structure was built in the nether.
     *
     * @return Returns true if the water block was replaced by cobblestone, otherwise false.
     */
    protected Boolean WaterReplacedWithCobbleStone(StructureConfiguration configuration, BuildBlock block, World world,
        BlockPos originalPos, EnumFacing assumedNorth, Block foundBlock, BlockState blockState, EntityPlayer player) {
        // Replace water blocks with cobblestone.
        if (foundBlock instanceof BlockLiquid && foundBlock.getMaterial() == Material.water
            && world.provider.isHellWorld) {
            block.setBlockDomain("minecraft");
            block.setBlockName("cobblestone");
            block.setBlockState(new BlockState(Blocks.cobblestone, 0));

            // Add this as a priority 3 block since it should be done at the end.
            this.priorityThreeBlocks.add(block);
            return true;
        }

        return false;
    }
}
