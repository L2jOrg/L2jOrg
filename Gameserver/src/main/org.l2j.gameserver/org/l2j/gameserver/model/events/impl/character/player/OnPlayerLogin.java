package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnPlayerLogin implements IBaseEvent {
    private final Player _activeChar;

    public OnPlayerLogin(Player activeChar) {
        _activeChar = activeChar;
    }

    public Player getPlayer() {
        return _activeChar;
    }

    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_LOGIN;
    }
}
