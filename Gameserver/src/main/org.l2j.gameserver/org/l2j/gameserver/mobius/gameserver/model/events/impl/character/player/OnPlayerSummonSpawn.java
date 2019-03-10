package org.l2j.gameserver.mobius.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.mobius.gameserver.model.events.EventType;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnPlayerSummonSpawn implements IBaseEvent {
    private final L2Summon _summon;

    public OnPlayerSummonSpawn(L2Summon summon) {
        _summon = summon;
    }

    public L2Summon getSummon() {
        return _summon;
    }

    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_SUMMON_SPAWN;
    }
}
