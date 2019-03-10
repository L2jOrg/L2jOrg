package org.l2j.gameserver.mobius.gameserver.model.itemcontainer;

import org.l2j.gameserver.mobius.gameserver.datatables.ItemTable;
import org.l2j.gameserver.mobius.gameserver.enums.ItemLocation;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;

import java.util.logging.Level;

/**
 * @author DS
 */
public class PcRefund extends ItemContainer {
    private final L2PcInstance _owner;

    public PcRefund(L2PcInstance owner) {
        _owner = owner;
    }

    @Override
    public String getName() {
        return "Refund";
    }

    @Override
    public L2PcInstance getOwner() {
        return _owner;
    }

    @Override
    public ItemLocation getBaseLocation() {
        return ItemLocation.REFUND;
    }

    @Override
    protected void addItem(L2ItemInstance item) {
        super.addItem(item);
        try {
            if (getSize() > 12) {
                final L2ItemInstance removedItem = _items.remove(0);
                if (removedItem != null) {
                    ItemTable.getInstance().destroyItem("ClearRefund", removedItem, getOwner(), null);
                    removedItem.updateDatabase(true);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "addItem()", e);
        }
    }

    @Override
    public void refreshWeight() {
    }

    @Override
    public void deleteMe() {
        try {
            for (L2ItemInstance item : _items.values()) {
                ItemTable.getInstance().destroyItem("ClearRefund", item, getOwner(), null);
                item.updateDatabase(true);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "deleteMe()", e);
        }
        _items.clear();
    }

    @Override
    public void restore() {
    }
}