package com.wuest.prefab.Items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import com.wuest.prefab.ModRegistry;

/**
 * @author WuestMan
 */
public class ItemBundleOfTimber extends Item {

    public ItemBundleOfTimber(String name) {
        super();

        this.setCreativeTab(CreativeTabs.tabMaterials);
        ModRegistry.setItemName(this, name);
    }
}
