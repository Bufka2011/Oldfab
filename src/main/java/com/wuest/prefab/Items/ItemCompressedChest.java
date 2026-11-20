package com.wuest.prefab.Items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.wuest.prefab.ModRegistry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * This is a condensed chest used in the construction of the warehouse.
 * 
 * @author WuestMan
 *
 */
public class ItemCompressedChest extends Item {

    /**
     * Initializes a new instance of the ItemCondensedChest class.
     * 
     * @param name The name of the item to register.
     */
    public ItemCompressedChest(String name) {
        super();

        this.setCreativeTab(CreativeTabs.tabMisc);
        ModRegistry.setItemName(this, name);
    }

    /**
     * allows items to add custom lines of information to the mouse-over description
     */
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        tooltip.add("Used in the recipes for structures, not for direct storage");
    }
}
