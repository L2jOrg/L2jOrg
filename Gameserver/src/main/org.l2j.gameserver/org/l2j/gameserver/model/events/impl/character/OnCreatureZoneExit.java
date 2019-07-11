package org.l2j.gameserver.model.events.impl.character;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.zone.L2ZoneType;

/**
 * @author UnAfraid
 */
public class OnCreatureZoneExit implements IBaseEvent {
    private final Creature _creature;
    private final L2ZoneType _zone;

    public OnCreatureZoneExit(Creature creature, L2ZoneType zone) {
        _creature = creature;
        _zone = zone;
    }

    public Creature getCreature() {
        return _creature;
    }

    public L2ZoneType getZone() {
        return _zone;
    }

    @Override
    public EventType getType() {
        return EventType.ON_CREATURE_ZONE_EXIT;
    }

}
