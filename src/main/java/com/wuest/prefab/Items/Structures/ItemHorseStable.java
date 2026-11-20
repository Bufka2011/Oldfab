package com.wuest.prefab.Items.Structures;

import com.wuest.prefab.ModRegistry;

/**
 * A structure item which opens the structure configuration GUI when used.
 *
 * @author WuestMan
 */
public class ItemHorseStable extends StructureItem {

    public ItemHorseStable(String name) {
        super(name, ModRegistry.GuiHorseStable);
    }
}
