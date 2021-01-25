package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.enums.LampMode;
import org.l2j.gameserver.enums.LampType;
import org.l2j.gameserver.model.StatsSet;

public class MagicLampDataHolder
{
    private final LampMode _mode;
    private final LampType _type;
    private final long _exp;
    private final long _sp;
    private final double _chance;

    public MagicLampDataHolder(StatsSet params)
    {
        _mode = params.getEnum("mode", LampMode.class);
        _type = params.getEnum("type", LampType.class);
        _exp = params.getLong("exp");
        _sp = params.getLong("sp");
        _chance = params.getDouble("chance");
    }

    public LampMode getMode()
    {
        return _mode;
    }

    public LampType getType()
    {
        return _type;
    }

    public long getExp()
    {
        return _exp;
    }

    public long getSp()
    {
        return _sp;
    }

    public double getChance()
    {
        return _chance;
    }
}