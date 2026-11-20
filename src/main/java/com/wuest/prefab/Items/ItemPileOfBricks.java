package com.wuest.prefab.Items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import com.wuest.prefab.ModRegistry;

/**
 * 
 * @author WuestMan
 *
 */
public class ItemPileOfBricks extends Item {

    public ItemPileOfBricks(String name) {
        super();

        this.setCreativeTab(CreativeTabs.tabMaterials);
        ModRegistry.setItemName(this, name);
    }
}
