package com.wuest.prefab.Config.Structures;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.StructureGen.CustomStructures.StructureTreeFarm;
import com.wuest.prefab.Util.BlockPos;
import com.wuest.prefab.Util.EnumFacing;

/**
 * 
 * @author WuestMan
 *
 */
public class TreeFarmConfiguration extends StructureConfiguration {

    /**
     * Custom method to read the NBTTagCompound message.
     * 
     * @param messageTag The message to create the configuration from.
     * @return An new configuration object with the values derived from the NBTTagCompound.
     */
    @Override
    public TreeFarmConfiguration ReadFromNBTTagCompound(NBTTagCompound messageTag) {
        TreeFarmConfiguration config = new TreeFarmConfiguration();

        return (TreeFarmConfiguration) super.ReadFromNBTTagCompound(messageTag, config);
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
        StructureTreeFarm structure = StructureTreeFarm
            .CreateInstance(StructureTreeFarm.ASSETLOCATION, StructureTreeFarm.class);
        structure.BuildStructure(this, world, hitBlockPos, EnumFacing.NORTH, player);

        player.inventory.consumeInventoryItem(ModRegistry.TreeFarm());
        player.inventoryContainer.detectAndSendChanges();
    }
}
