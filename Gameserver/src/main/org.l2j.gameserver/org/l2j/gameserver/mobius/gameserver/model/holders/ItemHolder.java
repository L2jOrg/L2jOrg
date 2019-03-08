package org.l2j.gameserver.mobius.gameserver.model.holders;

import org.l2j.gameserver.mobius.gameserver.model.StatsSet;
import org.l2j.gameserver.mobius.gameserver.model.interfaces.IIdentifiable;

/**
 * A simple DTO for items; contains item ID and count.<br>
 * Extended by {@link ItemChanceHolder}, {@link QuestItemHolder}, {@link UniqueItemHolder}.
 * @author UnAfraid
 */
public class ItemHolder implements IIdentifiable
{
    private final int _id;
    private final long _count;

    public ItemHolder(StatsSet set)
    {
        _id = set.getInt("id");
        _count = set.getLong("count");
    }

    public ItemHolder(int id, long count)
    {
        _id = id;
        _count = count;
    }

    /**
     * @return the ID of the item contained in this object
     */
    @Override
    public int getId()
    {
        return _id;
    }

    /**
     * @return the count of items contained in this object
     */
    public long getCount()
    {
        return _count;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof ItemHolder))
        {
            return false;
        }
        else if (obj == this)
        {
            return true;
        }
        final ItemHolder objInstance = (ItemHolder) obj;
        return (_id == objInstance.getId()) && (_count == objInstance.getCount());
    }

    @Override
    public String toString()
    {
        return "[" + getClass().getSimpleName() + "] ID: " + _id + ", count: " + _count;
    }
}
