package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnNpcFirstTalk implements IBaseEvent {
    private final L2Npc _npc;
    private final L2PcInstance _activeChar;

    public OnNpcFirstTalk(L2Npc npc, L2PcInstance activeChar) {
        _npc = npc;
        _activeChar = activeChar;
    }

    public L2Npc getNpc() {
        return _npc;
    }

    public L2PcInstance getActiveChar() {
        return _activeChar;
    }

    @Override
    public EventType getType() {
        return EventType.ON_NPC_FIRST_TALK;
    }
}
