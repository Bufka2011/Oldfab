package com.wuest.prefab.Config.Structures;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.StructureGen.CustomStructures.StructureChickenCoop;
import com.wuest.prefab.Util.BlockPos;
import com.wuest.prefab.Util.EnumFacing;

/**
 * 
 * @author WuestMan
 *
 */
public class ChickenCoopConfiguration extends StructureConfiguration {

    /**
     * Custom method to read the NBTTagCompound message.
     * 
     * @param messageTag The message to create the configuration from.
     * @return An new configuration object with the values derived from the NBTTagCompound.
     */
    @Override
    public ChickenCoopConfiguration ReadFromNBTTagCompound(NBTTagCompound messageTag) {
        ChickenCoopConfiguration config = new ChickenCoopConfiguration();

        return (ChickenCoopConfiguration) super.ReadFromNBTTagCompound(messageTag, config);
    }

    @Override
    protected void ConfigurationSpecificBuildStructure(EntityPlayer player, World world, BlockPos hitBlockPos) {
        StructureChickenCoop structure = StructureChickenCoop
            .CreateInstance(StructureChickenCoop.ASSETLOCATION, StructureChickenCoop.class);

        if (structure.BuildStructure(this, world, hitBlockPos, EnumFacing.NORTH, player)) {
            player.inventory.consumeInventoryItem(ModRegistry.ChickenCoop());
            player.inventoryContainer.detectAndSendChanges();
        }
    }
}
