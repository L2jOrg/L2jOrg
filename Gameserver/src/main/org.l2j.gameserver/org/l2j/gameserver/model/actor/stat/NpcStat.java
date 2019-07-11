package org.l2j.gameserver.model.actor.stat;

import org.l2j.gameserver.model.actor.Npc;

public class NpcStat extends CharStat {
    public NpcStat(Npc activeChar) {
        super(activeChar);
    }

    @Override
    public byte getLevel() {
        return getActiveChar().getTemplate().getLevel();
    }

    @Override
    public Npc getActiveChar() {
        return (Npc) super.getActiveChar();
    }
}
