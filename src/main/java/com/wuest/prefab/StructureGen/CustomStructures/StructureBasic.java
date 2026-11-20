package com.wuest.prefab.StructureGen.CustomStructures;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.wuest.prefab.BuildingMethods;
import com.wuest.prefab.Config.Structures.BasicStructureConfiguration;
import com.wuest.prefab.Config.Structures.BasicStructureConfiguration.EnumBasicStructureName;
import com.wuest.prefab.Config.Structures.StructureConfiguration;
import com.wuest.prefab.StructureGen.BuildBlock;
import com.wuest.prefab.StructureGen.Structure;
import com.wuest.prefab.Util.BlockPos;
import com.wuest.prefab.Util.BlockState;
import com.wuest.prefab.Util.EnumFacing;

/**
 * This is the basic structure to be used for structures which don't need a lot of configuration or a custom player
 * created structures.
 * 
 * @author WuestMan
 *
 */
public class StructureBasic extends Structure {

    BlockPos customBlockPos = null;

    @Override
    protected Boolean CustomBlockProcessingHandled(StructureConfiguration configuration, BuildBlock block, World world,
        BlockPos originalPos, EnumFacing assumedNorth, Block foundBlock, BlockState blockState, EntityPlayer player) {
        BasicStructureConfiguration config = (BasicStructureConfiguration) configuration;

        if (foundBlock instanceof BlockHopper && config.basicStructureName.getName()
            .equals(EnumBasicStructureName.AdavancedCoop.getName())) {
            customBlockPos = block.getStartingPosition()
                .getRelativePosition(originalPos, configuration.houseFacing);
        } else if (foundBlock instanceof BlockTrapDoor && config.basicStructureName.getName()
            .equals(EnumBasicStructureName.MineshaftEntrance.getName())) {
                customBlockPos = block.getStartingPosition()
                    .getRelativePosition(originalPos, configuration.houseFacing);
            }

        return false;
    }

    /**
     * This method is used after the main building is build for any additional
     * structures or modifications.
     * 
     * @param configuration The structure configuration.
     * @param world         The current world.
     * @param originalPos   The original position clicked on.
     * @param assumedNorth  The assumed northern direction.
     * @param player        The player which initiated the construction.
     */
    @Override
    public void AfterBuilding(StructureConfiguration configuration, World world, BlockPos originalPos,
        EnumFacing assumedNorth, EntityPlayer player) {
        BasicStructureConfiguration config = (BasicStructureConfiguration) configuration;

        if (this.customBlockPos != null) {
            if (config.basicStructureName.getName()
                .equals(EnumBasicStructureName.AdavancedCoop.getName())) {
                // For the advanced chicken coop, spawn 4 chickens above the hopper.
                for (int i = 0; i < 4; i++) {
                    EntityChicken entity = new EntityChicken(world);
                    entity.setPosition(
                        this.customBlockPos.getX(),
                        this.customBlockPos.up()
                            .getY(),
                        this.customBlockPos.getZ());
                    world.spawnEntityInWorld(entity);
                }
            } else if (config.basicStructureName.getName()
                .equals(EnumBasicStructureName.MineshaftEntrance.getName())) {
                    // Build the mineshaft where the trap door exists.
                    StructureAlternateStart
                        .PlaceMineShaft(world, this.customBlockPos.down(), configuration.houseFacing, true);
                }

            this.customBlockPos = null;
        }

        if (config.basicStructureName.getName()
            .equals(EnumBasicStructureName.AquaBase.getName())) {
            // Replace the entrance area with air blocks.
            BlockPos airPos = originalPos.up(4)
                .offset(configuration.houseFacing.getOpposite(), 1);

            // This is the first wall.
            BuildingMethods.setBlockToAir(world, airPos.offset(configuration.houseFacing.rotateY()));
            BuildingMethods.setBlockToAir(world, airPos);
            BuildingMethods.setBlockToAir(world, airPos.offset(configuration.houseFacing.rotateYCCW()));

            airPos = airPos.down();
            BuildingMethods.setBlockToAir(world, airPos.offset(configuration.houseFacing.rotateY()));
            BuildingMethods.setBlockToAir(world, airPos);
            BuildingMethods.setBlockToAir(world, airPos.offset(configuration.houseFacing.rotateYCCW()));

            airPos = airPos.down();
            BuildingMethods.setBlockToAir(world, airPos.offset(configuration.houseFacing.rotateY()));
            BuildingMethods.setBlockToAir(world, airPos);
            BuildingMethods.setBlockToAir(world, airPos.offset(configuration.houseFacing.rotateYCCW()));

            airPos = airPos.down();
            BuildingMethods.setBlockToAir(world, airPos.offset(configuration.houseFacing.rotateY()));
            BuildingMethods.setBlockToAir(world, airPos);
            BuildingMethods.setBlockToAir(world, airPos.offset(configuration.houseFacing.rotateYCCW()));

            // Second part of the wall.
            airPos = airPos.offset(configuration.houseFacing.getOpposite())
                .up();
            BuildingMethods.setBlockToAir(world, airPos.offset(configuration.houseFacing.rotateY()));
            BuildingMethods.setBlockToAir(world, airPos);
            BuildingMethods.setBlockToAir(world, airPos.offset(configuration.houseFacing.rotateYCCW()));

            airPos = airPos.up();
            BuildingMethods.setBlockToAir(world, airPos.offset(configuration.houseFacing.rotateY()));
            BuildingMethods.setBlockToAir(world, airPos);
            BuildingMethods.setBlockToAir(world, airPos.offset(configuration.houseFacing.rotateYCCW()));

            airPos = airPos.up();
            BuildingMethods.setBlockToAir(world, airPos);
        }
    }
}
