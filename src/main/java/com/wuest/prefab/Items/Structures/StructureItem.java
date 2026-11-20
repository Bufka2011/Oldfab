package com.wuest.prefab.Items.Structures;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Prefab;

/**
 * The base class for all of the structure items. Right clicking the top of a block opens the structure GUI.
 *
 * @author WuestMan
 */
public class StructureItem extends Item {

    protected int guiId = 0;

    public StructureItem(String name, int guiId) {
        super();
        this.Initialize(name, guiId);
    }

    /**
     * Does something when the item is right-clicked.
     */
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            // Side 1 is the top of the block.
            if (side == 1) {
                // Open the client side gui to determine the house options.
                player.openGui(Prefab.instance, this.guiId, player.worldObj, x, y, z);
                return true;
            }
        }

        return false;
    }

    /**
     * Initializes common fields/properties for this structure item.
     */
    protected void Initialize(String name, int guiId) {
        ModRegistry.setItemName(this, name);
        this.guiId = guiId;
        this.setCreativeTab(CreativeTabs.tabMisc);
    }
}
