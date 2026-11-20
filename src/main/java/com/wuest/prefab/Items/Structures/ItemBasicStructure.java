package com.wuest.prefab.Items.Structures;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import com.wuest.prefab.Config.Structures.BasicStructureConfiguration;
import com.wuest.prefab.Config.Structures.BasicStructureConfiguration.EnumBasicStructureName;
import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Prefab;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * This class is used for basic structures to show items. In 1.10 the structure configuration was stored in a
 * capability; Minecraft 1.7.10 doesn't have capabilities so the configuration is stored in the stack's NBT data.
 *
 * @author WuestMan
 */
public class ItemBasicStructure extends StructureItem {

    public static final String STRUCTURE_TAG = Prefab.MODID + ":structuresconfiguration";

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemBasicStructure(String name) {
        super(name, ModRegistry.GuiBasicStructure);
        this.setHasSubtypes(true);
    }

    /**
     * Creates an item stack for the specified basic structure.
     */
    public static ItemStack getStack(EnumBasicStructureName name) {
        ItemStack stack = new ItemStack(ModRegistry.BasicStructure(), 1, name.ordinal());
        BasicStructureConfiguration configuration = new BasicStructureConfiguration();
        configuration.basicStructureName = name;
        ItemBasicStructure.setConfiguration(stack, configuration);
        return stack;
    }

    /**
     * Gets the basic structure configuration stored on this stack.
     */
    public static BasicStructureConfiguration getConfiguration(ItemStack stack) {
        BasicStructureConfiguration configuration = new BasicStructureConfiguration();

        if (stack == null) {
            return configuration;
        }

        if (stack.hasTagCompound() && stack.getTagCompound()
            .hasKey(ItemBasicStructure.STRUCTURE_TAG)) {
            configuration = configuration.ReadFromNBTTagCompound(
                stack.getTagCompound()
                    .getCompoundTag(ItemBasicStructure.STRUCTURE_TAG));
        } else if (stack.getItemDamage() > 0 && stack.getItemDamage() < EnumBasicStructureName.values().length) {
            // Fall back to the damage value.
            configuration.basicStructureName = EnumBasicStructureName.values()[stack.getItemDamage()];
        }

        return configuration;
    }

    /**
     * Saves the basic structure configuration to the stack.
     */
    public static void setConfiguration(ItemStack stack, BasicStructureConfiguration configuration) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("structureEnumName", configuration.basicStructureName.name());

        if (configuration.structureDisplayName != null) {
            tag.setString("structureDisplayName", configuration.structureDisplayName);
        }

        stack.getTagCompound()
            .setTag(ItemBasicStructure.STRUCTURE_TAG, tag);
        stack.setItemDamage(configuration.basicStructureName.ordinal());
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
        for (EnumBasicStructureName value : EnumBasicStructureName.values()) {
            if (value.getResourceLocation() != null) {
                subItems.add(ItemBasicStructure.getStack(value));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        EnumBasicStructureName[] values = EnumBasicStructureName.values();
        this.icons = new IIcon[values.length];

        for (EnumBasicStructureName value : values) {
            if (value.getResourceLocation() != null) {
                this.icons[value.ordinal()] = register.registerIcon(
                    value.getResourceLocation()
                        .toString());
            }
        }

        this.itemIcon = register.registerIcon(Prefab.MODID + ":item_blueprint_base");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        if (damage >= 0 && damage < this.icons.length && this.icons[damage] != null) {
            return this.icons[damage];
        }

        return this.itemIcon;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        BasicStructureConfiguration configuration = ItemBasicStructure.getConfiguration(stack);

        if (configuration.basicStructureName != EnumBasicStructureName.Custom) {
            return configuration.getDisplayName();
        }

        return this.getUnlocalizedName();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        BasicStructureConfiguration configuration = ItemBasicStructure.getConfiguration(stack);

        if (configuration.basicStructureName != EnumBasicStructureName.Custom) {
            return StatCollector.translateToLocal(configuration.getDisplayName())
                .trim();
        }

        return super.getItemStackDisplayName(stack);
    }

    /**
     * Gets the basic structure item the player is holding.
     *
     * @return Null if the player isn't holding a basic structure.
     */
    public static ItemStack getBasicStructureItemInHand(EntityPlayer player) {
        ItemStack stack = player.getCurrentEquippedItem();

        if (stack != null && stack.getItem() instanceof ItemBasicStructure) {
            return stack;
        }

        return null;
    }
}
