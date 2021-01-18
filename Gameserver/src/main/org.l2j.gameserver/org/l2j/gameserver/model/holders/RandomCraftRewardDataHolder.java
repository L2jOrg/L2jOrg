package org.l2j.gameserver.model.holders;

public class RandomCraftRewardDataHolder
{
    private final int _itemId;
    private final long _count;
    private final double _chance;
    private final boolean _announce;

    public RandomCraftRewardDataHolder(int itemId, long count, double chance, boolean announce)
    {
        _itemId = itemId;
        _count = count;
        _chance = chance;
        _announce = announce;
    }

    public int getItemId()
    {
        return _itemId;
    }

    public long getCount()
    {
        return _count;
    }

    public double getChance()
    {
        return _chance;
    }

    public boolean isAnnounce()
    {
        return _announce;
    }
}
