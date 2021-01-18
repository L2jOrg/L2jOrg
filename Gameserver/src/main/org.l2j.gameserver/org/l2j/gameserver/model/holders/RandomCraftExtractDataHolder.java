package org.l2j.gameserver.model.holders;

public class RandomCraftExtractDataHolder
{
    private final long _points;
    private final long _fee;

    public RandomCraftExtractDataHolder(long points, long fee)
    {
        _points = points;
        _fee = fee;
    }

    public long getPoints()
    {
        return _points;
    }

    public long getFee()
    {
        return _fee;
    }
}
