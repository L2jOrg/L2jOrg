package org.l2j.gameserver.engine.item.container.listener;

import org.l2j.gameserver.api.item.PlayerInventoryListener;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.items.BodyPart;
import org.l2j.gameserver.model.items.instance.Item;

/**
 * @author JoeAlisson
 */
public final class BraceletListener implements PlayerInventoryListener {

    private BraceletListener() {

    }

    @Override
    public void notifyUnequiped(InventorySlot slot, Item item, Inventory inventory) {
        if (item.getBodyPart() == BodyPart.RIGHT_BRACELET) {
            InventorySlot.talismans().forEach(inventory::unEquipItemInSlot);
        }
    }

    // Note (April 3, 2009): Currently on equip, talismans do not display properly, do we need checks here to fix this?
    @Override
    public void notifyEquiped(InventorySlot slot, Item item, Inventory inventory) {
    }

    public static BraceletListener provider() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final BraceletListener INSTANCE = new BraceletListener();
    }
}
