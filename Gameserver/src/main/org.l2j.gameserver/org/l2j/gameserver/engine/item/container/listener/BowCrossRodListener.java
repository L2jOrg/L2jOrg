package org.l2j.gameserver.engine.item.container.listener;

import org.l2j.gameserver.api.item.PlayerInventoryListener;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.items.type.WeaponType;

/**
 * @author JoeAlisson
 */
public final class BowCrossRodListener implements PlayerInventoryListener {

    private BowCrossRodListener() {

    }

    @Override
    public void notifyUnequiped(int slot, Item item, Inventory inventory) {
        if (slot != Inventory.PAPERDOLL_RHAND) {
            return;
        }

        if (item.getItemType() == WeaponType.BOW) {
            final Item arrow = inventory.getPaperdollItem(Inventory.PAPERDOLL_LHAND);
            if (arrow != null) {
                inventory.setPaperdollItem(Inventory.PAPERDOLL_LHAND, null);
            }
        }
        else if ((item.getItemType() == WeaponType.CROSSBOW) || (item.getItemType() == WeaponType.TWO_HAND_CROSSBOW)) {
            final Item bolts = inventory.getPaperdollItem(Inventory.PAPERDOLL_LHAND);
            if (bolts != null) {
                inventory.setPaperdollItem(Inventory.PAPERDOLL_LHAND, null);
            }
        }else if (item.getItemType() == WeaponType.FISHING_ROD) {
            final Item lure = inventory.getPaperdollItem(Inventory.PAPERDOLL_LHAND);
            if (lure != null) {
                inventory.setPaperdollItem(Inventory.PAPERDOLL_LHAND, null);
            }
        }
    }

    @Override
    public void notifyEquiped(int slot, Item item, Inventory inventory) {
        if (slot != Inventory.PAPERDOLL_RHAND) {
            return;
        }

        if (item.getItemType() == WeaponType.BOW) {
            final Item arrow = inventory.findArrowForBow(item.getTemplate());
            if (arrow != null) {
                inventory.setPaperdollItem(Inventory.PAPERDOLL_LHAND, arrow);
            }
        } else if ((item.getItemType() == WeaponType.CROSSBOW) || (item.getItemType() == WeaponType.TWO_HAND_CROSSBOW)) {
            final Item bolts = inventory.findBoltForCrossBow(item.getTemplate());
            if (bolts != null) {
                inventory.setPaperdollItem(Inventory.PAPERDOLL_LHAND, bolts);
            }
        }
    }

    public static BowCrossRodListener provider() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final BowCrossRodListener INSTANCE = new BowCrossRodListener();
    }
}
