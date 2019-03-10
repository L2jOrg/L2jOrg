package org.l2j.gameserver.mobius.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.events.EventType;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.mobius.gameserver.model.items.L2Henna;

/**
 * @author UnAfraid
 */
public class OnPlayerHennaRemove implements IBaseEvent {
    private final L2PcInstance _activeChar;
    private final L2Henna _henna;

    public OnPlayerHennaRemove(L2PcInstance activeChar, L2Henna henna) {
        _activeChar = activeChar;
        _henna = henna;
    }

    public L2PcInstance getActiveChar() {
        return _activeChar;
    }

    public L2Henna getHenna() {
        return _henna;
    }

    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_HENNA_REMOVE;
    }
}
