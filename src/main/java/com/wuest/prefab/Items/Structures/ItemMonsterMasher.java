package com.wuest.prefab.Items.Structures;

import com.wuest.prefab.ModRegistry;

/**
 * A structure item which opens the structure configuration GUI when used.
 *
 * @author WuestMan
 */
public class ItemMonsterMasher extends StructureItem {

    public ItemMonsterMasher(String name) {
        super(name, ModRegistry.GuiMonsterMasher);
    }
}
