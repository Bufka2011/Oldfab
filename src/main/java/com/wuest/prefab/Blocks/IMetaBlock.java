package com.wuest.prefab.Blocks;

import net.minecraft.item.ItemStack;

/**
 * Implemented by blocks which use metadata for different variants.
 *
 * @author WuestMan
 */
public interface IMetaBlock {

    String getSpecialName(ItemStack stack);

    String getMetaDataUnLocalizedName(int metaData);
}
