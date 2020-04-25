package org.l2j.gameserver.model.item.container;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class PlayerFreight extends Warehouse {
    private final Player _owner;
    private final int _ownerId;

    public PlayerFreight(int object_id) {
        _owner = null;
        _ownerId = object_id;
        restore();
    }

    public PlayerFreight(Player owner) {
        _owner = owner;
        _ownerId = owner.getObjectId();
    }

    @Override
    public int getOwnerId() {
        return _ownerId;
    }

    @Override
    public Player getOwner() {
        return _owner;
    }

    @Override
    public ItemLocation getBaseLocation() {
        return ItemLocation.FREIGHT;
    }

    @Override
    public String getName() {
        return "Freight";
    }

    @Override
    public boolean validateCapacity(long slots) {
        final int curSlots = _owner == null ? Config.ALT_FREIGHT_SLOTS : Config.ALT_FREIGHT_SLOTS;
        return ((getSize() + slots) <= curSlots);
    }

    @Override
    public void refreshWeight() {
    }

    @Override
    public WarehouseType getType() {
        return WarehouseType.PRIVATE;
    }
}