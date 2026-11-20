package com.wuest.prefab.Items.Structures;

import com.wuest.prefab.ModRegistry;

/**
 * A structure item which opens the structure configuration GUI when used.
 *
 * @author WuestMan
 */
public class ItemStartHouse extends StructureItem {

    public ItemStartHouse(String name) {
        super(name, ModRegistry.GuiStartHouseChooser);
    }
}
