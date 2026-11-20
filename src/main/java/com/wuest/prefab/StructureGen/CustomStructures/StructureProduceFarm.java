package com.wuest.prefab.StructureGen.CustomStructures;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import com.wuest.prefab.Config.Structures.ProduceFarmConfiguration;
import com.wuest.prefab.Config.Structures.StructureConfiguration;
import com.wuest.prefab.StructureGen.BuildBlock;
import com.wuest.prefab.StructureGen.Structure;
import com.wuest.prefab.Util.BlockPos;
import com.wuest.prefab.Util.BlockState;
import com.wuest.prefab.Util.EnumFacing;

/**
 * @author WuestMan
 */
public class StructureProduceFarm extends Structure {

    public static final String ASSETLOCATION = "assets/prefab/structures/producefarm.zip";

    @Override
    protected Boolean CustomBlockProcessingHandled(StructureConfiguration configuration, BuildBlock block, World world,
        BlockPos originalPos, EnumFacing assumedNorth, Block foundBlock, BlockState blockState, EntityPlayer player) {
        if (foundBlock == Blocks.stained_glass) {
            ProduceFarmConfiguration structureConfiguration = (ProduceFarmConfiguration) configuration;
            block.setBlockState(blockState.withMeta(structureConfiguration.dyeColor.getMetadata()));
            this.priorityOneBlocks.add(block);
            return true;
        }

        return false;
    }
}
