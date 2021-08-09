package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.enums.LampType;

public class MagicLampHolder
{
    private final MagicLampDataHolder _lamp;
    private int _count;
    private long _exp;
    private long _sp;

    public MagicLampHolder(MagicLampDataHolder lamp)
    {
        _lamp = lamp;
    }

    public void inc()
    {
        _count++;
        _exp += _lamp.getExp();
        _sp += _lamp.getSp();
    }

    public LampType getType()
    {
        return _lamp.getType();
    }

    public int getCount()
    {
        return _count;
    }

    public long getExp()
    {
        return _exp;
    }

    public long getSp()
    {
        return _sp;
    }
}