package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnPlayerLevelChanged implements IBaseEvent {
    private final Player _activeChar;
    private final int _oldLevel;
    private final int _newLevel;

    public OnPlayerLevelChanged(Player activeChar, int oldLevel, int newLevel) {
        _activeChar = activeChar;
        _oldLevel = oldLevel;
        _newLevel = newLevel;
    }

    public Player getActiveChar() {
        return _activeChar;
    }

    public int getOldLevel() {
        return _oldLevel;
    }

    public int getNewLevel() {
        return _newLevel;
    }

    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_LEVEL_CHANGED;
    }
}
