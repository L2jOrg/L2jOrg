package org.l2j.gameserver.mobius.gameserver.model.itemcontainer;

import org.l2j.gameserver.mobius.gameserver.enums.ItemLocation;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

public class PcWarehouse extends Warehouse {
    private final L2PcInstance _owner;

    public PcWarehouse(L2PcInstance owner) {
        _owner = owner;
    }

    @Override
    public String getName() {
        return "Warehouse";
    }

    @Override
    public L2PcInstance getOwner() {
        return _owner;
    }

    @Override
    public ItemLocation getBaseLocation() {
        return ItemLocation.WAREHOUSE;
    }

    @Override
    public boolean validateCapacity(long slots) {
        return ((_items.size() + slots) <= _owner.getWareHouseLimit());
    }
}
