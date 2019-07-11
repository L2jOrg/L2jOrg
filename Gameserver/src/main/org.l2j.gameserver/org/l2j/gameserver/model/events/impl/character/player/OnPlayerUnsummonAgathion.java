package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author Mobius
 */
public class OnPlayerUnsummonAgathion implements IBaseEvent
{
    private final Player _player;
    private final int _agathionId;

    public OnPlayerUnsummonAgathion(Player player, int agathionId)
    {
        _player = player;
        _agathionId = agathionId;
    }

    public Player getPlayer()
    {
        return _player;
    }

    public int getAgathionId()
    {
        return _agathionId;
    }

    @Override
    public EventType getType()
    {
        return EventType.ON_PLAYER_UNSUMMON_AGATHION;
    }
}

