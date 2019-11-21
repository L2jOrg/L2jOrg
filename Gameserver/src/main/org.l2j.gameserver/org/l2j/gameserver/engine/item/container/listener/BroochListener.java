package org.l2j.gameserver.engine.item.container.listener;

import org.l2j.gameserver.api.item.PlayerInventoryListener;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.items.BodyPart;
import org.l2j.gameserver.model.items.instance.Item;

/**
 * @author JoeAlisson
 */
public final class BroochListener implements PlayerInventoryListener {

    private BroochListener() {

    }

    @Override
    public void notifyUnequiped(int slot, Item item, Inventory inventory) {
        if (item.getBodyPart() == BodyPart.BROOCH) {
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_BROOCH_JEWEL1);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_BROOCH_JEWEL2);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_BROOCH_JEWEL3);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_BROOCH_JEWEL4);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_BROOCH_JEWEL5);
            inventory.unEquipItemInSlot(Inventory.PAPERDOLL_BROOCH_JEWEL6);
        }
    }

    // Note (April 3, 2009): Currently on equip, talismans do not display properly, do we need checks here to fix this?
    @Override
    public void notifyEquiped(int slot, Item item, Inventory inventory) {
    }

    public static BroochListener provider() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final BroochListener INSTANCE = new BroochListener();
    }
}
