package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnNpcFirstTalk implements IBaseEvent {
    private final L2Npc _npc;
    private final Player _activeChar;

    public OnNpcFirstTalk(L2Npc npc, Player activeChar) {
        _npc = npc;
        _activeChar = activeChar;
    }

    public L2Npc getNpc() {
        return _npc;
    }

    public Player getActiveChar() {
        return _activeChar;
    }

    @Override
    public EventType getType() {
        return EventType.ON_NPC_FIRST_TALK;
    }
}
