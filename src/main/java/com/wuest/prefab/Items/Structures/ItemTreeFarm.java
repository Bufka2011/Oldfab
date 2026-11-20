package com.wuest.prefab.Items.Structures;

import com.wuest.prefab.ModRegistry;

/**
 * A structure item which opens the structure configuration GUI when used.
 *
 * @author WuestMan
 */
public class ItemTreeFarm extends StructureItem {

    public ItemTreeFarm(String name) {
        super(name, ModRegistry.GuiTreeFarm);
    }
}
