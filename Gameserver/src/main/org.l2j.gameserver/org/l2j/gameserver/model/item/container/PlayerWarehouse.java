package org.l2j.gameserver.model.item.container;

import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.actor.instance.Player;

public class PlayerWarehouse extends Warehouse {
    private final Player _owner;

    public PlayerWarehouse(Player owner) {
        _owner = owner;
    }

    @Override
    public String getName() {
        return "Warehouse";
    }

    @Override
    public Player getOwner() {
        return _owner;
    }

    @Override
    public ItemLocation getBaseLocation() {
        return ItemLocation.WAREHOUSE;
    }

    @Override
    public boolean validateCapacity(long slots) {
        return ((items.size() + slots) <= _owner.getWareHouseLimit());
    }

    @Override
    public WarehouseType getType() {
        return WarehouseType.PRIVATE;
    }
}
