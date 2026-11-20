package com.wuest.prefab.Items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.wuest.prefab.Blocks.IMetaBlock;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * An item block for blocks which have multiple variants stored in metadata.
 *
 * @author WuestMan
 */
public class ItemBlockMeta extends ItemBlock {

    public ItemBlockMeta(Block block) {
        super(block);

        if (!(block instanceof IMetaBlock)) {
            throw new IllegalArgumentException(
                String.format("The given Block %s is not an instance of IMetaBlockName!", block.getUnlocalizedName()));
        }

        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return this.field_150939_a.getIcon(2, damage);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + ((IMetaBlock) this.field_150939_a).getSpecialName(stack);
    }
}
