package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.item.Henna;

/**
 * @author UnAfraid
 */
public class OnPlayerHennaRemove implements IBaseEvent {
    private final Player _activeChar;
    private final Henna _henna;

    public OnPlayerHennaRemove(Player activeChar, Henna henna) {
        _activeChar = activeChar;
        _henna = henna;
    }

    public Player getActiveChar() {
        return _activeChar;
    }

    public Henna getHenna() {
        return _henna;
    }

    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_HENNA_REMOVE;
    }
}
