package org.l2j.gameserver.mobius.gameserver.model.holders;

import org.l2j.gameserver.mobius.gameserver.enums.DropType;

/**
 * @author Mobius
 */
public class DropHolder
{
    private final DropType _dropType;
    private final int _itemId;
    private final long _min;
    private final long _max;
    private final double _chance;

    public DropHolder(DropType dropType, int itemId, long min, long max, double chance)
    {
        _dropType = dropType;
        _itemId = itemId;
        _min = min;
        _max = max;
        _chance = chance;
    }

    public DropType getDropType()
    {
        return _dropType;
    }

    public int getItemId()
    {
        return _itemId;
    }

    public long getMin()
    {
        return _min;
    }

    public long getMax()
    {
        return _max;
    }

    public double getChance()
    {
        return _chance;
    }
}
