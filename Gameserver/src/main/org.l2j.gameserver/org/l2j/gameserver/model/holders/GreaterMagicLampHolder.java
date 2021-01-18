package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.StatsSet;

public class GreaterMagicLampHolder
{
    private final int _itemId;
    private final long _count;

    public GreaterMagicLampHolder(StatsSet params)
    {
        _itemId = params.getInt("item");
        _count = params.getLong("count");
    }

    public int getItemId()
    {
        return _itemId;
    }

    public long getCount()
    {
        return _count;
    }
}