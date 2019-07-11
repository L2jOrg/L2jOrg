package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author Krunchy
 * @since 2.6.0.0
 */
public class OnPlayerProfessionCancel implements IBaseEvent {
    private final Player _activeChar;
    private final int _classId;

    public OnPlayerProfessionCancel(Player activeChar, int classId) {
        _activeChar = activeChar;
        _classId = classId;
    }

    public Player getActiveChar() {
        return _activeChar;
    }

    public int getClassId() {
        return _classId;
    }

    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_PROFESSION_CANCEL;
    }
}