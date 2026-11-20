package com.wuest.prefab.Items.Structures;

import com.wuest.prefab.ModRegistry;

/**
 * A structure item which opens the structure configuration GUI when used.
 *
 * @author WuestMan
 */
public class ItemVillagerHouses extends StructureItem {

    public ItemVillagerHouses(String name) {
        super(name, ModRegistry.GuiVillagerHouses);
        this.setMaxDamage(10);
        this.setMaxStackSize(1);
    }
}
