package com.wuest.prefab.StructureGen;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.BlockWall;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.google.gson.annotations.Expose;
import com.wuest.prefab.Config.Structures.StructureConfiguration;
import com.wuest.prefab.Util.BlockPos;
import com.wuest.prefab.Util.BlockState;
import com.wuest.prefab.Util.EnumFacing;
import com.wuest.prefab.Util.ModernBlockTranslator;

/**
 * This class defines a single block and where it will be in the structure.
 * <p>
 * The structure files were created with Minecraft 1.10.2 so the block is described using the 1.10 registry name and
 * block state properties. The {@link ModernBlockTranslator} is used to convert this into a 1.7.10 block and metadata.
 *
 * @author WuestMan
 */
public class BuildBlock {

    @Expose
    private String blockDomain;

    @Expose
    private String blockName;

    @Expose
    private PositionOffset startingPosition;

    @Expose
    private ArrayList<BuildProperty> properties;

    @Expose
    private BuildBlock subBlock;

    @Expose
    private boolean hasFacing;

    @Expose
    private String blockStateData;

    private transient BlockState state;

    public BuildBlock() {
        this.Initialize();
    }

    public String getBlockDomain() {
        return this.blockDomain;
    }

    public void setBlockDomain(String value) {
        this.blockDomain = value;
    }

    public String getBlockName() {
        return this.blockName;
    }

    public void setBlockName(String value) {
        this.blockName = value;
    }

    public PositionOffset getStartingPosition() {
        return this.startingPosition;
    }

    public void setStartingPosition(PositionOffset value) {
        this.startingPosition = value;
    }

    public ArrayList<BuildProperty> getProperties() {
        return this.properties;
    }

    public void setProperties(ArrayList<BuildProperty> value) {
        this.properties = value;
    }

    public BuildProperty getProperty(String name) {
        for (BuildProperty property : this.getProperties()) {
            if (name.equals(property.getName())) {
                return property;
            }
        }

        return null;
    }

    /**
     * @return The properties of this block as a map.
     */
    public HashMap<String, String> getPropertyMap() {
        HashMap<String, String> map = new HashMap<>();

        if (this.properties != null) {
            for (BuildProperty property : this.properties) {
                map.put(property.getName(), property.getValue());
            }
        }

        return map;
    }

    public BuildBlock getSubBlock() {
        return this.subBlock;
    }

    public void setSubBlock(BuildBlock value) {
        this.subBlock = value;
    }

    public boolean getHasFacing() {
        return this.hasFacing;
    }

    public void setHasFacing(boolean value) {
        this.hasFacing = value;
    }

    public BlockState getBlockState() {
        return this.state;
    }

    public void setBlockState(BlockState value) {
        this.state = value;
    }

    public String getBlockStateData() {
        return this.blockStateData;
    }

    public void setBlockStateData(String value) {
        this.blockStateData = value;
    }

    public NBTTagCompound getBlockStateDataTag() {
        NBTTagCompound tag = null;

        if (this.blockStateData != null && !this.blockStateData.equals("")) {
            try {
                NBTBase base = JsonToNBT.func_150315_a(this.blockStateData);

                if (base instanceof NBTTagCompound) {
                    tag = (NBTTagCompound) base;
                }
            } catch (NBTException e) {
                e.printStackTrace();
            }
        }

        return tag;
    }

    public void Initialize() {
        this.blockDomain = "";
        this.blockName = "";
        this.properties = new ArrayList<>();
        this.hasFacing = false;
        this.state = null;
        this.subBlock = null;
        this.startingPosition = new PositionOffset();
        this.blockStateData = "";
    }

    /**
     * Gets the number of clockwise quarter turns needed to rotate a structure built with the assumed north to the
     * configured house facing.
     */
    public static int getRotationTurns(StructureConfiguration configuration, EnumFacing assumedNorth) {
        if (configuration.houseFacing == assumedNorth.rotateY()) {
            return 1;
        } else if (configuration.houseFacing == assumedNorth.getOpposite()) {
            return 2;
        } else if (configuration.houseFacing == assumedNorth.rotateYCCW()) {
            return 3;
        }

        return 0;
    }

    /**
     * Gets the 1.7.10 block state for this block, rotated for the configuration facing.
     *
     * @return Null if this block doesn't exist in this version of minecraft.
     */
    public BlockState getTranslatedState(StructureConfiguration configuration, EnumFacing assumedNorth) {
        HashMap<String, String> properties = this.getPropertyMap();
        ModernBlockTranslator.rotate(properties, BuildBlock.getRotationTurns(configuration, assumedNorth));
        BlockState translated = ModernBlockTranslator.translate(this.blockDomain, this.blockName, properties);

        if (translated != null) {
            // Determine if this block is attached to something, these must be placed after other blocks.
            boolean facing = properties.containsKey("facing") || properties.containsKey("rotation");
            Block block = translated.getBlock();

            if (block instanceof BlockVine || block instanceof BlockWall) {
                facing = facing || properties.containsValue("true");
            }

            this.setHasFacing(facing);
        }

        return translated;
    }

    public static BuildBlock SetBlockState(StructureConfiguration configuration, World world, BlockPos originalPos,
        EnumFacing assumedNorth, BuildBlock block, Block foundBlock, BlockState blockState) {
        block.setBlockState(blockState);
        return block;
    }
}
