package org.l2j.gameserver.mobius.gameserver.model.itemcontainer;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.enums.ItemLocation;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author UnAfraid
 */
public class PcFreight extends ItemContainer
{
    private final L2PcInstance _owner;
    private final int _ownerId;

    public PcFreight(int object_id)
    {
        _owner = null;
        _ownerId = object_id;
        restore();
    }

    public PcFreight(L2PcInstance owner)
    {
        _owner = owner;
        _ownerId = owner.getObjectId();
    }

    @Override
    public int getOwnerId()
    {
        return _ownerId;
    }

    @Override
    public L2PcInstance getOwner()
    {
        return _owner;
    }

    @Override
    public ItemLocation getBaseLocation()
    {
        return ItemLocation.FREIGHT;
    }

    @Override
    public String getName()
    {
        return "Freight";
    }

    @Override
    public boolean validateCapacity(long slots)
    {
        final int curSlots = _owner == null ? Config.ALT_FREIGHT_SLOTS : Config.ALT_FREIGHT_SLOTS;
        return ((getSize() + slots) <= curSlots);
    }

    @Override
    public void refreshWeight()
    {
    }
}