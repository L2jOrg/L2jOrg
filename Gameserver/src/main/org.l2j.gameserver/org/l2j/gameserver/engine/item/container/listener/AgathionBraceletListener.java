package org.l2j.gameserver.engine.item.container.listener;

import org.l2j.gameserver.api.item.PlayerInventoryListener;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.instance.Item;

/**
 * @author JoeAlisson
 */
public final class AgathionBraceletListener implements PlayerInventoryListener {

    private AgathionBraceletListener() {

    }

    @Override
    public void notifyUnequiped(InventorySlot slot, Item item, Inventory inventory) {
        if (item.getBodyPart() == BodyPart.LEFT_BRACELET) {
            InventorySlot.agathions().forEach(inventory::unEquipItemInSlot);
        }
    }

    @Override
    public void notifyEquiped(InventorySlot slot, Item item, Inventory inventory) {
    }


    public static AgathionBraceletListener provider() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final AgathionBraceletListener INSTANCE = new AgathionBraceletListener();
    }

}
