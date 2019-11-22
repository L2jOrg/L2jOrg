package org.l2j.gameserver.engine.item.container.listener;

import org.l2j.gameserver.api.item.PlayerInventoryListener;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.items.BodyPart;
import org.l2j.gameserver.model.items.instance.Item;

import static org.l2j.gameserver.model.items.BodyPart.AGATHION;

/**
 * @author JoeAlisson
 */
public final class AgathionBraceletListener implements PlayerInventoryListener {

    private AgathionBraceletListener() {

    }

    @Override
    public void notifyUnequiped(InventorySlot slot, Item item, Inventory inventory) {
        if (item.getBodyPart() == BodyPart.LEFT_BRACELET) {
            for (int paperdoll = AGATHION.slot(); paperdoll < AGATHION.slot() + 5; paperdoll++) {
                inventory.unEquipItemInSlot(paperdoll);
            }
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
