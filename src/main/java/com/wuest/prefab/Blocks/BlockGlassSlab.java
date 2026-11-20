package com.wuest.prefab.Blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.wuest.prefab.ModRegistry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * The base class for the half and double glass slabs.
 *
 * @author WuestMan
 */
public abstract class BlockGlassSlab extends BlockSlab {

    public BlockGlassSlab(boolean isDouble) {
        super(isDouble, Material.glass);
        this.setHardness(0.5F);
        this.setStepSound(Block.soundTypeGlass);
        this.setLightOpacity(1);

        if (!isDouble) {
            this.setCreativeTab(CreativeTabs.tabBlock);
        }

        this.useNeighborBrightness = true;
    }

    @Override
    public String func_150002_b(int meta) {
        return this.getUnlocalizedName();
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return Item.getItemFromBlock(ModRegistry.GlassSlab());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z) {
        return Item.getItemFromBlock(ModRegistry.GlassSlab());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        Block block = world.getBlock(x, y, z);

        if (this.field_150004_a && (block == Blocks.glass || block instanceof BlockGlassSlab)) {
            return false;
        }

        return super.shouldSideBeRendered(world, x, y, z, side);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.blockIcon = register.registerIcon("glass");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return this.blockIcon;
    }
}
