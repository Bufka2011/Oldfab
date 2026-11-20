package com.wuest.prefab.Blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Stairs made out of glass.
 *
 * @author WuestMan
 */
public class BlockGlassStairs extends BlockStairs {

    public BlockGlassStairs(String name) {
        super(Blocks.glass, 0);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setHardness(0.5F);
        this.setStepSound(Block.soundTypeGlass);
        this.setLightOpacity(1);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        Block block = world.getBlock(x, y, z);

        if (block == Blocks.glass || block instanceof BlockGlassStairs || block instanceof BlockGlassSlab) {
            return false;
        }

        return true;
    }
}
