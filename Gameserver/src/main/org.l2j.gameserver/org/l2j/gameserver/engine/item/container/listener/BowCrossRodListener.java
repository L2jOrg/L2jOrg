package org.l2j.gameserver.engine.item.container.listener;

import org.l2j.gameserver.api.item.PlayerInventoryListener;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.items.type.WeaponType;

import static org.l2j.commons.util.Util.doIfNonNull;

/**
 * @author JoeAlisson
 */
public final class BowCrossRodListener implements PlayerInventoryListener {

    private BowCrossRodListener() {

    }

    @Override
    public void notifyUnequiped(InventorySlot slot, Item item, Inventory inventory) {
        if (slot != InventorySlot.RIGHT_HAND && slot != InventorySlot.TWO_HAND) {
            return;
        }

        if (item.getItemType() == WeaponType.BOW) {
            doIfNonNull(inventory.getPaperdollItem(InventorySlot.LEFT_HAND), i -> inventory.setPaperdollItem(InventorySlot.LEFT_HAND, null));
        }
        else if ((item.getItemType() == WeaponType.CROSSBOW) || (item.getItemType() == WeaponType.TWO_HAND_CROSSBOW)) {
            doIfNonNull(inventory.getPaperdollItem(InventorySlot.LEFT_HAND), i -> inventory.setPaperdollItem(InventorySlot.LEFT_HAND, null));
        }else if (item.getItemType() == WeaponType.FISHING_ROD) {
            doIfNonNull(inventory.getPaperdollItem(InventorySlot.LEFT_HAND), i -> inventory.setPaperdollItem(InventorySlot.LEFT_HAND, null));
        }
    }

    @Override
    public void notifyEquiped(InventorySlot slot, Item item, Inventory inventory) {
        if (slot != InventorySlot.RIGHT_HAND && slot != InventorySlot.TWO_HAND  ) {
            return;
        }

        if (item.getItemType() == WeaponType.BOW) {
            doIfNonNull(inventory.findArrowForBow(item.getTemplate()), arrow -> inventory.setPaperdollItem(InventorySlot.LEFT_HAND, arrow));
        } else if ((item.getItemType() == WeaponType.CROSSBOW) || (item.getItemType() == WeaponType.TWO_HAND_CROSSBOW)) {
            doIfNonNull(inventory.findBoltForCrossBow(item.getTemplate()), bolts -> inventory.setPaperdollItem(InventorySlot.LEFT_HAND, bolts));
        }
    }

    public static BowCrossRodListener provider() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final BowCrossRodListener INSTANCE = new BowCrossRodListener();
    }
}
