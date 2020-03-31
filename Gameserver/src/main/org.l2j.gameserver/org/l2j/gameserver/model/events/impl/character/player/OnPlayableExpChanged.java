package org.l2j.gameserver.model.events.impl.character.player;


import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnPlayableExpChanged implements IBaseEvent {
    private final Playable _activeChar;
    private final long _oldExp;
    private final long _newExp;

    public OnPlayableExpChanged(Playable activeChar, long oldExp, long newExp) {
        _activeChar = activeChar;
        _oldExp = oldExp;
        _newExp = newExp;
    }

    public Playable getPlayable() {
        return _activeChar;
    }

    public long getOldExp() {
        return _oldExp;
    }

    public long getNewExp() {
        return _newExp;
    }

    @Override
    public EventType getType() {
        return EventType.ON_PLAYABLE_EXP_CHANGED;
    }
}
