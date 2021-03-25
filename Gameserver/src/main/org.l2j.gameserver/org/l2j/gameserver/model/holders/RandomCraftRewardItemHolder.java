package org.l2j.gameserver.model.holders;

public class RandomCraftRewardItemHolder {
    private final int _id;
    private final long _count;
    private boolean _locked;
    private int _lockLeft;

    public RandomCraftRewardItemHolder(int id, long count, boolean locked, int lockLeft)
    {
        _id = id;
        _count = count;
        _locked = locked;
        _lockLeft = lockLeft;
    }

    public int getItemId()
    {
        return _id;
    }

    public long getItemCount()
    {
        return _count;
    }

    public boolean isLocked()
    {
        return _locked;
    }

    public int getLockLeft()
    {
        return _lockLeft;
    }

    public void lock()
    {
        _locked = true;
    }

    public void decLock()
    {
        _lockLeft--;
        if (_lockLeft <= 0)
        {
            _locked = false;
        }
    }
}
