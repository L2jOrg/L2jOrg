package org.l2j.gameserver.mobius.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.events.EventType;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.IBaseEvent;

/**
 * @author Krunchy
 * @since 2.6.0.0
 */
public class OnPlayerProfessionCancel implements IBaseEvent
{
    private final L2PcInstance _activeChar;
    private final int _classId;

    public OnPlayerProfessionCancel(L2PcInstance activeChar, int classId)
    {
        _activeChar = activeChar;
        _classId = classId;
    }

    public L2PcInstance getActiveChar()
    {
        return _activeChar;
    }

    public int getClassId()
    {
        return _classId;
    }

    @Override
    public EventType getType()
    {
        return EventType.ON_PLAYER_PROFESSION_CANCEL;
    }
}