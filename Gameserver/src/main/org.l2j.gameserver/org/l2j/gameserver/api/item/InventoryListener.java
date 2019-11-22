package org.l2j.gameserver.api.item;

import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.items.instance.Item;

public interface InventoryListener {

    void notifyEquiped(InventorySlot slot, Item inst, Inventory inventory);

    void notifyUnequiped(InventorySlot slot, Item inst, Inventory inventory);
}
