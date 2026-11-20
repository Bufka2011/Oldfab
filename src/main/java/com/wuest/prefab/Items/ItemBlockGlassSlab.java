package com.wuest.prefab.Items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemSlab;

import com.wuest.prefab.Blocks.BlockDoubleGlassSlab;
import com.wuest.prefab.Blocks.BlockHalfGlassSlab;

/**
 * The item block for glass slabs.
 *
 * @author WuestMan
 */
public class ItemBlockGlassSlab extends ItemSlab {

    public ItemBlockGlassSlab(Block block, BlockHalfGlassSlab singleSlab, BlockDoubleGlassSlab doubleSlab,
        Boolean isDouble) {
        super(block, singleSlab, doubleSlab, isDouble);
    }
}
