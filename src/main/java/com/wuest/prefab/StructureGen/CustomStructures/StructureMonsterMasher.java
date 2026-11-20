package com.wuest.prefab.StructureGen.CustomStructures;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;

import com.wuest.prefab.Config.Structures.MonsterMasherConfiguration;
import com.wuest.prefab.Config.Structures.StructureConfiguration;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.StructureGen.BuildBlock;
import com.wuest.prefab.StructureGen.Structure;
import com.wuest.prefab.Util.BlockPos;
import com.wuest.prefab.Util.BlockState;
import com.wuest.prefab.Util.EnumFacing;

/**
 * 
 * @author WuestMan
 *
 */
public class StructureMonsterMasher extends Structure {

    public static final String ASSETLOCATION = "assets/prefab/structures/monster_masher.zip";

    private ArrayList<BlockPos> mobSpawnerPos = new ArrayList<BlockPos>();
    private BlockPos signPosition = null;

    @Override
    protected Boolean CustomBlockProcessingHandled(StructureConfiguration configuration, BuildBlock block, World world,
        BlockPos originalPos, EnumFacing assumedNorth, Block foundBlock, BlockState blockState, EntityPlayer player) {
        if (foundBlock == Blocks.stained_glass) {
            MonsterMasherConfiguration wareHouseConfiguration = (MonsterMasherConfiguration) configuration;

            blockState = blockState.withMeta(wareHouseConfiguration.dyeColor.getMetadata());
            block.setBlockState(blockState);
            this.priorityOneBlocks.add(block);

            return true;
        } else if (foundBlock == Blocks.mob_spawner) {
            if (Prefab.proxy.proxyConfiguration.includeSpawnersInMasher) {
                this.mobSpawnerPos.add(
                    block.getStartingPosition()
                        .getRelativePosition(originalPos, configuration.houseFacing));
            } else {
                return true;
            }
        } else if (foundBlock instanceof BlockSign) {
            this.signPosition = block.getStartingPosition()
                .getRelativePosition(originalPos, configuration.houseFacing);
        }

        return false;
    }

    /**
     * This method is used after the main building is build for any additional structures or modifications.
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
        int monstersPlaced = 0;

        // Set the spawner.
        for (BlockPos pos : this.mobSpawnerPos) {
            TileEntity tileEntity = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());

            if (tileEntity != null && tileEntity instanceof TileEntityMobSpawner) {
                TileEntityMobSpawner spawner = (TileEntityMobSpawner) tileEntity;

                switch (monstersPlaced) {
                    case 0: {
                        // Zombie.
                        spawner.func_145881_a()
                            .setEntityName("Zombie");
                        break;
                    }

                    case 1: {
                        // Skeleton.
                        spawner.func_145881_a()
                            .setEntityName("Skeleton");
                        break;
                    }

                    case 2: {
                        // Spider.
                        spawner.func_145881_a()
                            .setEntityName("Spider");
                        break;
                    }

                    default: {
                        // Creeper.
                        spawner.func_145881_a()
                            .setEntityName("Creeper");
                        break;
                    }
                }

                monstersPlaced++;
            }
        }

        if (this.signPosition != null) {
            TileEntity tileEntity = world
                .getTileEntity(this.signPosition.getX(), this.signPosition.getY(), this.signPosition.getZ());

            if (tileEntity instanceof TileEntitySign) {
                TileEntitySign signTile = (TileEntitySign) tileEntity;
                signTile.signText[0] = "Lamp On=Mobs";

                signTile.signText[2] = "Lamp Off=No Mobs";
                signTile.markDirty();
                world.markBlockForUpdate(this.signPosition.getX(), this.signPosition.getY(), this.signPosition.getZ());
            }
        }
    }
}
