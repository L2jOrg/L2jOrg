package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnPlayerSummonSpawn implements IBaseEvent {
    private final Summon _summon;

    public OnPlayerSummonSpawn(Summon summon) {
        _summon = summon;
    }

    public Summon getSummon() {
        return _summon;
    }

    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_SUMMON_SPAWN;
    }
}
