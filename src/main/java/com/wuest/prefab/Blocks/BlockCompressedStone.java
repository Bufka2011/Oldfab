package com.wuest.prefab.Blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.wuest.prefab.Prefab;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Compressed stone, glowstone and dirt blocks.
 *
 * @author WuestMan
 */
public class BlockCompressedStone extends Block implements IMetaBlock {

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public BlockCompressedStone() {
        super(Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setHardness(1.5F);
        this.setResistance(10.0F);
        this.setHarvestLevel("pickaxe", 0);
        this.setStepSound(Block.soundTypeStone);
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);

        if (block != this) {
            return block.getLightValue(world, x, y, z);
        }

        EnumType meta = EnumType.byMetadata(world.getBlockMetadata(x, y, z));

        if (meta == EnumType.COMPRESSED_GLOWSTONE || meta == EnumType.DOUBLE_COMPRESSED_GLOWSTONE) {
            return 15;
        }

        return super.getLightValue(world, x, y, z);
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }

    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta) {
        return true;
    }

    @Override
    public int damageDropped(int meta) {
        return EnumType.byMetadata(meta)
            .getMetadata();
    }

    @Override
    public int getDamageValue(World world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z);
    }

    @Override
    public String getSpecialName(ItemStack stack) {
        return EnumType.byMetadata(stack.getItemDamage()).name;
    }

    @Override
    public String getMetaDataUnLocalizedName(int metaData) {
        return EnumType.byMetadata(metaData).unlocalizedName;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        for (EnumType enumType : EnumType.values()) {
            list.add(new ItemStack(itemIn, 1, enumType.getMetadata()));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.icons = new IIcon[EnumType.values().length];

        for (EnumType type : EnumType.values()) {
            this.icons[type.getMetadata()] = register.registerIcon(Prefab.MODID + ":" + type.getUnlocalizedName());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return this.icons[EnumType.byMetadata(meta)
            .getMetadata()];
    }

    public enum EnumType {

        COMPRESSED_STONE(0, "block_compressed_stone"),
        DOUBLE_COMPRESSED_STONE(1, "block_double_compressed_stone"),
        TRIPLE_COMPRESSED_STONE(2, "block_triple_compressed_stone"),
        COMPRESSED_GLOWSTONE(3, "block_compressed_glowstone"),
        DOUBLE_COMPRESSED_GLOWSTONE(4, "block_double_compressed_glowstone"),
        COMPRESSED_DIRT(5, "block_compressed_dirt"),
        DOUBLE_COMPRESSED_DIRT(6, "block_double_compressed_dirt");

        private static final EnumType[] META_LOOKUP = new EnumType[values().length];

        private final int meta;
        private final String name;
        private final String unlocalizedName;

        EnumType(int meta, String name) {
            this.meta = meta;
            this.name = name;
            this.unlocalizedName = name;
        }

        public int getMetadata() {
            return this.meta;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        public String getUnlocalizedName() {
            return this.unlocalizedName;
        }

        public static EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        static {
            for (EnumType type : values()) {
                META_LOOKUP[type.getMetadata()] = type;
            }
        }
    }
}
