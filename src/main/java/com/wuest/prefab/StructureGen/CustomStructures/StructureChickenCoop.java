package com.wuest.prefab.StructureGen.CustomStructures;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.wuest.prefab.Config.Structures.StructureConfiguration;
import com.wuest.prefab.StructureGen.BuildBlock;
import com.wuest.prefab.StructureGen.Structure;
import com.wuest.prefab.Util.BlockPos;
import com.wuest.prefab.Util.BlockState;
import com.wuest.prefab.Util.EnumFacing;

/**
 * @author WuestMan
 */
public class StructureChickenCoop extends Structure {

    public static final String ASSETLOCATION = "assets/prefab/structures/chickencoop.zip";

    private BlockPos chickenCoopBlockPos = null;

    @Override
    protected Boolean CustomBlockProcessingHandled(StructureConfiguration configuration, BuildBlock block, World world,
        BlockPos originalPos, EnumFacing assumedNorth, Block foundBlock, BlockState blockState, EntityPlayer player) {
        if (foundBlock instanceof BlockFenceGate && this.chickenCoopBlockPos == null) {
            this.chickenCoopBlockPos = block.getStartingPosition()
                .getRelativePosition(originalPos, configuration.houseFacing)
                .offset(configuration.houseFacing.getOpposite(), 2);
        }

        return false;
    }

    @Override
    public void AfterBuilding(StructureConfiguration configuration, World world, BlockPos originalPos,
        EnumFacing assumedNorth, EntityPlayer player) {
        if (this.chickenCoopBlockPos != null) {
            EntityChicken entity = new EntityChicken(world);
            entity.setPosition(
                this.chickenCoopBlockPos.getX() + 0.5D,
                this.chickenCoopBlockPos.getY(),
                this.chickenCoopBlockPos.getZ() + 0.5D);
            world.spawnEntityInWorld(entity);

            this.chickenCoopBlockPos = null;
        }
    }
}
