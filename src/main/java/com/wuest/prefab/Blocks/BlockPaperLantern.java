package com.wuest.prefab.Blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.wuest.prefab.Prefab;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * A small paper lantern which gives off light.
 *
 * @author WuestMan
 */
public class BlockPaperLantern extends Block {

    public static final Material BlockMaterial = new SeeThroughMaterial(MapColor.airColor);

    /**
     * The render type id for this block. This is set by the client proxy.
     */
    public static int renderType = -1;

    @SideOnly(Side.CLIENT)
    public IIcon stickIcon;

    @SideOnly(Side.CLIENT)
    public IIcon stickSideIcon;

    @SideOnly(Side.CLIENT)
    public IIcon torchIcon;

    public BlockPaperLantern(String name) {
        super(BlockMaterial);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setStepSound(Block.soundTypeSnow);
        this.setHardness(0.6F);
        this.setLightLevel(0.9375F);
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
    public boolean canPlaceTorchOnTop(World world, int x, int y, int z) {
        return false;
    }

    @Override
    public int getRenderType() {
        return BlockPaperLantern.renderType;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.blockIcon = register.registerIcon(Prefab.MODID + ":paper_lantern_side");
        this.stickIcon = register.registerIcon(Prefab.MODID + ":stick");
        this.stickSideIcon = register.registerIcon(Prefab.MODID + ":stick_side");
        this.torchIcon = register.registerIcon("torch_on");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        double d0 = x + 0.5D;
        double d1 = y + 0.7D;
        double d2 = z + 0.5D;
        world.spawnParticle("smoke", d0, d1, d2, 0.0D, 0.0D, 0.0D);
        world.spawnParticle("flame", d0, d1, d2, 0.0D, 0.0D, 0.0D);
    }

    /**
     * A material which doesn't block light.
     */
    public static class SeeThroughMaterial extends Material {

        public SeeThroughMaterial(MapColor color) {
            super(color);
        }

        @Override
        public boolean isOpaque() {
            return false;
        }

        @Override
        public boolean blocksMovement() {
            return true;
        }

        @Override
        public boolean getCanBlockGrass() {
            return false;
        }

        public SeeThroughMaterial setImmovable() {
            this.setImmovableMobility();
            return this;
        }
    }
}
