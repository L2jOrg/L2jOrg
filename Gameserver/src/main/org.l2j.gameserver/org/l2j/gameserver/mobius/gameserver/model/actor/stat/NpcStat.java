package org.l2j.gameserver.mobius.gameserver.model.actor.stat;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;

public class NpcStat extends CharStat
{
    public NpcStat(L2Npc activeChar)
    {
        super(activeChar);
    }

    @Override
    public byte getLevel()
    {
        return getActiveChar().getTemplate().getLevel();
    }

    @Override
    public L2Npc getActiveChar()
    {
        return (L2Npc) super.getActiveChar();
    }
}
