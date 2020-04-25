package org.l2j.gameserver.model.item.container;

import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;

/**
 * @author DS
 */
public class PlayerRefund extends ItemContainer {
    private final Player _owner;

    public PlayerRefund(Player owner) {
        _owner = owner;
    }

    @Override
    public String getName() {
        return "Refund";
    }

    @Override
    public Player getOwner() {
        return _owner;
    }

    @Override
    public ItemLocation getBaseLocation() {
        return ItemLocation.REFUND;
    }

    @Override
    protected void addItem(Item item) {
        super.addItem(item);
        try {
            if (getSize() > 12) {
                final Item removedItem = items.remove(0);
                if (removedItem != null) {
                    ItemEngine.getInstance().destroyItem("ClearRefund", removedItem, getOwner(), null);
                    removedItem.updateDatabase(true);
                }
            }
        } catch (Exception e) {
            LOGGER.error("addItem()", e);
        }
    }

    @Override
    public void refreshWeight() {
    }

    @Override
    public void deleteMe() {
        try {
            for (Item item : items.values()) {
                ItemEngine.getInstance().destroyItem("ClearRefund", item, getOwner(), null);
                item.updateDatabase(true);
            }
        } catch (Exception e) {
            LOGGER.error("deleteMe()", e);
        }
        items.clear();
    }

    @Override
    public void restore() {
    }
}